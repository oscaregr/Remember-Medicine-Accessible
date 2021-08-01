package com.example.remembermedicine

//import kotlinx.android.synthetic.main.activity_show_medicine.*

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_to__register.*
import java.util.*


class to_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to__register)

        val lista = arrayOf(
            "Pastillas",
            "Capsulas",
            "Inalador",
            "Gotas",
            "Crema untable",
            "Gel consumible",
            "Inyección"
        )
        val adaptador1 = ArrayAdapter<String>(this, R.layout.options_item, lista)
        spinner.adapter = adaptador1



        val edit = getIntent().getExtras()?.getBoolean("edit", false)
        val idRegister = getIntent().getExtras()?.getInt("idRegister")

        if (edit == true) {

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val fila = bd.rawQuery("select * from medicamentos where id='${idRegister}'", null)

            if (fila.moveToFirst()) {

                nombre.editText?.setText(fila.getString(1))

                editText2.setText(fila.getString(2))

                if (fila.getInt(3) != 0)
                    dias.editText?.setText(fila.getInt(3).toString())
                else if (fila.getInt(4) != 0)
                    horas.editText?.setText(fila.getInt(4).toString())
                else if (fila.getInt(5) != 0)
                    minutos.editText?.setText(fila.getInt(5).toString())

                editText4.setText(fila.getString(6))

                spinner.setSelection(lista.indexOf(fila.getString(7).toString()))

            }

            bd.close()

        }

        boton1.setOnClickListener {
            doWhenIsCorrect(edit, idRegister)
        }
    }

    fun validedNoEmpty(editTxt: TextInputLayout): Boolean {

        if (editTxt.editText?.text.toString().trim().isEmpty()) {
             editTxt.error= "El campo no puede quedar vacio"
            return false
        } else {
            editTxt.error = null
            return true
        }
    }

    fun doWhenIsCorrect(edit: Boolean?, idRegister: Int?) {

        if (!validedNoEmpty(nombre))
            return

        if (edit == true){

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()

            registro.put("nombre", nombre.editText?.text.toString())
            registro.put("dosis", editText2.getText().toString())
            registro.put("dias", dias.editText?.text.toString().toIntOrNull())
            registro.put("horas", horas.editText?.text.toString().toIntOrNull())
            registro.put("minutos", minutos.editText?.text.toString().toIntOrNull())
            registro.put("descripcion", editText4.getText().toString())
            registro.put("tipo", spinner.selectedItem.toString())

            val cant = bd.update("medicamentos", registro, "id= '${idRegister}'", null)
            bd.close()

            if (cant == 1)
                Toast.makeText(this, "Se modificaron los datos", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "No existe el medicamento", Toast.LENGTH_SHORT).show()

            finish()

            val otherScreen = Intent(this, showMedicine::class.java)
            otherScreen.putExtra("id", idRegister)
            startActivity(otherScreen)

        } else {

            val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
            val bd = admin.writableDatabase
            val registro = ContentValues()
            val c = Calendar.getInstance()

            // sumar la el tiepo a definido a la fecha actual para genrear la fecha de alarma
            dias.editText?.text.toString().toIntOrNull()?.let { it1 -> c.add(
                Calendar.DAY_OF_YEAR,
                it1
            ) }
            horas.editText?.text.toString().toIntOrNull()?.let { it1 -> c.add(
                Calendar.HOUR_OF_DAY,
                it1
            ) }
            minutos.editText?.text.toString().toIntOrNull()?.let { it1 -> c.add(
                Calendar.MINUTE,
                it1
            ) }

            registro.put("nombre", nombre.editText?.text.toString())
            registro.put("dosis", editText2.getText().toString())
            registro.put("dias", dias.editText?.text.toString().toIntOrNull())
            registro.put("horas", horas.editText?.text.toString().toIntOrNull())
            registro.put("minutos", minutos.editText?.text.toString().toIntOrNull())
            registro.put("descripcion", editText4.getText().toString())
            registro.put("tipo", spinner.selectedItem.toString())
            registro.put("fechaConsumo", c.time.time)
            registro.put("tomar", 1)

            bd.insert("medicamentos", null, registro)

            val fila = bd.rawQuery("select * from medicamentos", null)
            fila.moveToLast()
            val idMedicine = fila.getInt(0)

            fila.close()
            bd.close()

            Toast.makeText(this, "Se cargaron los datos del medicamento", Toast.LENGTH_SHORT).show()

            finish()

            val otherScreen = Intent(this, showMedicine::class.java)
            otherScreen.putExtra("id", idMedicine)
            startActivity(otherScreen)
        }
    }
}