package eu.mcomputing.mobv.zadanie.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import eu.mcomputing.mobv.zadanie.R
import java.util.concurrent.Executors

class OthersProfileFragment : Fragment(R.layout.fragment_others_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val userName = arguments?.getString("Name")
        val userPhoto = arguments?.getString("Photo")
        val userUpdated = arguments?.getString("Updated")
        Log.d("Another profile name:", userPhoto ?: "",)

        view.findViewById<TextView>(R.id.tv_Name).text = userName

        view.findViewById<TextView>(R.id.updated).text = "Updated: " + userUpdated


        val imageView = view.findViewById<ImageView>(R.id.photo)
        var image: Bitmap? = null
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            if (userPhoto == ""){
                imageView.setImageResource(R.drawable.ic_anonym)
            } else {
                val imageURL = "https://upload.mcomputing.eu/$userPhoto"

                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)

                    handler.post {
                        imageView.setImageBitmap(image)
                    }
                }

                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    }
}