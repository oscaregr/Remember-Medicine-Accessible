package com.example.remembermedicine

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getLongOrNull
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    var nombre = arrayListOf<String>()
    var description = arrayListOf<String>()
    var image = arrayListOf<Int>()
    var id = arrayListOf<Int>()

    val language = arrayOf("English", "Español", "français", "日本", "Português", "русский", "中国人")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocate()
        setContentView(R.layout.activity_main)

        // spiner of language
        select_language.setOnClickListener {
            startDialog(language)
        }

        languageRigtNow()

        // recuperar registros
        val admin = AdminSQLiteOpenHelper(this, "medicinas", null, 1)
        val bd = admin.writableDatabase
        val resultados = bd.rawQuery("select id, nombre, descripcion, tipo, fechaConsumo from medicamentos", null)

        if (resultados.count > 0){

            resultados.moveToFirst()
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getInt(3))

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

    fun languageRigtNow () {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = resources.getString(R.string.preference_file_key)
        val lenguage = sharedPref.getString(getString(R.string.preference_file_key), defaultValue)

        select_language.text = lenguage
    }

    private fun startDialog(language: Array<String>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("${resources.getString(R.string.lenguage)}")
        builder.setItems(language, DialogInterface.OnClickListener { dialog, which ->
            changeLenguage(which)
        })
        builder.show()
    }

    fun changeLenguage(position: Int) {

        select_language.contentDescription = "Language. Idioma. langage. 熟語. идиома. 成语, ${language[position]}"

        when (position){
            0 -> setLocale("en")
            1 -> setLocale("es")
            2 -> setLocale("fr")
            3 -> setLocale("ja")
            4 -> setLocale("pt")
            5 -> setLocale("ru")
            6 -> setLocale("zh")
        }

        finish()
        Intent(this,MainActivity::class.java).also {
            startActivity(it)
        }
    }

    private fun setLocale(lang: String?) {


        val context: Context = MyContextWrapper.wrap(this, lang)
        resources.updateConfiguration(context.getResources().getConfiguration(), context.getResources().getDisplayMetrics())

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(getString(R.string.preference_file_key), lang)
            commit()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    fun loadLocate () {

        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE) ?: return
        val defaultValue = resources.getString(R.string.preference_file_key)
        val lenguage = sharedPref.getString(getString(R.string.preference_file_key), defaultValue)

        setLocale(lenguage)
    }

    private fun toNextRow(resultados: Cursor, bd: SQLiteDatabase) {
        val date = Calendar.getInstance()

        while(resultados.moveToNext()){
            id.add(resultados.getInt(0))
            nombre.add(resultados.getString(1))
            description.add(resultados.getString(2))
            image.add(resultados.getInt(3))

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
        val myListAdapter = MyListAdapter(this, nombre, description, image, resources)
        listView.adapter = myListAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            val idRegister = this.id.get(position)
            finish()

            val otherScreen = Intent(this, showMedicine::class.java)
            otherScreen.putExtra("id", idRegister)
            startActivity(otherScreen)
        }
    }
}
