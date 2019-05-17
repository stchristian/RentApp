package com.example.rentapp.fragments


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rentapp.MainActivity
import com.example.rentapp.R
import com.example.rentapp.adapter.ImagePagerAdapter
import com.example.rentapp.data.Gadget
import com.savvi.rangedatepicker.CalendarPickerView
import kotlinx.android.synthetic.main.gadget_detail.*
import kotlinx.android.synthetic.main.gadget_rent.*
import java.util.*

class GadgetRentFragment : Fragment() {

    companion object{
        const val TAG = "GADGET_RENT_FRAGMENT_TAG"
        const val TAKE_PHOTO = 1001
    }

    private var gadget: Gadget? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.gadget_rent, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = Date()
        val maxDate = Date(currentDate.year, currentDate.month + 3, currentDate.day)

        calendar_view.init(currentDate,maxDate)
                .inMode(CalendarPickerView.SelectionMode.RANGE)



//        val mainActivity = activity as MainActivity
//        this.gadget = mainActivity.selectedGadget
//
//        //TODO: bind data to view
//        this.gadget?.let {
//            vpImageGallery.adapter = ImagePagerAdapter(it.images, activity as Activity)
//            tvDetailGadgetName.text = it.name
//            tvDetailGadgetDescription.text = it.description
//        }


    }


}