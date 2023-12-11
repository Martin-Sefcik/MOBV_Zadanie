package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.PreferenceData
import eu.mcomputing.mobv.zadanie.databinding.FragmentMapBinding
import eu.mcomputing.mobv.zadanie.databinding.FragmentOthersProfileBinding
import eu.mcomputing.mobv.zadanie.viewmodels.OtherProfileViewModel
import eu.mcomputing.mobv.zadanie.widgets.BottomBar
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class OthersProfileFragment : Fragment(R.layout.fragment_others_profile) {
    private lateinit var binding: FragmentOthersProfileBinding
    private lateinit var otherProfileViewModel: OtherProfileViewModel
    private lateinit var pointAnnotationManager: PointAnnotationManager
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
        binding = FragmentOthersProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = otherProfileViewModel
        }.also { bnd ->
            pointAnnotationManager = bnd.mapView.annotations.createPointAnnotationManager()

            otherProfileViewModel.loadUser()

            otherProfileViewModel.otherProfileResult.observe(viewLifecycleOwner) {
                val userPhoto = it!!.photo

                val imageView = view.findViewById<ImageView>(R.id.photo)
                var image: Bitmap? = null
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    if (userPhoto == "") {
                        imageView.setImageResource(R.drawable.ic_anonym)
                    } else {
                        val imageURL = "https://upload.mcomputing.eu/$userPhoto"

                        try {
                            val `in` = java.net.URL(imageURL).openStream()
                            image = BitmapFactory.decodeStream(`in`)

                            handler.post {
                                imageView.setImageBitmap(image)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }



                val point = Point.fromLngLat(it.lon,it.lat)
                binding.mapView.getMapboxMap()
                    .setCamera(CameraOptions.Builder().center(point).zoom(16.0).build())
                addMarker(point)

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

    private fun addMarker(point: Point) {
        pointAnnotationManager.deleteAll()
            val bitmap = bitmapFromDrawableRes(requireContext(), R.drawable.ic_marker)
            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(bitmap!!)
            pointAnnotationManager.create(pointAnnotationOptions)

    }

}