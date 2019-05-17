package com.example.rentapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.rentapp.network.GadgetRESTApi
import kotlinx.android.synthetic.main.activity_registration.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        btnRegister.setOnClickListener{
            register()
        }
    }

    private fun register() {
        var error : Boolean = false

        if(etKeresztnev.text.isNullOrEmpty()) {
            etKeresztnev.setError("Keresztnév megadása kötelező")
            error = true
        }
        if(etVezeteknev.text.isNullOrEmpty()) {
            etVezeteknev.setError("Vezetéknév megadása kötelező")
            error = true
        }
        if(etEmail.text.isNullOrEmpty()) {
            etEmail.setError("Email cím megadása kötelező")
            error = true
        }
        if(etJelszo1.text.isNullOrEmpty()) {
            etJelszo1.setError("Jelszó megadása kötelező")
            error = true
        }
        if(etJelszo2.text.isNullOrEmpty()) {
            etJelszo2.setError("Jelszó megadása kötelező")
            error = true
        }
        if(etJelszo1.text.toString() != etJelszo2.text.toString()) {
            etJelszo1.setError("A megadott jelszavak nem egyeznek meg")
            error = true
        }
        if(etTelefonszam.text.isNullOrEmpty()) {
            etTelefonszam.setError("Telefonszám megadása kötelező")
            error = true
        }

        if(!error) {
            GadgetRESTApi.getInstance().registerUser(etEmail.text.toString(), etJelszo1.text.toString(), etVezeteknev.text.toString(), etKeresztnev.text.toString(), etTelefonszam.text.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{ res ->
                        if(res.success) {
                            AlertDialog.Builder(this).setMessage(res.msg).setTitle("Sikeres regisztráció!").show()
                        }
                        else {
                            res.error?.forEach { err ->
                                tvRegistrationError.append(err.msg)
                            }
                        }
                    }
        }

    }
}
