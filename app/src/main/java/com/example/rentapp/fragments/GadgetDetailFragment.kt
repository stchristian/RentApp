package com.example.rentapp.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentapp.MainActivity
import com.example.rentapp.R
import com.example.rentapp.adapter.ImagePagerAdapter
import com.example.rentapp.data.Gadget
import kotlinx.android.synthetic.main.gadget_detail.*

class GadgetDetailFragment : Fragment() {

    companion object{
        const val TAG = "GADGET_DETAIL_FRAGMENT_TAG"
    }



    private var gadget: Gadget? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =inflater.inflate(R.layout.gadget_detail, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        this.gadget = mainActivity.selectedGadget

        //TODO: bind data to view
        this.gadget?.let {
            vpImageGallery.adapter = ImagePagerAdapter(it.images, activity as Activity)
            tvGadgetName.text = it.name
            tvGadgetDescription.text = it.description
        }



    }
}