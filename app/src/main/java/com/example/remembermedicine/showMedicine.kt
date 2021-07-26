package com.example.remembermedicine

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_medicine.*

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

            if (fila.getInt(3) != 0 && fila.getInt(4) != 0 && fila.getInt(5) != 0)
                lapso.text = "Cada ${fila.getInt(3)} dias, ${fila.getInt(4)} horas, ${fila.getInt(5)} minutos."
            else if (fila.getInt(4) != 0 && fila.getInt(5) != 0)
                lapso.text = "Cada ${fila.getInt(4)} horas, ${fila.getInt(5)} minutos."
            else if (fila.getInt(5) != 0)
                lapso.text = "Cada ${fila.getInt(5)} minutos."

            descripcion.text = fila.getString(6)

            when (fila.getString(7))
            {
                "Pastillas" -> tipo.setImageResource(R.drawable.pills_free)
                "Capsulas" ->  tipo.setImageResource(R.drawable.capsulas_free)
                "Inalador" -> tipo.setImageResource(R.drawable.inalador_free)
                "Gotas" -> tipo.setImageResource(R.drawable.gotas)
                "Crema untable" -> tipo.setImageResource(R.drawable.gel_untable)
                "Gel consumible" -> tipo.setImageResource(R.drawable.gel_consumible)
                "Inyección" -> tipo.setImageResource(R.drawable.injeccion)
            }

            tipo.contentDescription = fila.getString(7)

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

    //Toast.makeText(this, "No existe un Cliente con dicho nombre", Toast.LENGTH_SHORT).show()
    }
}