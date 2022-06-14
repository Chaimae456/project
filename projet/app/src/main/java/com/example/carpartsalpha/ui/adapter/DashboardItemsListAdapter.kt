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

    // A global variable for OnClickListener interface.
    private var onClickListener: OnClickListener? = null

    class MyViewHolder(val binding: ItemDashboardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
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

            // Assign the on click event for item view and pass the required params in the on click function.
            // START
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
            // END

        }
    }
    /**
    * Gets the number of items in the list
    */
    override fun getItemCount(): Int {
        return list.size
    }
    /**
     * A function for OnClickListener where the Interface is the expected parameter and assigned to the global variable.
     *
     * @param onClickListener
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    /**
     * An interface for onclick items.
     */
    interface OnClickListener {

        // Define a function to get the required params when user clicks on the item view in the interface.
        // START
        fun onClick(position: Int, product: Product)
        // END
    }
    // END


}