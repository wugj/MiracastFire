package a.screenmirroring.home

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.*
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import a.screenmirroring.R
import a.screenmirroring.databinding.ActivityMainBinding
import a.screenmirroring.databinding.CustomRatingBarBinding
import a.screenmirroring.Utils.getBooleanFromStorage
import a.screenmirroring.Utils.longToast
import a.screenmirroring.Utils.openAppLink
import a.screenmirroring.Utils.setBooleanToStorage
import a.screenmirroring.Utils.shortToast
import a.screenmirroring.model.SliderItem
import a.screenmirroring.privacy_policy.PrivacyPolicyActivity


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var wifiManager: WifiManager
    private var connectivityManager: ConnectivityManager?=null
    private var networkCallback: ConnectivityManager.NetworkCallback?=null
    private var wifiInfo: WifiInfo?=null
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
        wifiManager= applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager = applicationContext.getSystemService(ConnectivityManager::class.java)
        }

        binding.widiBtn.setOnClickListener(this)
        setUpSlider()


    }

    private fun setUpSlider() {
        sliderAdapter= SliderAdapter(this)
        binding.imageSlider.setSliderAdapter(sliderAdapter)
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.imageSlider.startAutoCycle()

        val sliderItemOne: SliderItem = SliderItem(R.drawable.slider_one, "Slider one title")
        val sliderItemTwo: SliderItem = SliderItem(R.drawable.slider_two, "Slider two title")
        val sliderItemThree: SliderItem = SliderItem(R.drawable.slider_three, "Slider three title")
        sliderAdapter.addItem(sliderItemOne)
        sliderAdapter.addItem(sliderItemTwo)
        sliderAdapter.addItem(sliderItemThree)
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
                            try {
                                wifiInfo=networkCapabilities.transportInfo as WifiInfo
                            } catch (e: Exception) {}
                        } else {
                            wifiInfo=wifiManager.connectionInfo
                        }
                        binding.wifiSsidNameTextView.text=wifiInfo?.ssid
                    }

                    override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {

                    }
                }
                connectivityManager?.requestNetwork(request, networkCallback!!) // For request
                connectivityManager?.registerNetworkCallback(request, networkCallback!!) // For listen
            } else {
                wifiInfo=wifiManager.connectionInfo
                binding.wifiSsidNameTextView.text=wifiInfo?.ssid
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

    private fun createCustomRatingAlertDialog() {
        val ratingDialogViewBinding: CustomRatingBarBinding = CustomRatingBarBinding.inflate(layoutInflater)
        val ratingBar: RatingBar=ratingDialogViewBinding.ratingBar
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                if (p1 ==5f) {
                    setBooleanToStorage(this,"IsRatted",true)
                    openAppLink(this)
                } else {
                    shortToast(resources.getString(R.string.thank_you))
                    super.onBackPressed()
                }
            }
        ratingDialogViewBinding.skipButton.setOnClickListener(this)

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setCancelable(false)
        alertDialog.setView(ratingDialogViewBinding.root)
        if (!isFinishing) {
            alertDialog.show()
        }
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
        val builder : AlertDialog.Builder=AlertDialog.Builder(this,R.style.MyDialogTheme)
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
            R.id.skipButton -> {
                shortToast(resources.getString(R.string.thank_you))
                super.onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        if (getBooleanFromStorage(this,"IsRatted",false)) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            doubleBackToExitPressedOnce = true
            longToast(resources.getString(R.string.click_back_again))
            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        } else {
            createCustomRatingAlertDialog()
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
        try {
            if (networkCallback !=null) {
                connectivityManager?.unregisterNetworkCallback(networkCallback!!)
            }
        } catch (e: Exception) {}
        super.onDestroy()
    }


}