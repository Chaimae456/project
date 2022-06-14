package com.example.carpartsalpha.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carpartsalpha.databinding.ItemDashboardLayoutBinding
import com.example.carpartsalpha.databinding.ItemListLayoutBinding
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.GlideLoader

open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolder(val binding: ItemDashboardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DashboardItemsListAdapter.MyViewHolder(
            ItemDashboardLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.binding.ivDashboardItemImage
            )
            holder.binding.tvDashboardItemTitle.text = model.title
            holder.binding.tvDashboardItemPrice.text = "${model.price} MAD"
        }
    }
    /**
    * Gets the number of items in the list
    */
    override fun getItemCount(): Int {
        return list.size
    }

}