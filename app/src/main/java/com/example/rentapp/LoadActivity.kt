package com.example.rentapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rentapp.network.GadgetRESTApi
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import android.R.attr.port
import android.widget.Toast
import kotlinx.android.synthetic.main.gadget_rent.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * This activity controls what activity is launched when the app starts. If we have an authenticated user, MainActivity is launched, otherwise
 * LoginActivity.
 */
class LoadActivity : AppCompatActivity() {
    companion object {
        val TAG = "::LoadActivity::"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
    }

    /**
     * If we have a token stored in sharedPref and still valid then we don't have to sign in the user. Otherwise the LoginActivity will be launched.
     * Another case: if the server is not reachable then app is started in demo mode with mocked data.
     */
    override fun onResume() {
        super.onResume()

        doAsync {
            val isServerReachable = isHostReachable(InetAddress.getByName("152.66.180.164"), 3000)
            uiThread {
                if(isServerReachable) {
                    launchNextActivity()
                }
                else {
                    Toast.makeText(this@LoadActivity, "Szerver nem elérhető! Demó módban vagyunk.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LoadActivity, MainActivity::class.java)
                    intent.putExtra("DEMO_MODE", true)
                    startActivity(intent)
                }
            }

        }
    }

    /**
     * Check if a host is reachable. If this function is called the thread will be blocked for 2 seconds.
     */
    private fun isHostReachable(ip: InetAddress, port : Int) : Boolean {
        var exists = false
        try {
            val sockaddr = InetSocketAddress(ip, port)
            // Create an unbound socket
            val sock = Socket()
            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            val timeoutMs = 2000   // 2 seconds
            sock.connect(sockaddr, timeoutMs)
            exists = true

            sock.close()
        } catch (e: IOException) {
            // Handle exception
        }
        return exists
    }

    private fun launchNextActivity() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Get token from shared preferences
        val token = sharedPref.getString("token", null)
        Log.d(TAG, "Token from sharedPref: ${token}")

        if(token != null) {
            // If token is still valid we can go to main app, if not go to login
            val api = GadgetRESTApi.getInstance()
            api.verifyToken(token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{res ->
                        Log.d(TAG, "Response for /verify: ${res.toString()}")
                        val intent =
                                if (res.success) {
                                    GadgetRESTApi.token = token
                                    Intent(this, MainActivity::class.java)
                                }
                                else {
                                    Intent(this, LoginActivity::class.java)
                                }
                        startActivity(intent)
                    }
        }
        else {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
            }
        }
    }
}
