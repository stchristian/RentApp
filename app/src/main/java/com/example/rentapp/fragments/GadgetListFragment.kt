package com.example.rentapp.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.rentapp.MainActivity
import com.example.rentapp.R
import com.example.rentapp.adapter.GadgetAdapter
import com.example.rentapp.data.Gadget
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.gadget_list.*

/**
 * Az eszközök listájának fragmentje
 */
class GadgetListFragment : Fragment() {

    companion object{
        const val TAG = "GADGET_LIST_FRAGMENT_TAG"
        const val lorem_ipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    }

    private lateinit var adapter: GadgetAdapter


    private val mockedGadgetList = mutableListOf<Gadget>(
            Gadget("Iphone XS", "QRcode123", "001122", "Kovács Béla", lorem_ipsum,"https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg", arrayOf("https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg","https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg")),
            Gadget("Iphone XS", "QRcode123", "001122", "Kovács Béla", lorem_ipsum,"https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg", arrayOf()),
            Gadget("Iphone XS", "QRcode123", "001122", "Kovács Béla", "Leírás az eszközről...","https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg", arrayOf()),
            Gadget("Pixel 3", "QRcode123", "001122", "Kovács Béla", "Leírás az eszközről...","https://i.gadgets360cdn.com/products/large/1536782640_635_iphone_xs.jpg", arrayOf()),
            Gadget("Samsung Galaxy S9+", "QRcode123", "001122", "Kovács Béla", "Leírás az eszközről...","https://ss7.vzw.com/is/image/VerizonWireless/SAMSUNG_Galaxy_S9_Plus_Blue", arrayOf()),
            Gadget("Samsung Galaxy S9+", "QRcode123", "001122", "Kovács Béla", "Leírás az eszközről...","https://ss7.vzw.com/is/image/VerizonWireless/SAMSUNG_Galaxy_S9_Plus_Blue", arrayOf())
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.gadget_list, container, false)
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        searchView.setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

        })

        ibSearchByQr.setOnClickListener {
            IntentIntegrator(this.activity).initiateScan()
        }
    }

    fun onQrCodeScanned(content : String) {
        tvQrResult.text = content
    }

    private fun initRecyclerView() {
        adapter = GadgetAdapter(  activity as MainActivity, this.context!!, mockedGadgetList)
        recyclerGadget.adapter = adapter
    }
}