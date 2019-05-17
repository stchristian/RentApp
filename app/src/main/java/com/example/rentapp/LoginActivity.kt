package com.example.rentapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.rentapp.network.GadgetRESTApi
import kotlinx.android.synthetic.main.activity_login.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        tvNoAccount.setOnClickListener{
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener{

            val mail = etUserMail.text.toString()
            val password = etUserPassword.text.toString()

            var errors = false
            if(mail.isEmpty()) {
                errors = true
                tilUserMail.error = "Email cím megadása kötelező"
            }
            if(password.isEmpty()) {
                errors = true
                tilUserPassword.error = "Jelszó megadása kötelező"
            }

            Log.d("::LoginActivity::", "Error volt e: ${errors}")
            if(!errors) {
                val api = GadgetRESTApi.getInstance()
                api.loginUser(mail, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ res ->
                            Log.d("::LoginActivity::", "Response: ${res.toString()}")
                            if(res.success) {
                                val token = res.token
                                GadgetRESTApi.token = token
                                val sharedPref = getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putString("token", token)
                                    apply()
                                }
                                Intent(this@LoginActivity, MainActivity::class.java).also {
                                    startActivity(it)
                                }
                            }
                            else {
                                res.error?.forEach { err ->
                                    tvError.append(err.msg)
                                }
                            }
                        },
                        { error ->
                            Log.d("::LoginActivity::", "ERROR: ${error}")
                        })
            }

        }
    }
}
