package a.screenmirroring

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast


object Utils {
    private val preferenceName : String="CastSharedPreference"
    var sharedPreferences: SharedPreferences?=null

    private fun initSharedPref(context: Context): SharedPreferences {
        if (sharedPreferences ==null) {
            sharedPreferences =context.getSharedPreferences(preferenceName,MODE_PRIVATE)
        }
        return sharedPreferences!!
    }


    fun setBooleanToStorage(context: Context,key: String, value: Boolean) {
        val editor: SharedPreferences.Editor= initSharedPref(context).edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun getBooleanFromStorage(context: Context,key: String, defaultValue: Boolean) : Boolean {
        return initSharedPref(context).getBoolean(key,defaultValue)
    }

    fun openAppLink(context: Context) {
        val appPackageName: String=context.applicationContext.packageName
        try {
            val appIntent: Intent= Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            appIntent.setPackage("com.android.vending")
            context.startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")),"Choose One"))
        }
    }

    fun Context.shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.longToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


}