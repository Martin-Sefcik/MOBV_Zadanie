package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import kotlinx.coroutines.launch
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import eu.mcomputing.mobv.zadanie.viewmodels.FeedViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.OtherProfileViewModel
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private var selectedPoint: CircleAnnotation? = null
    private var lastLocation: Point? = null
    private lateinit var annotationManager: CircleAnnotationManager
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var otherProfileViewModel: OtherProfileViewModel

    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                initLocationComponent()
                addLocationListeners()
            }
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        otherProfileViewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OtherProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[OtherProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = otherProfileViewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.MAP)

            val sharing = PreferenceData.getInstance().getSharing(requireContext())
            if (!sharing) {
                showRecordingDisabledAlert(view)
            } else {
                annotationManager = bnd.mapView.annotations.createCircleAnnotationManager()
                pointAnnotationManager = bnd.mapView.annotations.createPointAnnotationManager()

                val hasPermission = hasPermissions(requireContext())
                onMapReady(hasPermission)

                bnd.myLocation.setOnClickListener {
                    if (!hasPermissions(requireContext())) {
                        requestPermissionLauncher.launch(
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } else {
                        lastLocation?.let {
                            lifecycleScope.launch {
                                refreshLocation(it)
                            }
                        }
                        addLocationListeners()
                    }
                }
            }
        }
    }

    private fun showRecordingDisabledAlert(view: View) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Sharing Disabled")
            .setMessage("Sharing is currently disabled. Do you want to enable it?")
            .setPositiveButton("Enable") { _, _ ->
                view.findNavController().navigate(R.id.feed_to_profile)
//                PreferenceData.getInstance().putSharing(requireContext(), true)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    suspend fun loadUsers(): List<UserEntity>? {
        val users = DataRepository.getInstance(requireContext()).getUsersList()
        return users
    }

    private fun onMapReady(enabled: Boolean) {
        binding.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(14.3539484, 49.8001304))
                .zoom(2.0)
                .build()
        )
        binding.mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            if (enabled) {
                initLocationComponent()
                addLocationListeners()
            }
        }

        binding.mapView.getMapboxMap().addOnMapClickListener {
            if (hasPermissions(requireContext())) {
                onCameraTrackingDismissed()
            }
            true
        }
    }


    private fun initLocationComponent() {
        Log.d("MapFragment", "initLocationComponent")
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
        }

    }

    private fun addLocationListeners() {
        binding.mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        binding.mapView.gestures.addOnMoveListener(onMoveListener)

    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
//        Log.d("MapFragment", "poloha je $it")
        if (lastLocation != it) {
            lifecycleScope.launch {
                refreshLocation(it)
            }
        }
    }

    private suspend fun refreshLocation(point: Point) {
        binding.mapView.getMapboxMap()
            .setCamera(CameraOptions.Builder().center(point).zoom(16.0).build())
        binding.mapView.gestures.focalPoint =
            binding.mapView.getMapboxMap().pixelForCoordinate(point)
        lastLocation = point
        addMarker(point)

        val users = loadUsers()

        for (item in users!!) {
            val p = Point.fromLngLat(item.lon, item.lat)
            addMarkerOtherUsers(p, item.photo, item.uid)
        }
    }

    private fun addMarker(point: Point) {

        if (selectedPoint == null) {
            annotationManager.deleteAll()
            val pointAnnotationOptions = CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(150.0)
                .withCircleOpacity(0.1)
                .withCircleColor("#000")
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")
            selectedPoint = annotationManager.create(pointAnnotationOptions)
        } else {
            selectedPoint?.let {
                it.point = point
                annotationManager.update(it)
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun loadBitmapFromUrl(context: Context, imageUrl: String, callback: (Bitmap?) -> Unit) {
        Picasso.get()
            .load(imageUrl)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    callback(bitmap)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    // Handle the failure if needed
                    callback(null)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            })
    }

    private fun addMarkerOtherUsers(point: Point, photo: String, uid: String) {
        // Load the bitmap from the network using Picasso
        if (photo == "") {
            val bitmap = bitmapFromDrawableRes(requireContext(), R.drawable.ic_anonym)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(bitmap!!)
                .withTextField(uid)
                .withTextOpacity(0.0)
            pointAnnotationManager.create(pointAnnotationOptions)
            pointAnnotationManager.apply {
                addClickListener(
                    OnPointAnnotationClickListener {
                        otherProfileViewModel.uid.value = it.textField
                        Navigation.findNavController(requireView()).navigate(R.id.action_map_otherProfile)

                        true
                    }
                )

            }
        } else {
            loadBitmapFromUrl(
                requireContext(),
                "https://upload.mcomputing.eu/$photo"
            ) { bitmap ->
                if (bitmap != null) {
                    // Use the loaded bitmap to create the marker
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(bitmap)
                        .withTextField(uid)
                        .withIconSize(0.3)
                        .withTextOpacity(0.0)

                    pointAnnotationManager.create(pointAnnotationOptions)

                    pointAnnotationManager.apply {
                        addClickListener(
                            OnPointAnnotationClickListener {
                                otherProfileViewModel.uid.value = it.textField
                                Navigation.findNavController(requireView()).navigate(R.id.action_map_otherProfile)

                                true
                            }
                        )
                    }
                }
            }
        }
    }


    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }


    private fun onCameraTrackingDismissed() {
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.apply {
            location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            gestures.removeOnMoveListener(onMoveListener)
        }
    }

}