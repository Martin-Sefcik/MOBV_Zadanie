package eu.mcomputing.mobv.zadanie

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        val submitButton: Button = findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            // Logika po kliknutí na tlačidlo, napríklad na získanie textu z EditText
            val input_username: String = findViewById<EditText>(R.id.editText1).text.toString()
            val input_password: String = findViewById<EditText>(R.id.editText2).text.toString()

            // Spracovanie dát alebo iné akcie
        }
    }
}