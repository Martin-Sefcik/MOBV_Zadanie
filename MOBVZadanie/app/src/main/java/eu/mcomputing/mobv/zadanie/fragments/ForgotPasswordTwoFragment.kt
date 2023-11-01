package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentForgotPasswordBinding
import eu.mcomputing.mobv.zadanie.databinding.FragmentForgotPasswordTwoBinding

class ForgotPasswordTwoFragment : Fragment(R.layout.fragment_forgot_password_two) {
    private var binding: FragmentForgotPasswordTwoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordTwoBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.confirmButton.apply {
                setOnClickListener {
                    findNavController().navigate(R.id.forgetPasswordTwo_to_login)
                }
            }
        }

    }
}