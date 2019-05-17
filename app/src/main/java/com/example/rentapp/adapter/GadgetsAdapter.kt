package com.example.rentapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.example.rentapp.R
import com.example.rentapp.data.Gadget
import kotlinx.android.synthetic.main.gadget_list_item.view.*
import java.util.*

interface OnGadgetItemClickListener {
    fun onGadgetItemClick(gadget: Gadget)
    fun onGadgetRent(gadget: Gadget)
}
/**
 * Az eszközök listájának adaptere.
 */
class GadgetAdapter : RecyclerView.Adapter<GadgetAdapter.ViewHolder> , Filterable {

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            results?.let {
                items.clear()
                items.addAll(results!!.values as List<Gadget>)
            }
            notifyDataSetChanged()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            var filteredList : List<Gadget>
            if(constraint == null || constraint.isEmpty()) {
                filteredList = itemsFull.toList()
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim()

                filteredList = itemsFull.filter {gadget ->
                    gadget.name.toLowerCase().contains(filterPattern)
                }
            }

            val results = FilterResults()
            results.values = filteredList
            return results
        }

    }



    private val items = mutableListOf<Gadget>()
    private val itemsFull = mutableListOf<Gadget>()

    private val context: Context
    private val listener: OnGadgetItemClickListener

    constructor(listener : OnGadgetItemClickListener,context: Context, items: List<Gadget>) : super() {
        this.listener = listener
        this.context = context
        this.items.addAll(items)
        this.itemsFull.addAll(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
                R.layout.gadget_list_item, parent, false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = items[position].name
        Glide.with(this.context).load(items[position].thumbnailUrl).into(holder.ivGadgetLogo)

        holder.btnRent.setOnClickListener{
            listener.onGadgetRent(items[position])
            Toast.makeText(this.context, items[position].name + " requested for rent", Toast.LENGTH_LONG).show()
        }

        holder.itemView.setOnClickListener{
            this.listener.onGadgetItemClick(items[position])
        }
    }

    fun addItem(item: Gadget) {
        items.add(item)
        notifyItemInserted(items.lastIndex)
    }

    fun deleteItem(position: Int) {
//        val dbThread = Thread {
//            AppDatabase.getInstance(context).shoppingItemDao().deleteItem(
//                    items[position])
//            (context as MainActivity).runOnUiThread{
//                items.removeAt(position)
//                notifyItemRemoved(position)
//            }
//        }
//        dbThread.start()
    }

//    fun updateItem(item: ShoppingItem) {
//        val idx = items.indexOf(item)
//        items[idx] = item
//        notifyItemChanged(idx)
//    }

//    override fun onItemDismissed(position: Int) {
//        deleteItem(position)
//    }
//
//    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
//        Collections.swap(items, fromPosition, toPosition)
//
//        notifyItemMoved(fromPosition, toPosition)
//    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.tvGadgetName
        val ivGadgetLogo: ImageView = itemView.ivGadgetLogo
        val btnRent: Button = itemView.btnRent
    }
}