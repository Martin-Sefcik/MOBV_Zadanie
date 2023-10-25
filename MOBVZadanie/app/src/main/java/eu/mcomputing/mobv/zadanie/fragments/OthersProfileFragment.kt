package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import eu.mcomputing.mobv.zadanie.R

class OthersProfileFragment : Fragment(R.layout.fragment_others_profile) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //view.findViewById<BottomBar>(R.id.bottom_bar).setActive(BottomBar.PROFILE)

//        view.findViewById<Button>(R.id.changePasswordButton).apply {
//            setOnClickListener {
//                findNavController().navigate(R.id.profile_to_changePassword)
//            }
//        }

    }
}