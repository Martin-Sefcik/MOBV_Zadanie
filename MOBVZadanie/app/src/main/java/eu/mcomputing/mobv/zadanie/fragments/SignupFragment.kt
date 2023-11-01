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
import eu.mcomputing.mobv.zadanie.databinding.FragmentSignupBinding
import eu.mcomputing.mobv.zadanie.viewmodels.AuthViewModel


class SignupFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var viewModel: AuthViewModel
    private var binding: FragmentSignupBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(DataRepository.getInstance()) as T
            }
        })[AuthViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
        }.also { bnd ->
            viewModel.registrationResult.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    requireView().findNavController().navigate(R.id.action_signup_feed)
                } else {
                    Snackbar.make(
                        bnd.submitButton,
                        it,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
//            bnd.submitButton.apply {
//                setOnClickListener {
//                    viewModel.registerUser(
//                        bnd.editText2.text.toString(),
//                        bnd.editText1.text.toString(),
//                        bnd.editText3.text.toString()
//                    )
//                }
//            }
        }

    }
}