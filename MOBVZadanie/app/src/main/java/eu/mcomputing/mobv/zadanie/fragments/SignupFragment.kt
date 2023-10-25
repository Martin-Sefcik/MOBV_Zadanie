package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.data.api.DataRepository
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel


class SignupFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var viewModel: AuthViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        viewModel.registrationResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                requireView().findNavController().navigate(R.id.action_signup_feed)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.submitButton),
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
                viewModel.registerUser(
                    view.findViewById<EditText>(R.id.editText2).text.toString(),
                    view.findViewById<EditText>(R.id.editText1).text.toString(),
                    view.findViewById<EditText>(R.id.editText3).text.toString()
                )
            }
        }


        // Kód z Activity
//        view.findViewById<Button>(R.id.submitButton).apply {
//        setOnClickListener {
//            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
//            val input_email: String = view.findViewById<EditText>(R.id.editText1).text.toString()
//            val input_username: String = view.findViewById<EditText>(R.id.editText2).text.toString()
//            val input_password: String = view.findViewById<EditText>(R.id.editText3).text.toString()
//            val input_password_again: String =
//                view.findViewById<EditText>(R.id.editText4).text.toString()
//
//            findNavController().navigate(R.id.action_signup_login)
//
//            // Spracovanie dát alebo iné akcie
//            }
//        }
    }
}