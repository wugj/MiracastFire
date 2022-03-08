package a.screenmirroring.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import a.screenmirroring.R
import a.screenmirroring.databinding.ActivitySplashBinding
import a.screenmirroring.home.MainActivity
import a.screenmirroring.privacy_policy.PrivacyPolicyActivity


class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private var timer: CountDownTimer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setUpUi()
        initTimer()

    }


    private fun setUpUi() {
        Picasso.get().load(R.drawable.ic_launcher).error(R.drawable.ic_launcher).into(binding.splashLogo)
    }

    private  fun  initTimer() {
        timer?.cancel()
        timer=null
        timer= object : CountDownTimer( 3000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                gotoNextActivity()
            }

        }.start()
    }

    private fun gotoNextActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbarPrivacyPolicyMenuId -> startActivity(
                Intent(
                    this@SplashActivity,
                    PrivacyPolicyActivity::class.java
                )
            )
        }
        return true
    }

    override fun onDestroy() {
        timer?.cancel()
        timer=null
        super.onDestroy()
    }


}