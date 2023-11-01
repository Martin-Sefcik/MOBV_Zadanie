package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.R
import eu.mcomputing.mobv.zadanie.databinding.FragmentIntroBinding

class IntroFragment : Fragment(R.layout.fragment_intro) {
    private var binding: FragmentIntroBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentIntroBinding.bind(view).apply {
            lifecycleOwner = viewLifecycleOwner
        }.also { bnd ->
            bnd.button1.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_to_signup)
                }
            }
            bnd.button2.apply {
                setOnClickListener {
                    it.findNavController().navigate(R.id.action_intro_to_login)
                }
            }
        }

        // Kód z Activity
//        view.findViewById<Button>(R.id.button1).apply {
//            setOnClickListener {
//                it.findNavController().navigate(R.id.action_intro_to_signup)
//            }
//        }
//
//        view.findViewById<Button>(R.id.button2).apply {
//            setOnClickListener {
//                it.findNavController().navigate(R.id.action_intro_to_login)
//            }
//        }
    }
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}