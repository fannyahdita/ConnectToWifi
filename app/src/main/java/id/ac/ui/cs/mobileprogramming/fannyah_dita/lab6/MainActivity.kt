package id.ac.ui.cs.mobileprogramming.fannyah_dita.lab6

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(connectionReciever, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        connect.setOnClickListener {
            val wifi = getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (!wifi.isWifiEnabled) {
                titleText.text = getString(R.string.enabling)
                wifi.isWifiEnabled = true
            }

            titleText.text = getString(R.string.connecting)

            connectToNetworkWep("ssid", "password") //wifi apa ni yang kamu mau

            titleText.text = getString(R.string.connected)
        }

        disconnect.setOnClickListener {
            titleText.text = getString(R.string.disconnecting)
            val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.disconnect()
            wifiManager.isWifiEnabled = false
            titleText.text = getString(R.string.disconnected)
        }
    }

    private val connectionReciever = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currentNetworkInfo =
                intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)
            if (currentNetworkInfo!!.isConnected) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                Toast.makeText(
                    applicationContext,
                    "Broadcast Connected:" + wifiInfo.ssid,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(applicationContext, "Broadcast Not Connected", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun connectToNetworkWep(ssid: String, password: String): Boolean {
        try {
            val conf = WifiConfiguration()
            conf.SSID =
                "\"" + ssid + "\""

            conf.preSharedKey = "\"" + password + "\""

            conf.status = WifiConfiguration.Status.ENABLED
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)

            val wifiManager =
                this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(conf)

            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    break
                }
            }
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
