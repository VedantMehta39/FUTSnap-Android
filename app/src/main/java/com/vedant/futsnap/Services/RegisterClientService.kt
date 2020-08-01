package com.vedant.futsnap.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.vedant.futsnap.R
import com.vedant.futsnap.UI.LoginActivity
import com.vedant.futsnap.Utils.SharedPrefFileNames
import com.vedant.futsnap.Utils.SharedPrefRepo
import com.vedant.futsnap.Utils.SharedPrefsTags
import com.vedant.futsnap.UI.Models.Platform
import com.vedant.futsnap.Utils.StringFormatter
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class RegisterClientService:FirebaseMessagingService() {
    lateinit var sharedPrefRepo:SharedPrefRepo


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        sharedPrefRepo = SharedPrefRepo(this, SharedPrefFileNames.CLIENT_REGISTRATION)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{task ->
            if(!task.isSuccessful){
                return@addOnCompleteListener
            }
            else{
                sharedPrefRepo.writeToSharedPref(SharedPrefsTags.FIREBASE_TOKEN_KEY, newToken)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()){
            sendNotification(message.data)
        }
    }


    private fun sendNotification(data: Map<String,String>){

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel("Alert",
                "Player Price Alert",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            manager.createNotificationChannel(notificationChannel)
        }


        val arrowIcon:Int
        val arrowColor:Int
        if (data["Gte"] == "true"){
            arrowIcon = R.drawable.ic_green_arrow_up
            arrowColor = R.color.profit_notification
        }
        else{
            arrowIcon = R.drawable.ic_red_arrow_down
            arrowColor = R.color.loss_notification
        }


        val intent = Intent(this,LoginActivity::class.java)
            .putExtra("FROM", "REGISTER_CLIENT_SERVICE")
            .putExtra("TO","TRACKED_PLAYERS_FRAGMENT")

        val pendingIntent = PendingIntent.getActivity(this,0,
            intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(this,"Alert")
            .setAutoCancel(true)
            .setContentTitle(parseDataForTitle(data))
            .setContentText(parseDataForBody(data))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .setLargeIcon(AppCompatResources.getDrawable(this,arrowIcon)!!.toBitmap())
            .setGroup(data["Name"])

        val summaryBuilder = NotificationCompat.Builder(this,
            "Alert")
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setGroup(data["Name"])
            .setGroupSummary(true)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))

        

        manager.notify(Random.nextInt(),builder.build())
        manager.notify(data["Id"]!!.toInt(),summaryBuilder.build())

    }

    private fun parseDataForTitle(data:Map<String,String>): String {
        val platform = Platform.values()[(data["Platform"] ?: error("")).toInt()]
        return "${data["Name"]} $platform"
    }

    private fun parseDataForBody(data:Map<String,String>): String{
        val currentPrice = StringFormatter.getLocaleFormattedStringFromNumber(data["Current Price"]!!.toInt())
        val targetPrice = StringFormatter.getLocaleFormattedStringFromNumber(data["Target Price"]!!.toInt())
        return "Current Price: $currentPrice    Target Price: $targetPrice"

    }
}