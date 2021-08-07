package com.example.remembermedicine

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getLongOrNull
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    var nombre = arrayListOf<String>()
    var description = arrayListOf<String>()
    var image = arrayListOf<String>()
    var id = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recuperar registros
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val resultados = bd.rawQuery("select id, nombre, descripcion, tipo, fechaConsumo from medicamentos", null)

        if (resultados.count > 0){

            resultados.moveToFirst()
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getString(3))

            // active buton on items
            val date = Calendar.getInstance()
            if (date.time.time.toString() >= resultados.getLongOrNull(4).toString()) {
                val registro = ContentValues()
                registro.put("tomar", 1) // medicamento a tomar
                bd.update("medicamentos", registro, "id= '${resultados.getInt(0)}'", null)
            }

            toNextRow(resultados, bd)
        }

        bd.close()

        val admin1 = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd1 = admin1.writableDatabase

        bd1.close()

        agregar.setOnClickListener {
            finish()

           Intent(this, to_Register::class.java).also {
               startActivity(it)
               
           }
        }
    }

    private fun toNextRow(resultados: Cursor, bd: SQLiteDatabase) {
        val date = Calendar.getInstance()

        while(resultados.moveToNext()){
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getString(3))

            if (date.time.time.toString() >= resultados!!.getLongOrNull(4).toString()) {
                val registro = ContentValues()
                registro.put("tomar", 1) // medicamento a tomar
                bd.update("medicamentos", registro, "id= '${resultados.getInt(0)}'", null)
            }
        }
        pintarLista()
    }

    // mandar registros a creacion de lis
    private fun pintarLista() {
        val myListAdapter = MyListAdapter(this, nombre, description, image, id)
        listView.adapter = myListAdapter
        listView.setOnItemClickListener(){ adapterView, view, position, id ->

            val idRegister = this.id.get(position)
            finish()

            val otherScreen = Intent(this, showMedicine::class.java)
            otherScreen.putExtra("id", idRegister)
            startActivity(otherScreen)
        }
    }
}
