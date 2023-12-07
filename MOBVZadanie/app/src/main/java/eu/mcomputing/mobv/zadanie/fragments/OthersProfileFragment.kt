package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import eu.mcomputing.mobv.zadanie.R

class OthersProfileFragment : Fragment(R.layout.fragment_others_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val userName = arguments?.getString("Name")
        Log.d("Another profile name:", userName ?: "",)


    }
}