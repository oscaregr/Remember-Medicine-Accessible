package com.example.remembermedicine

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_show_medicine.*
import java.util.*

class showMedicine : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_medicine)

        val idRegister = getIntent().getExtras()?.getInt("id")

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
                "Capsulas" ->  tipo.setImageResource(R.drawable.capsulas)
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
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
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
            does.isEnabled = false
            val date = Calendar.getInstance()

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()

            fila.getInt(3)?.let { it1 -> date.add(Calendar.DAY_OF_YEAR, it1) }
            fila.getInt(4)?.let { it1 -> date.add(Calendar.HOUR_OF_DAY, it1) }
            fila.getInt(5)?.let { it1 -> date.add(Calendar.MINUTE, it1) }

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
}