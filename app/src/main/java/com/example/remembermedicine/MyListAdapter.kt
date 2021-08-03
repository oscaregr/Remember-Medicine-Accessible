package com.example.remembermedicine
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class MyListAdapter(private val context: Activity, private val title: ArrayList<String>, private val description: ArrayList<String>, private val imgid: ArrayList<String>, private val id: ArrayList<Int>)
    : ArrayAdapter<String>(context, R.layout.custom_list, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
        val subtitleText = rowView.findViewById(R.id.description) as TextView
        val idRegister = rowView.findViewById(R.id.idRegister) as TextView

        titleText.text = title[position]
        idRegister.text = id[position].toString()
        when (imgid[position])
        {
            "Pastillas" -> imageView.setImageResource(R.drawable.pills_free)
            "Capsulas" -> imageView.setImageResource(R.drawable.capsulas)
            "Inalador" -> imageView.setImageResource(R.drawable.inalador_free)
            "Gotas" -> imageView.setImageResource(R.drawable.gotas)
            "Crema untable" -> imageView.setImageResource(R.drawable.gel_untable)
            "Gel consumible" -> imageView.setImageResource(R.drawable.gel_consumible)
            "Inyección" -> imageView.setImageResource(R.drawable.injeccion)
            "Ampolletas" -> imageView.setImageResource(R.drawable.ampolleta)
            "Inalador via nazal" -> imageView.setImageResource(R.drawable.inalador_nazal)
        }

        imageView.contentDescription = imgid[position]
        subtitleText.text = description[position]

        rowView.contentDescription = "Toca 2 veces para mostrar información"

        return rowView
    }

}