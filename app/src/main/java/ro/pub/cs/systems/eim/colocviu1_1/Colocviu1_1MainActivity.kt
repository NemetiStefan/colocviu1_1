package ro.pub.cs.systems.eim.colocviu1_1

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Colocviu1_1MainActivity : AppCompatActivity() {

    private lateinit var directionsText: EditText
    private lateinit var pressesCount: TextView
    private val directions = mutableListOf<String>()

    private var count = 0

    private var serviceStarted = false


    private val secondaryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val message = "REGISTERED: ${directions.joinToString(", ")}"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

            directions.clear()
            count = 0
            updateUi()
        } else {
            Toast.makeText(this, "CANCELLED", Toast.LENGTH_SHORT).show()

        }
    }

    private inner class DirectionsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val message = intent.getStringExtra(Constants.EXTRA_BROADCAST_MESSAGE)
            Log.d(Constants.TAG, "$message")

        }
    }

    private val receiver = DirectionsReceiver()


    private val filter = IntentFilter(Constants.ACTION_DIRECTIONS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_colocviu1_1_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        directionsText = findViewById(R.id.directions_text)
        pressesCount = findViewById(R.id.presses_count)

        savedInstanceState?.let {
            directions.addAll(it.getStringArrayList(Constants.DIRECTIONS_KEY) ?: arrayListOf())
            count = it.getInt(Constants.COUNT_KEY, 0)
           serviceStarted = it.getBoolean(Constants.SERVICE_STARTED_KEY, false)
        }
        updateUi()

        val directionListener = View.OnClickListener { view ->
            directions.add((view as Button).text.toString())
            count++
            updateUi()
            checkAndStartService()
        }
        for (id in intArrayOf(R.id.north_button, R.id.south_button, R.id.east_button, R.id.west_button)) {
            findViewById<Button>(id).setOnClickListener(directionListener)
        }

        findViewById<Button>(R.id.navigate_button).setOnClickListener {
            val intent = Intent(this, Colocviu1_1SecondaryActivity::class.java)
            intent.putExtra(Constants.EXTRA_DIRECTIONS, directions.joinToString(", "))
            secondaryLauncher.launch(intent)
        }

    }

    private fun updateUi() {
        directionsText.setText(directions.joinToString(", "))
        pressesCount.text = count.toString()

    }

    private fun checkAndStartService() {
        if (count >= Constants.PRESS_THRESHOLD && !serviceStarted) {
            val intent = Intent(this, Colocviu_1_1Service::class.java)
            intent.putExtra(Constants.EXTRA_DIRECTIONS, directions.joinToString(", "))
            startService(intent)
            serviceStarted = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(Constants.DIRECTIONS_KEY, ArrayList(directions))
        outState.putInt(Constants.COUNT_KEY, count)
        outState.putBoolean(Constants.SERVICE_STARTED_KEY, serviceStarted)
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }


    override fun onDestroy() {
        if (isFinishing) {
            stopService(Intent(this, Colocviu_1_1Service::class.java))
        }
        super.onDestroy()
    }
}