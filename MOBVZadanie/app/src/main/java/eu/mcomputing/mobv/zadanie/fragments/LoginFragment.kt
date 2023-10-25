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

//class SignActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_login)
//
//        val submitButton: Button = findViewById(R.id.submitButton)
//        submitButton.setOnClickListener {
//            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
//            val input_username: String = findViewById<EditText>(R.id.editText1).text.toString()
//            val input_password: String = findViewById<EditText>(R.id.editText2).text.toString()
//
//            // Spracovanie dát alebo iné akcie
//        }
//    }
//}

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var viewModel: AuthViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]

        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
                val username: String =
                    view.findViewById<EditText>(R.id.editText1).text.toString()
                val password: String =
                    view.findViewById<EditText>(R.id.editText2).text.toString()
                login(username, password)
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                requireView().findNavController().navigate(R.id.action_login_feed)
            } else {
                Snackbar.make(
                    view.findViewById(R.id.submitButton),
                    it,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        // Kód z Activity
//        view.findViewById<Button>(R.id.submitButton).apply {
//            setOnClickListener {
//                // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
//                val username: String = view.findViewById<EditText>(R.id.editText1).text.toString()
//                val password: String = view.findViewById<EditText>(R.id.editText2).text.toString()
//
//                login(username, password)
//                // Spracovanie dát alebo iné akcie
//            }
//        }

        view.findViewById<Button>(R.id.forgetLoginButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_to_forgetPassword)
            }
        }
    }

    private fun login(username: String, password: String) {
        viewModel.loginUser(username, password)
    }
//    private fun login(username: String, password: String) {
//        findNavController().navigate(R.id.action_login_map)
//    }
}