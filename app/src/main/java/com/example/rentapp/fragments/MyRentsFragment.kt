package com.example.rentapp.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentapp.R

class MyRentsFragment : Fragment(){
    companion object{
        const val TAG = "ADD_GADGET_FRAGMENT_TAG"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.rent_list, container,false)
    }
}