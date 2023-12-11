package eu.mcomputing.mobv.zadanie.fragments

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
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
            }
        }
    }
}