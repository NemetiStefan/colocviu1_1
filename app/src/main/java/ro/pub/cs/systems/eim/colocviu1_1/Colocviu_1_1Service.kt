package ro.pub.cs.systems.eim.colocviu1_1

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Colocviu_1_1Service : Service() {
    @Volatile private var running = false
    private var thread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val directions = intent?.getStringExtra(Constants.EXTRA_DIRECTIONS).orEmpty()
        Log.d(Constants.TAG, "service started with: $directions")

        thread?.interrupt()
        running = true
        thread = Thread {
            while (running) {
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val broadcast = Intent(Constants.ACTION_DIRECTIONS)
                broadcast.setPackage(packageName)
                broadcast.putExtra(Constants.EXTRA_BROADCAST_MESSAGE, "$time - $directions")
                sendBroadcast(broadcast)
                try {
                    Thread.sleep(Constants.INTERVAL_MS)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
        thread?.start()
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        running = false
        thread?.interrupt()
        super.onDestroy()
    }
}