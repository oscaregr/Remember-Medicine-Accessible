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

    var date = arrayListOf<Int>()

    var idRegisterToEdit: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to__register)

        val lista = arrayListOf(R.array.items_type_medicine)

        ArrayAdapter.createFromResource(
                this,
                R.array.items_type_medicine,
                R.layout.options_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(R.layout.options_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val edit = getIntent().getExtras()?.getBoolean("edit", false)

        val idRegister = getIntent().getExtras()?.getInt("idRegister")
        if (idRegister != null) {
            idRegisterToEdit = idRegister
        }


        if (edit == true) {
            showMedicineInfo(lista, idRegister)
            title_template.text = R.string.update.toString()
        }

        back.setOnClickListener {
            finish()
            if (edit == true)
                Intent(this, showMedicine::class.java).also {
                    it.putExtra("id", idRegister)
                    startActivity(it)
                }
            else
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                }

        }

        boton1.setOnClickListener {
            if (edit == true)
                editDataBase(idRegister)
            else
                addToDataBase()
        }
    }

    fun validedNoEmpty(editTxt: TextInputLayout): Boolean {

        if (editTxt.editText?.text.toString().trim().isEmpty()) {
             editTxt.error= "${resources.getString(R.string.inputError)}"
            return false
        } else {
            editTxt.error = null
            return true
        }
    }

    fun showMedicineInfo(lista: MutableList<Int>?, idRegister: Int?) {
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val fila = bd.rawQuery("select * from medicamentos where id='${idRegister}'", null)

        fila.moveToFirst()

        nombre.editText?.setText(fila.getString(1))

        editText2.setText(fila.getString(2))

        if (fila.getInt(3) != 0)
            dias.editText?.setText(fila.getInt(3).toString())
        else if (fila.getInt(4) != 0)
            horas.editText?.setText(fila.getInt(4).toString())
        else if (fila.getInt(5) != 0)
            minutos.editText?.setText(fila.getInt(5).toString())

        date.add(fila.getInt(3))
        date.add(fila.getInt(4))
        date.add(fila.getInt(5))
        date.add(fila.getInt(9))//get if the medicine are on wait for take

        editText4.setText(fila.getString(6))

        spinner.setSelection(lista!!.indexOf(fila.getInt(7)))

        bd.close()
    }

    fun editDataBase(idRegister: Int?) {

        if (!validedNoEmpty(nombre))
            return

        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val registro = ContentValues()

        registro.put("nombre", nombre.editText?.text.toString())
        registro.put("dosis", editText2.getText().toString())
        registro.put("dias", dias.editText?.text.toString().toIntOrNull())
        registro.put("horas", horas.editText?.text.toString().toIntOrNull())
        registro.put("minutos", minutos.editText?.text.toString().toIntOrNull())
        registro.put("descripcion", editText4.getText().toString())
        registro.put("tipo", spinner.selectedItemPosition)

        if (date[3] == 0) {
            val c = Calendar.getInstance()

            dias.editText?.text.also {
                if(it.isNullOrEmpty())
                    dias.editText?.setText("0")
            }
            horas.editText?.text.also {
                if(it.isNullOrEmpty())
                    horas.editText?.setText("0")
            }
            minutos.editText?.text.also {
                if(it.isNullOrEmpty())
                    minutos.editText?.setText("0")
            }

            dias.editText?.text.toString().toIntOrNull()?.let { it1 -> c.set(Calendar.DAY_OF_YEAR, it1) }

            date[0]?.let { it1 -> c.add(Calendar.DAY_OF_YEAR, it1) }

            horas.editText?.text.toString().toIntOrNull()?.let { it1 -> c.set(Calendar.HOUR_OF_DAY, it1) }

            date[1]?.let { it1 -> c.add(Calendar.HOUR_OF_DAY, it1) }

            minutos.editText?.text.toString().toIntOrNull()?.let { it1 -> c.set(Calendar.MINUTE, it1) }

            date[2]?.let { it1 -> c.add(Calendar.MINUTE, it1) }
            registro.put("fechaConsumo", c.time.time)

        }

        registro.put("tomar", 1)

        val cant = bd.update("medicamentos", registro, "id= '${idRegister}'", null)
        bd.close()

        if (cant == 1)
            Toast.makeText(this, "${resources.getString(R.string.dataUpdate)}", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "${resources.getString(R.string.dataNoExist)}", Toast.LENGTH_SHORT).show()

        finish()

        val otherScreen = Intent(this, showMedicine::class.java)
        otherScreen.putExtra("id", idRegister)
        startActivity(otherScreen)
    }

    fun addToDataBase() {

        if (!validedNoEmpty(nombre))
            return

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
        registro.put("tipo", spinner.selectedItemPosition)
        registro.put("fechaConsumo", c.time.time)
        registro.put("tomar", 1)

        bd.insert("medicamentos", null, registro)

        val fila = bd.rawQuery("select * from medicamentos", null)
        fila.moveToLast()
        val idMedicine = fila.getInt(0)

        fila.close()
        bd.close()

        Toast.makeText(this, "${resources.getString(R.string.dataSaved)}", Toast.LENGTH_SHORT).show()

        finish()

        val otherScreen = Intent(this, showMedicine::class.java)
        otherScreen.putExtra("id", idMedicine)
        startActivity(otherScreen)
    }

}