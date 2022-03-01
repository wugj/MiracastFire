package miracast.fire

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast


object Utils {
    private val preferenceName : String="CastSharedPreference"


    public fun saveIntToStorage(context: Context, key: String, value: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    public fun getIntFromStorage(context: Context, key: String, defaultValue: Int): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(key, defaultValue)
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