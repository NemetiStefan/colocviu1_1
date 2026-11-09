package ro.pub.cs.systems.eim.colocviu1_1

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Colocviu1_1SecondaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_colocviu1_1_secondary)

        val text = intent.getStringExtra(Constants.EXTRA_DIRECTIONS).orEmpty()
        findViewById<TextView>(R.id.received_directions).text =
            if (text.isEmpty()) "(nicio directie)" else text

        findViewById<Button>(R.id.register_button).setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
        findViewById<Button>(R.id.cancel_button).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

}