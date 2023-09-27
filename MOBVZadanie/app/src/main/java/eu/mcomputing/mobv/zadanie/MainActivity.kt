package eu.mcomputing.mobv.zadanie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cons_layout)
        //setContentView(R.layout.activity_main)
        Log.d("MojTag", "moje info")

        val myButton: Button = findViewById(R.id.button1)
        myButton.setOnClickListener {
            Log.d("My Buttonn", "moje tlacitko")
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

        val myButton2: Button = findViewById(R.id.button2)
        myButton2.setOnClickListener {
            Log.d("My Buttonn2222", "moje tlacitko2")
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
    }
}