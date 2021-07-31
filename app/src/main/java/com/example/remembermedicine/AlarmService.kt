package com.example.remembermedicine

import android.app.*
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.database.getLongOrNull
import java.util.*


class AlarmService : Service() {

    private var crash:Boolean = false
    private var update:Boolean = false

    override fun onBind(intent: Intent): IBinder? {
       return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    fun refresh(bd: SQLiteDatabase): Cursor? {
        // optener base de datos
        val resultados = bd.rawQuery("select * from medicamentos where tomar=0", null)
        return resultados
    }

    fun AlarmDetect(datos: Cursor?, bd: SQLiteDatabase, r: Ringtone) {

        if (datos?.count!! > 0) {
            datos!!.moveToFirst()
            val date = Calendar.getInstance()

            if (date.time.time.toString() >= datos!!.getLongOrNull(8).toString()) {
                startNotification(datos)
                startAlarm(datos!!.getInt(0), bd, r)
            }

            while(datos!!.moveToNext()){
                if (date.time.time.toString() >= datos!!.getLongOrNull(8).toString()) {
                    startNotification(datos)
                    startAlarm(datos!!.getInt(0), bd, r)
                }
            }
        } else {
            bd.close()
            crash = true
        }

        return
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        crash = false
        update = false

        val r = RingtoneManager.getRingtone(
                getApplicationContext(), RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_RINGTONE
        )
        )

        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase

        var datosActualizados = refresh(bd)

        /// calis code
        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                AlarmDetect(datosActualizados, bd, r)
                //super.handleMessage(msg)
                //Toast.makeText(this, "5 secs has passed", Toast.LENGTH_SHORT).show()
            }
        }



        Thread {
            // TODO Auto-generated method stub
            while (!crash) {
                try {
                    Thread.sleep(1)
                    if (update){
                        datosActualizados = refresh(bd)
                        update = !update
                    }
                    handler.sendEmptyMessage(0)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }.start()

        stopSelf()
        onDestroy()

        return super.onStartCommand(intent, flags, startId)
    }

    fun startAlarm(int: Int, bd: SQLiteDatabase, r: Ringtone) {
        // sonido de alarma



        val registro = ContentValues()
        registro.put("tomar", 1) // medicamento a tomar
        bd.update("medicamentos", registro, "id= '${int}'", null)

        update = !update

        r.play()

        val mHandler = Handler(Looper.getMainLooper())
        mHandler.postDelayed({
            try {
                r.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 30 * 1000)

    }

    //crear notificacion
    fun startNotification(resultados: Cursor?) {

        // activity to start
        val resultIntent = Intent(this, showMedicine::class.java)
        resultIntent.putExtra("id", resultados?.getInt(0))
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        /// create notification
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
        builder.setContentTitle(resultados?.getString(1))
        builder.setContentText(resultados?.getString(6))
        builder.setAutoCancel(true)
        builder.setContentIntent(resultPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.icon_medicine);
            builder.setColor(ContextCompat.getColor(this, R.color.white))
        } else {
            builder.setSmallIcon(R.mipmap.icon_medicine);
        }

        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(1, builder.build())

        return
    }

    override fun onDestroy () {
        super.onDestroy()
    }

}