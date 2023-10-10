package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.confirmButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_to_forgetPasswordTwo)
            }
        }

    }
}