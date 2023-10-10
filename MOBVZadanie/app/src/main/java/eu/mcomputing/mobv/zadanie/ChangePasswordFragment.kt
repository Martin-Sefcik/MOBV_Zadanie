package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.confirmButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.changePassword_to_profile)
            }
        }

    }
}