package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.databinding.FragmentProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.ProfileViewModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.LocationServices
import android.location.Location
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.GeofencingRequest
import eu.mcomputing.mobv.zadanie.broadcastReceivers.GeofenceBroadcastReceiver
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel
import eu.mcomputing.mobv.zadanie.workers.MyWorker
import java.util.concurrent.TimeUnit
import androidx.work.Constraints
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import eu.mcomputing.mobv.zadanie.viewmodels.OtherProfileViewModel


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var viewModelAuth: AuthViewModel
    private lateinit var otherProfileViewModel: OtherProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private val PERMISSIONS_REQUIRED = when {
        Build.VERSION.SDK_INT >= 33 -> { // android 13
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        Build.VERSION.SDK_INT >= 29 -> { // android 10
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

        else -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
        }

    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[ProfileViewModel::class.java]

        viewModelAuth = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance(requireContext())) as T
            }
        })[AuthViewModel::class.java]

        otherProfileViewModel =
            ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OtherProfileViewModel(DataRepository.getInstance(requireContext())) as T
                }
            })[OtherProfileViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            bnd.bottomBar.setActive(BottomBar.PROFILE)
            pointAnnotationManager = bnd.mapView.annotations.createPointAnnotationManager()
//            bnd.loadProfileBtn.setOnClickListener {
            val user = PreferenceData.getInstance().getUser(requireContext())
            user?.let {
                viewModel.loadUser(it.id)
            }
//            }

            bnd.logoutBtn.setOnClickListener {
//                viewModel.logoutUser()
                viewModelAuth.logoutUser()
                PreferenceData.getInstance().clearData(requireContext())

                it.findNavController().navigate(R.id.action_profile_intro)
            }
//            viewModel.profileResult.observe(viewLifecycleOwner) {
//                if (it.isNotEmpty()) {
//                    Snackbar.make(
//                        bnd.loadProfileBtn,
//                        it,
//                        Snackbar.LENGTH_SHORT
//                    ).show()
//                }
//            }
            bnd.changePasswordButton.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.profile_to_changePassword)
                }
            }
            bnd.locationSwitch.isChecked = PreferenceData.getInstance().getSharing(requireContext())

            bnd.locationSwitch.setOnCheckedChangeListener { _, checked ->
                Log.d("ProfileFragment", "sharing je $checked")
                if (checked) {
                    turnOnSharing()
                } else {
                    turnOffSharing()
                }
            }

//            otherProfileViewModel.uid.value = user!!.id
//            otherProfileViewModel.loadUser()
//
//            otherProfileViewModel.otherProfileResult.observe(viewLifecycleOwner) {
//                val point = Point.fromLngLat(it!!.lon, it.lat)
//                binding.mapView.getMapboxMap()
//                    .setCamera(CameraOptions.Builder().center(point).zoom(16.0).build())
//                addMarker(point)
//            }
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

    private fun addMarker(point: Point) {
        pointAnnotationManager.deleteAll()
        val bitmap = bitmapFromDrawableRes(requireContext(), R.drawable.ic_marker)
        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(bitmap!!)
        pointAnnotationManager.create(pointAnnotationOptions)

    }

    @SuppressLint("MissingPermission")
    private fun turnOnSharing() {
        Log.d("ProfileFragment", "turnOnSharing")
        if (!hasPermissions(requireContext())) {
            Log.d("ProfileFragment", "0")
            binding.locationSwitch.isChecked = false
            for (p in PERMISSIONS_REQUIRED) {
                requestPermissionLauncher.launch(p)
            }
            return
        }
        Log.d("ProfileFragment", "1")
        PreferenceData.getInstance().putSharing(requireContext(), true)
        Log.d("ProfileFragment", "2")
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Log.d("ProfileFragment", "3")
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) {
            // Logika pre prácu s poslednou polohou
            Log.d("ProfileFragment", "poloha posledna ${it ?: "-"}")
            if (it == null) {
                Log.e("ProfileFragment", "poloha neznama geofence nevytvoreny")
            } else {
                setupGeofence(it)
            }
        }
    }

    private fun turnOffSharing() {
        Log.d("ProfileFragment", "turnOffSharing")
        PreferenceData.getInstance().putSharing(requireContext(), false)
        removeGeofence()
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofence(location: Location) {

        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val geofence = Geofence.Builder()
            .setRequestId("my-geofence")
            .setCircularRegion(location.latitude, location.longitude, 100f) // 100m polomer
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent =
            PendingIntent.getBroadcast(
                requireActivity(),
                0,
                intent,
//                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE

                PendingIntent.FLAG_MUTABLE
//                PendingIntent.FLAG_UPDATE_CURRENT
            )

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                // Geofences boli úspešne pridané
                Log.d("ProfileFragment", "geofence vytvoreny")
                viewModel.updateGeofence(location.latitude, location.longitude, 100.0)
                runWorker()
            }
            addOnFailureListener {
                // Chyba pri pridaní geofences
                it.printStackTrace()
                binding.locationSwitch.isChecked = false
                PreferenceData.getInstance().putSharing(requireContext(), false)
            }
        }
    }

    private fun removeGeofence() {
        Log.d("ProfileFragment", "geofence zruseny")
        val geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        geofencingClient.removeGeofences(listOf("my-geofence"))
        viewModel.removeGeofence()
        cancelWorker()
    }

    private fun runWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(
            15, TimeUnit.MINUTES, // repeatInterval
            5, TimeUnit.MINUTES // flexInterval
        )
            .setConstraints(constraints)
            .addTag("myworker-tag")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myworker",
            ExistingPeriodicWorkPolicy.KEEP, // or REPLACE
            repeatingRequest
        )
    }

    private fun cancelWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("myworker")
    }
}