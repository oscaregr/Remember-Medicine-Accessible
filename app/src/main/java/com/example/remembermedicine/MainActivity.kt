package com.example.remembermedicine

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var nombre = arrayListOf<String>()
    var description = arrayListOf<String>()
    var imageId = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // recuperar registros
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val resultados = bd.rawQuery("select nombre, descripcion, foto from medicamentos", null)

        if (resultados.count > 0){

            resultados.moveToFirst()
            nombre.add(resultados.getString(0))
            description.add(resultados.getString(1))
            imageId.add(resultados.getInt(2))
            toNextRow(resultados)
        }

        agregar.setOnClickListener {
            val otherScreen = Intent(this, to_Register::class.java)
            startActivity(otherScreen)
        }

        bd.close()
    }

    private fun toNextRow(resultados: Cursor) {
        while(resultados.moveToNext()){
            nombre.add(resultados.getString(0))
            description.add(resultados.getString(1))
            imageId.add(resultados.getInt(2))
            toNextRow(resultados)
        }
        pintarLista()
    }

    // mandar registros a creacion de lis
    private fun pintarLista() {
        val myListAdapter = MyListAdapter(this,nombre,description,imageId)
        listView.adapter = myListAdapter
        listView.setOnItemClickListener(){adapterView, view, position, id ->
            val itemAtPos = adapterView.getItemAtPosition(position)
            val itemIdAtPos = adapterView.getItemIdAtPosition(position)
            Toast.makeText(this, "Click on item at $itemAtPos its item id $itemIdAtPos", Toast.LENGTH_LONG).show()
        }
    }

}
