package com.example.remembermedicine
import android.app.Activity
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_show_medicine.*

class MyListAdapter(private val context: Activity, private val title: ArrayList<String>, private val description: ArrayList<String>, private val imgid: ArrayList<Int>, private val resources: Resources)
    : ArrayAdapter<String>(context, R.layout.custom_list, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {

        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
        val subtitleText = rowView.findViewById(R.id.description) as TextView

        titleText.text = title[position]
        when (imgid[position])
        {
            0 -> {
                imageView.setImageResource(R.drawable.pills_free)
                imageView.contentDescription = "${resources.getString(R.string.tablets)}"
            }
            1 -> {
                imageView.setImageResource(R.drawable.capsulas)
                imageView.contentDescription = "${resources.getString(R.string.capsules)}"
            }
            2 -> {
                imageView.setImageResource(R.drawable.inalador_free)
                imageView.contentDescription = "${resources.getString(R.string.inalator)}"
            }
            3 -> {
                imageView.setImageResource(R.drawable.gotas)
                imageView.contentDescription = "${resources.getString(R.string.drops)}"
            }
            4 -> {
                imageView.setImageResource(R.drawable.gel_untable)
                imageView.contentDescription = "${resources.getString(R.string.cream)}"
            }
            5 -> {
                imageView.setImageResource(R.drawable.gel_consumible)
                imageView.contentDescription = "${resources.getString(R.string.gel)}"
            }
            6 -> {
                imageView.setImageResource(R.drawable.injeccion)
                imageView.contentDescription = "${resources.getString(R.string.injection)}"
            }
            7 -> {
                imageView.setImageResource(R.drawable.ampolleta)
                imageView.contentDescription = "${resources.getString(R.string.ampoules)}"
            }
            8 -> {
                imageView.setImageResource(R.drawable.inalador_nazal)
                imageView.contentDescription = "${resources.getString(R.string.nasal)}"
            }
        }

        subtitleText.text = description[position]

        //rowView.contentDescription = "Toca 2 veces para mostrar información"

        return rowView
    }

}