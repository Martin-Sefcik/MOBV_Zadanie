package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

//class InputActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_signup)
//
//        val submitButton: Button = findViewById(R.id.submitButton)
//        submitButton.setOnClickListener {
//            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
//            val input_email: String = findViewById<EditText>(R.id.editText1).text.toString()
//            val input_username: String = findViewById<EditText>(R.id.editText2).text.toString()
//            val input_password: String = findViewById<EditText>(R.id.editText3).text.toString()
//            val input_password_again: String = findViewById<EditText>(R.id.editText4).text.toString()
//
//            // Spracovanie dát alebo iné akcie
//        }
//    }
//}

class SignupFragment : Fragment(R.layout.fragment_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kód z Activity
        view.findViewById<Button>(R.id.submitButton).apply {
        setOnClickListener {
            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
            val input_email: String = view.findViewById<EditText>(R.id.editText1).text.toString()
            val input_username: String = view.findViewById<EditText>(R.id.editText2).text.toString()
            val input_password: String = view.findViewById<EditText>(R.id.editText3).text.toString()
            val input_password_again: String =
                view.findViewById<EditText>(R.id.editText4).text.toString()

            findNavController().navigate(R.id.action_signup_feed)

            // Spracovanie dát alebo iné akcie
        }
        }
    }
}