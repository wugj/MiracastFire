package miracast.of.all.tv.privacy_policy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import miracast.of.all.tv.R
import miracast.of.all.tv.databinding.ActivityPrivacyPolicyBinding


class PrivacyPolicyActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityPrivacyPolicyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.privacyPolicyReadFromOnlineButtonId.setOnClickListener(this)


    }


    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(Intent.createChooser(i, resources.getString(R.string.choose_a_browser)))
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.privacyPolicyReadFromOnlineButtonId -> {
                openUrl("https://bd-super.blogspot.com/p/privacy-policy-for-screen-mirroring-pro.html?m=1");
            }
        }
    }


}