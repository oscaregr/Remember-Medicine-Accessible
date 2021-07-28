package com.example.remembermedicine

import android.app.*
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getLongOrNull
import java.util.*


class AlarmService : Service() {

    override fun onBind(intent: Intent): IBinder? {
       return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // en registro
        //get date actual

        // optener base de datos
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val resultados = bd.rawQuery("select * from medicamentos", null)

        val crashServiceAlarm

        // sonido de alarma
        val r = RingtoneManager.getRingtone(
                getApplicationContext(), RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_RINGTONE
        )
        )

        /// calis code
        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                resultados.moveToFirst()
                val date = Calendar.getInstance()

                if (date.time.time.toString() == resultados.getLongOrNull(8).toString()) {
                    startAlarm(r)
                }

                while(resultados.moveToNext()){
                    if (date.time.time.toString() == resultados.getLongOrNull(8).toString()) {
                        startAlarm(r)
                    }
                }
                super.handleMessage(msg)
                //Toast.makeText(this, "5 secs has passed", Toast.LENGTH_SHORT).show()
            }
        }



        Thread {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    Thread.sleep(1)
                    handler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                    stopSelf()
                }
            }
        }.start()

        // here code

        return super.onStartCommand(intent, flags, startId)
    }

    fun startAlarm(r: Ringtone) {
        r.play()
    }

    //crear notificacion
    fun startNotification(resultados: Cursor) {
        /*var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent

        alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, NotificationReseiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60,
            alarmIntent
        )

         */

        /// posible jenera error
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    "Medicine Notification",
                    "Medicine Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager? =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
            manager!!.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, "Medicine Notification")
        builder.setContentTitle("titulo")
        builder.setContentText("Conetnido")
        builder.setAutoCancel(true)
        //builder.setSmallIcon(imagen);

        //builder.setSmallIcon(imagen);
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(1, builder.build())

    }

    fun cancelNotification () {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val notification = Intent(this, AlarmService::class.java)
        val penddingIntent = PendingIntent.getBroadcast(this, 1, notification, 0)

        alarmManager?.cancel(penddingIntent)
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}