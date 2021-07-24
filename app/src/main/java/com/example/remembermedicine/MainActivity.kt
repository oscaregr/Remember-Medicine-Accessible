package com.example.remembermedicine

import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_list.view.*

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
        val resultados = bd.rawQuery("select id, nombre, descripcion, tipo from medicamentos", null)

        if (resultados.count > 0){

            resultados.moveToFirst()
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getString(3))
            toNextRow(resultados)
        }

        bd.close()

        agregar.setOnClickListener {
            finish()

            val otherScreen = Intent(this, to_Register::class.java)
            startActivity(otherScreen)
        }


    }

    private fun toNextRow(resultados: Cursor) {
        while(resultados.moveToNext()){
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getString(3))
        }
        pintarLista()
    }

    // mandar registros a creacion de lis
    private fun pintarLista() {
        val myListAdapter = MyListAdapter(this,nombre,description,image,id)
        listView.adapter = myListAdapter
        listView.setOnItemClickListener(){adapterView, view, position, id ->
            val itemAtPos = adapterView.getItemAtPosition(position)
            val idRegister = Integer.parseInt(adapterView.get(position).idRegister.text.toString()) // recuperar la clave primeria
            //Toast.makeText(this, "Click on item at $itemAtPos its item id $idRegister", Toast.LENGTH_LONG).show()

            finish()

            val otherScreen = Intent(this, showMedicine::class.java)
            otherScreen.putExtra("id",idRegister)
            startActivity(otherScreen)
        }
    }

}
