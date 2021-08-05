package com.example.remembermedicine

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.database.getLongOrNull
import kotlinx.android.synthetic.main.activity_show_medicine.*
import java.util.*

class showMedicine : AppCompatActivity() {

    var idRegister: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_medicine)

        idRegister = getIntent().getExtras()?.getInt("id")!!

        update()

        // llenar la con los datos
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from medicamentos where id='${idRegister}'", null)
        if (fila.moveToFirst()) {

            medicamento.text = fila.getString(1)

            dosis.text = "Dosis ${fila.getString(2)}"

            lapso.text = "Cada "

            if (fila.getInt(3) != 0)
                lapso.text =  "${lapso.text} ${fila.getInt(3)} dias"
            else if (fila.getInt(4) != 0)
                lapso.text = "${lapso.text} ${fila.getInt(4)} horas"
            else if (fila.getInt(5) != 0)
                lapso.text = "${lapso.text} ${fila.getInt(5)} minutos"

            descripcion.text = fila.getString(6)

            when (fila.getString(7))
            {
                "Pastillas" -> tipo.setImageResource(R.drawable.pills_free)
                "Capsulas" -> tipo.setImageResource(R.drawable.capsulas)
                "Inalador" -> tipo.setImageResource(R.drawable.inalador_free)
                "Gotas" -> tipo.setImageResource(R.drawable.gotas)
                "Crema untable" -> tipo.setImageResource(R.drawable.gel_untable)
                "Gel consumible" -> tipo.setImageResource(R.drawable.gel_consumible)
                "Inyección" -> tipo.setImageResource(R.drawable.injeccion)
                "Ampolletas" -> tipo.setImageResource(R.drawable.ampolleta)
                "Inalador via nazal" -> tipo.setImageResource(R.drawable.inalador_nazal)
            }

            tipo.contentDescription = fila.getString(7)

            if (fila.getInt(9).equals(1))
                does.isEnabled = true
        }
        bd.close()

        back.setOnClickListener {
            finish()
            val otherScreen = Intent(this, MainActivity::class.java)
            startActivity(otherScreen)
        }

        edit.setOnClickListener {
            finish()
            val otherScreen = Intent(this, to_Register::class.java)
            otherScreen.putExtra("edit", true)
            otherScreen.putExtra("idRegister", idRegister)
            startActivity(otherScreen)
        }

        delete.setOnClickListener {

            val i = Intent(this,MyReceiverNotification::class.java)
            i.setAction("send.info")
            i.putExtra("id",idRegister)
            val alarmService = getSystemService(Context.ALARM_SERVICE) as AlarmManager?

            val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext, idRegister, i, 0)
            alarmService!!.cancel(pendingIntent)
            pendingIntent.cancel()

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase

            val fila = bd.rawQuery("select fechaConsumo from medicamentos where id='${idRegister}'", null)

            fila.moveToFirst()

            val cant = bd.delete("medicamentos", "id= '${idRegister}'", null)
            bd.close()

            if (cant == 1)
                Toast.makeText(this, "Se borró el Medicamento", Toast.LENGTH_SHORT)
                    .show()

            finish()
            val otherScreen = Intent(this, MainActivity::class.java)
            startActivity(otherScreen)
        }

        does.setOnClickListener {

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

            val intent = Intent(this,MyReceiverNotification::class.java)
            intent.setAction("send.info")
            intent.putExtra("id", fila.getInt(0))
            intent.putExtra("name", fila.getString(1))
            intent.putExtra("description", fila.getString(6))
            val penddingIntent = PendingIntent.getBroadcast(this, idRegister, intent, 0)


            val alarmService = this.getSystemService(ALARM_SERVICE) as AlarmManager?

            val date = Calendar.getInstance()

            fila.getInt(3)?.let { it1 -> date.add(Calendar.DAY_OF_YEAR, it1) }
            fila.getInt(4)?.let { it1 -> date.add(Calendar.HOUR_OF_DAY, it1) }
            fila.getInt(5)?.let { it1 -> date.add(Calendar.MINUTE, it1) }

            alarmService?.set(AlarmManager.RTC_WAKEUP, date.time.time, penddingIntent)

            does.isEnabled = false

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()

            registro.put("fechaConsumo", date.time.time)
            registro.put("tomar", 0) // medicamento a tomar

            bd.update("medicamentos", registro, "id= '${idRegister}'", null)
            bd.close()

            finish()

            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }

        }

    //Toast.makeText(this, "No existe un Cliente con dicho nombre", Toast.LENGTH_SHORT).show()
    }

    fun update () {
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from medicamentos where id='${idRegister}'", null)
        fila.moveToFirst()
        // active buton
        val date = Calendar.getInstance()
        if (date.time.time.toString() >= fila!!.getLongOrNull(8).toString()) {
            val registro = ContentValues()
            registro.put("tomar", 1) // medicamento a tomar
            bd.update("medicamentos", registro, "id= '${fila.getInt(0)}'", null)
        }
        bd.close()

        return
    }
}