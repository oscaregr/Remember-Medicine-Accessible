package com.example.remembermedicine

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_to__register.*

class to_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to__register)

        val lista = arrayOf("Pastillas", "Capsulas", "Inalador", "Gotas", "Crema untable", "Gel consumible", "Inyección")
        val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista)
        spinner.adapter = adaptador1


        boton1.setOnClickListener {

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()
            registro.put("nombre", editText1.getText().toString())
            registro.put("dosis", editText2.getText().toString())
            registro.put("dias", dias.text.toString().toIntOrNull())
            registro.put("horas", horas.text.toString().toIntOrNull())
            registro.put("minutos", minutos.text.toString().toIntOrNull())
            registro.put("descripcion", editText4.getText().toString())
            registro.put("tipo", spinner.selectedItem.toString())

            bd.insert("medicamentos", null, registro)
            bd.close()
            editText1.setText("")
            editText2.setText("")
            editText4.setText("")
            dias.setText("")
            horas.setText("")
            minutos.setText("")
            Toast.makeText(this, "Se cargaron los datos del Cliente", Toast.LENGTH_SHORT).show()

            finish()

            val otherScreen = Intent(this, MainActivity::class.java)
            startActivity(otherScreen)
        }


        boton3.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()
            registro.put("dosis", editText2.text.toString())
            registro.put("descripcion", editText4.text.toString())
            val cant = bd.update("medicamentos", registro, "nombre= '${editText1.text.toString()}'", null)
            bd.close()
            if (cant == 1)
                Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "No existe un Cliente con el nombre ingresado", Toast.LENGTH_SHORT).show()
        }


    }
}