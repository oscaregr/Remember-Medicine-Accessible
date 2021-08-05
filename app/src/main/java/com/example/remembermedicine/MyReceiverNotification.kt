package com.example.remembermedicine

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable


class MyReceiverNotification : BroadcastReceiver() {

    var id: Int? = 0
    var name: String? = "don't work"
    var descripcion: String? = "no work"
    var tipo: String? = "no work"

    override fun onReceive(context: Context, intent: Intent) {

        val action = intent.action

        Log.i("Receiver", "Broadcast received: $action")

        if (action == "send.info") {
            id = intent.extras!!.getInt("id")
            name = intent.extras!!.getString("name")
            descripcion = intent.extras!!.getString("description")
        }

        // to clik on notification
        val resultIntent = Intent(context, showMedicine::class.java)
        resultIntent.putExtra("id", id)
        // Create the TaskStackBuilder
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(id!!.toInt(), PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val builder : NotificationCompat.Builder = NotificationCompat.Builder(
                context, "Medicine ${id}"
        )
        builder.setContentTitle(name)
        builder.setContentText(descripcion)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.icon_medicine);
            builder.setColor(ContextCompat.getColor(context, R.color.white))
        } else {
            builder.setSmallIcon(R.mipmap.icon_medicine);
        }

        builder.setLargeIcon(Bitmap.createScaledBitmap(BitmapFactory.decodeResource( context.resources,R.mipmap.icon_medicine), 128, 128, false))

        builder.setContentIntent(resultPendingIntent)

        val managerCompat = NotificationManagerCompat.from(context)
        managerCompat.notify(id.toString().toInt(), builder.build())
    }
}