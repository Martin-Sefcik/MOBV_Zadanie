package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kód z Activity
        view.findViewById<Button>(R.id.submitButton).apply {
            setOnClickListener {
                // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
                val input_username: String = findViewById<EditText>(R.id.editText1).text.toString()
                val input_password: String = findViewById<EditText>(R.id.editText2).text.toString()

                // Spracovanie dát alebo iné akcie
            }
        }
    }
}