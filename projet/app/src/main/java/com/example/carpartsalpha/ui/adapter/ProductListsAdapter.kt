package com.example.carpartsalpha.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ItemListLayoutBinding
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.GlideLoader
import com.example.carpartsalpha.ui.fragments.ProductsFragment

open class ProductListsAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolder(val binding: ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.binding.ivItemImage)

            holder.binding.tvItemName.text = model.title
            holder.binding.tvItemPrice.text = "${model.price} MAD"
            /*
            // TODO Step 4: Assigning the click event to the delete button.
            // START
            holder.binding.ibDeleteProduct.setOnClickListener {

                // TODO Step 8: Now let's call the delete function of the ProductsFragment.
                // START
                fragment.deleteProduct(model.product_id)
                // END
            }*/
            // END
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


}