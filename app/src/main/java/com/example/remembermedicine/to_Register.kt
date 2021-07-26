package com.example.remembermedicine

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_show_medicine.*
import kotlinx.android.synthetic.main.activity_to__register.*
import kotlinx.android.synthetic.main.custom_list.*

class to_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to__register)

        val lista = arrayOf("Pastillas", "Capsulas", "Inalador", "Gotas", "Crema untable", "Gel consumible", "Inyección")
        val adaptador1 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lista)
        spinner.adapter = adaptador1

        val edit = getIntent().getExtras()?.getBoolean("edit",false)
        val idRegister = getIntent().getExtras()?.getInt("idRegister")

        if (edit == true) {

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val fila = bd.rawQuery("select * from medicamentos where id='${idRegister}'", null)

            if (fila.moveToFirst()) {

                editText1.setText(fila.getString(1))

                editText2.setText(fila.getString(2))

                if (fila.getInt(3) != 0)
                    dias.setText(fila.getInt(3).toString())
                else if (fila.getInt(4) != 0)
                    horas.setText(fila.getInt(4).toString())
                else if (fila.getInt(5) != 0)
                    minutos.setText(fila.getInt(5).toString())

                editText4.setText(fila.getString(6))

                spinner.setSelection(lista.indexOf(fila.getString(7).toString()))

            }

            bd.close()

        }

        boton1.setOnClickListener {

            if (edit == true){

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

                val cant = bd.update("medicamentos", registro, "id= '${idRegister}'", null)
                bd.close()

                editText1.setText("")
                editText2.setText("")
                editText4.setText("")
                dias.setText("")
                horas.setText("")
                minutos.setText("")

                if (cant == 1)
                    Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "No existe el medicamento", Toast.LENGTH_SHORT).show()

                finish()

                val otherScreen = Intent(this, showMedicine::class.java)
                otherScreen.putExtra("id",idRegister)
                startActivity(otherScreen)

            } else {


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
                Toast.makeText(this, "Se cargaron los datos del medicamento", Toast.LENGTH_SHORT).show()

                finish()

                val otherScreen = Intent(this, MainActivity::class.java)
                startActivity(otherScreen)
            }
        }
    }
}