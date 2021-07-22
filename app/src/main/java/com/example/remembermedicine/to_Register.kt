package com.example.remembermedicine

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_to__register.*

class to_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to__register)

        boton1.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()
            registro.put("nombre", editText1.getText().toString())
            registro.put("dosis", editText2.getText().toString())
            registro.put("lapso", editText3.getText().toString())
            registro.put("descripcion", editText4.getText().toString())

            bd.insert("medicamentos", null, registro)
            bd.close()
            editText1.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")
            Toast.makeText(this, "Se cargaron los datos del Cliente", Toast.LENGTH_SHORT).show()
        }


        boton2.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val fila = bd.rawQuery("select dosis, lapso, descripcion, foto from medicamentos where nombre='${editText1.text.toString()}'", null)
            if (fila.moveToFirst()) {
                editText2.setText(fila.getString(0))
                editText3.setText(fila.getString(1))
                editText4.setText(fila.getString(2))
            } else
                Toast.makeText(this, "No existe un Cliente con dicho nombre", Toast.LENGTH_SHORT).show()
            bd.close()
        }

        boton3.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()
            registro.put("dosis", editText2.text.toString())
            registro.put("lapso", editText3.text.toString())
            registro.put("descripcion", editText4.text.toString())
            val cant = bd.update("medicamentos", registro, "nombre= '${editText1.text.toString()}'", null)
            bd.close()
            if (cant == 1)
                Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "No existe un Cliente con el nombre ingresado", Toast.LENGTH_SHORT).show()
        }

        boton4.setOnClickListener {
            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val cant = bd.delete("medicamentos", "nombre= '${editText1.text.toString()}'", null)
            bd.close()
            editText1.setText("")
            editText2.setText("")
            editText3.setText("")
            editText4.setText("")
            if (cant == 1)
                Toast.makeText(this, "Se borró el Cliente con dicho nombre", Toast.LENGTH_SHORT)
                    .show()
            else
                Toast.makeText(this, "No existe un Clienteo con dicho nombre", Toast.LENGTH_SHORT)
                    .show()
        }

    }
}