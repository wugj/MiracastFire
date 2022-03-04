package miracast.fire.home

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.urapp.myappratinglibrary.AppRatingDialog
import com.urapp.myappratinglibrary.listener.RatingDialogListener
import miracast.fire.DeviceSpecs
import miracast.fire.R
import miracast.fire.Utils.longToast
import miracast.fire.Utils.openAppLink
import miracast.fire.Utils.shortToast
import miracast.fire.databinding.ActivityMainBinding
import miracast.fire.model.SliderItem
import miracast.fire.privacy_policy.PrivacyPolicyActivity


class MainActivity : AppCompatActivity(), View.OnClickListener, RatingDialogListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var wifiManager: WifiManager
    private var connectivityManager: ConnectivityManager?=null
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private lateinit var wifiInfo: WifiInfo
    private val ACTION_WIFI_DISPLAY_SETTINGS : String = "android.settings.WIFI_DISPLAY_SETTINGS"
    var doubleBackToExitPressedOnce = false
    private val MY_PERMISSIONS_ACCESS_FINE_LOCATION: Int=101
    private lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initAll()

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),MY_PERMISSIONS_ACCESS_FINE_LOCATION)
            } else {
                getConnectedWifiInfo()
            }
        } else {
            getConnectedWifiInfo()
        }




    }


    private fun initAll() {
        sliderAdapter=SliderAdapter(this)
        wifiManager= applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager = applicationContext.getSystemService(ConnectivityManager::class.java)
        }

        binding.widiBtn.setOnClickListener(this)
        binding.imageSlider.setSliderAdapter(sliderAdapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()

        for(i in 1..5) {
            val sliderItem: SliderItem= SliderItem(R.drawable.ic_launcher, "Test title is:- $i")
            sliderAdapter.addItem(sliderItem)
        }

    }

    private fun getConnectedWifiInfo() {
        if (wifiManager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT >= 23) {
                var request: NetworkRequest =
                    NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build()

                networkCallback = object:ConnectivityManager.NetworkCallback() {  // FLAG_INCLUDE_LOCATION_INFO (into constructor)
                    override fun onAvailable(network : Network) {

                    }

                    override fun onLost(network : Network) {

                    }

                    override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            wifiInfo=networkCapabilities.transportInfo as WifiInfo
                        } else {
                            wifiInfo=wifiManager.connectionInfo
                        }
                        binding.wifiSsidNameTextView.text=wifiInfo.ssid
                    }

                    override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {

                    }
                }
                connectivityManager?.requestNetwork(request, networkCallback) // For request
                connectivityManager?.registerNetworkCallback(request, networkCallback) // For listen
            } else {
                wifiInfo=wifiManager.connectionInfo
                binding.wifiSsidNameTextView.text=wifiInfo.ssid
            }
        }
    }

    private fun wifidisplay() {
        try {
            startActivity(Intent(ACTION_WIFI_DISPLAY_SETTINGS))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            try {
                startActivity(packageManager.getLaunchIntentForPackage("com.samsung.wfd.LAUNCH_WFD_PICKER_DLG"))
            } catch (e2: Exception) {
                try {
                    startActivity(Intent("android.settings.CAST_SETTINGS"))
                } catch (e3: Exception) {
                    Toast.makeText(applicationContext, "Device not supported", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        appFeedbackDialog().monitor()
        if (appFeedbackDialog().shouldShowRateDialog()) {
            appFeedbackDialog().showRateDialogIfMeetsConditions()
        } else {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            doubleBackToExitPressedOnce = true
            longToast(resources.getString(R.string.click_back_again))
            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    private fun appFeedbackDialog(): AppRatingDialog {
        return AppRatingDialog.Builder()
            .setCancelable(false)
            .setPositiveButtonText(resources.getString(R.string.submit))
            .setNegativeButtonText(resources.getString(R.string.never))
            .setNeutralButtonText(resources.getString(R.string.later))
            .setTitle(resources.getString(R.string.app_feedback_title))
            .setDescription(resources.getString(R.string.app_feedback_message))
            .setStarColor(R.color.colorAccent)
            .setTitleTextColor(R.color.colorWhite)
            .setDescriptionTextColor(R.color.colorWhite)
            .setDialogBackgroundColor(R.color.colorGray)
            .setAfterInstallDay(0)
            .setDefaultRating(3)
            .setNumberOfLaunches(1)
            .setRemindIntervalDay(0)
            .setCanceledOnTouchOutside(false)
            .create(this)
    }

    private fun checkWifiState() {
        if (!wifiManager.isWifiEnabled) {
            if (Build.VERSION.SDK_INT>=29) {
                showWifiOnAlertDialog()
            } else {
                wifiManager.setWifiEnabled(true)
                wifidisplay()
            }
        } else {
            wifidisplay()
        }
    }

    private fun showWifiOnAlertDialog() {
        val builder : AlertDialog.Builder=AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage(resources.getString(R.string.wifi_permission_dialog))
        builder.setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
            finishAffinity()
        }
        val alertDialog : AlertDialog=builder.create()
        if (!isFinishing) {
            alertDialog.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getConnectedWifiInfo()
            } else{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                shortToast(resources.getString(R.string.location_cancel_message))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.toolbarPrivacyPolicyMenuId -> startActivity(
                Intent(
                    this@MainActivity,
                    PrivacyPolicyActivity::class.java
                )
            )
//            R.id.toolbarRatingMenuId -> openAppInPlayStore()
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.widiBtn -> checkWifiState()
        }
    }

    override fun onNegativeButtonClicked() {
        shortToast(resources.getString(R.string.thank_you))
        super.onBackPressed()
    }

    override fun onNeutralButtonClicked() {
        shortToast(resources.getString(R.string.thank_you))
        super.onBackPressed()
    }

    override fun onPositiveButtonClicked(rate: Int) {
        if (rate==5) {
            openAppLink(this)
        } else{
            shortToast(resources.getString(R.string.thank_you))
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.particleView.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.particleView.resume()
    }

    override fun onDestroy() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
        super.onDestroy()
    }


}