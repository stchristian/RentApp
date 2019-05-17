package com.example.rentapp.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.rentapp.data.Gadget

/**
 * This class is the adapter of the image slider component (which is basically just a ViewPagerAdapter) for the gadget's details.
 */
class ImagePagerAdapter(private val imageUrls: Array<String> , private val context: Context): PagerAdapter() {

    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val img =  ImageView(container.context)
        Glide.with(container.context).load(this.imageUrls[position]).into(img);
        container.addView(img)
        return img
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

}