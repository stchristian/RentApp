package com.example.rentapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import android.widget.Toast
import com.example.rentapp.adapter.GadgetAdapter
import com.example.rentapp.adapter.OnGadgetItemClickListener
import com.example.rentapp.data.Gadget
import com.example.rentapp.fragments.*
import com.example.rentapp.network.GadgetRESTApi
import com.example.rentapp.network.User
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.gadget_list.*
import kotlinx.android.synthetic.main.nav_header.*
import kotlinx.android.synthetic.main.nav_header.view.*
import org.w3c.dom.Text
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity(), OnGadgetItemClickListener {


    companion object {
        val CONSOLE_TAG = "RENTAPP"
    }

    var selectedGadget: Gadget? = null
    var currentFragment: Fragment? = null
    var gadgetListFragment: GadgetListFragment? = null
    var addGadgetFragment: AddGadgetFragment?  = null
    var myRentsFragment: MyRentsFragment?  = null

    private lateinit var user: User

    /**
     * When user click on gadget it shows the details
     */
    override fun onGadgetItemClick(gadget: Gadget) {
        Log.d(CONSOLE_TAG, "onGadgetItemClick()")
        this.selectedGadget = gadget

        val fragment = GadgetDetailFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment, GadgetDetailFragment.TAG)
        ft.addToBackStack(GadgetDetailFragment.TAG)
        ft.commit()
    }

    /**
     * When user presses the rent button we are gonna show the rent fragment.
     */
    override fun onGadgetRent(gadget: Gadget) {
        this.selectedGadget = gadget
        val fragment = GadgetRentFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment, GadgetRentFragment.TAG)
        ft.addToBackStack(GadgetRentFragment.TAG)
        ft.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setActionBarToggle()
        setUpNavigationView()
        gadgetListFragment()

        getCurrentUser()
    }

    lateinit var textViewUserFullName : TextView
    lateinit var textViewUserEmail: TextView

    private fun gadgetListFragment() {
        if(gadgetListFragment == null) {
            gadgetListFragment = GadgetListFragment()
        }
        val localFragment = this.gadgetListFragment as Fragment
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, localFragment, GadgetListFragment.TAG)
        ft.addToBackStack(GadgetListFragment.TAG)
        ft.commit()
        currentFragment = gadgetListFragment
    }

    private fun setUpNavigationView() {
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_gadget_list -> gadgetListFragment()
                R.id.nav_add_gadget -> addGadgetFragment()
                R.id.nav_my_rents -> myRentsFragment()
                R.id.log_out -> logOut()
            }
            if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            return@setNavigationItemSelectedListener true
        }
        nav_view.menu.getItem(0).isChecked = true

        val hView = nav_view.inflateHeaderView(R.layout.nav_header)
        textViewUserFullName = hView.findViewById(R.id.tvUserFullName) as TextView
        textViewUserEmail = hView.findViewById(R.id.tvUserMail) as TextView
    }

    private fun addGadgetFragment() {
        if(addGadgetFragment == null) {
            addGadgetFragment = AddGadgetFragment()
        }
        val localFragment = this.addGadgetFragment as Fragment
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, localFragment, AddGadgetFragment.TAG)
        ft.addToBackStack(AddGadgetFragment.TAG)
        ft.commit()
        currentFragment = addGadgetFragment
    }

    private fun myRentsFragment() {
        if(myRentsFragment == null) {
            myRentsFragment = MyRentsFragment()
        }
        val localFragment = this.myRentsFragment as Fragment
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, localFragment, MyRentsFragment.TAG)
        ft.addToBackStack(MyRentsFragment.TAG)
        ft.commit()
        currentFragment = myRentsFragment
    }

    private fun setActionBarToggle() {
        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }

    private fun logOut() {
        val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            remove("token")
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result != null) {
            if(result.contents != null) {
                val currFragment = currentFragment
                if(currFragment is GadgetListFragment) {
                    currFragment.onQrCodeScanned(result.contents)
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getCurrentUser() {
        if(intent.getBooleanExtra("DEMO_MODE",false)) {
            user = User(0,"1",true,"demojani@demo.com",false,"asd", "János", true,"2017.01.02", "+30201231234", "Demó")
            displayUserDataOnUI()
        }
        else {
            GadgetRESTApi.getInstance().getMyUser().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe{res ->
                        Log.d("::MainActivity:: ", "Response ${res}")
                        if(res.success) {
                            user = res.user!!
                            displayUserDataOnUI()
                        }
                        else {
                            Toast.makeText(this, "Hiba : ${res?.msg}", Toast.LENGTH_LONG).show()
                        }
                    }
        }
    }

    private fun displayUserDataOnUI() {
        textViewUserFullName.text = "${user.vezeteknev} ${user.keresztnev}"
        textViewUserEmail.text = "${user.email}"
        if(user.admin || user.eszkozfelelos) {
            nav_view.menu.add(R.id.navigation_links, R.id.nav_add_gadget, 0, R.string.add_gadget).setIcon(R.drawable.ic_add_gadget).setCheckable(true)
        }
        Toast.makeText(this, "Üdvözöljük ${user.vezeteknev} ${user.keresztnev}!", Toast.LENGTH_LONG).show()
    }
}
