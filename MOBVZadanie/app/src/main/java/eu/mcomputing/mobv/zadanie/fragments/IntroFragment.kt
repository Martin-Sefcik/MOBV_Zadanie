package eu.mcomputing.mobv.zadanie.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import eu.mcomputing.mobv.zadanie.R

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_intro)
//        //setContentView(R.layout.activity_main)
//        Log.d("MojTag", "moje info")
//
//        val myButton: Button = findViewById(R.id.button1)
//        myButton.setOnClickListener {
//            Log.d("My Buttonn", "moje tlacitko")
//            val intent = Intent(this, InputActivity::class.java)
//            startActivity(intent)
//        }
//
//        val myButton2: Button = findViewById(R.id.button2)
//        myButton2.setOnClickListener {
//            Log.d("My Buttonn2222", "moje tlacitko2")
//            val intent = Intent(this, SignActivity::class.java)
//            startActivity(intent)
//        }
//    }
//}

class IntroFragment : Fragment(R.layout.fragment_intro) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Kód z Activity
        view.findViewById<Button>(R.id.button1).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_intro_to_signup)
            }
        }

        view.findViewById<Button>(R.id.button2).apply {
            setOnClickListener {
                it.findNavController().navigate(R.id.action_intro_to_login)
            }
        }
    }
}