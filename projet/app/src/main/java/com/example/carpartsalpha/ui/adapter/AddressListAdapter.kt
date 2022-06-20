package com.example.carpartsalpha.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.carpartsalpha.databinding.ItemAddressLayoutBinding
import com.example.carpartsalpha.databinding.ItemCartLayoutBinding
import com.example.carpartsalpha.models.Address
import com.example.carpartsalpha.models.CartItem
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.ui.activities.AddEditAddressActivity
import com.example.carpartsalpha.ui.activities.CheckoutActivity

open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemAddressLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.binding.tvAddressFullName.text = model.name
            holder.binding.tvAddressType.text = model.type
            holder.binding.tvAddressDetails.text = "${model.address}, ${model.zipCode}"
            holder.binding.tvAddressMobileNumber.text = model.mobileNumber

            if(selectAddress){
                holder.itemView.setOnClickListener {
                   val intent = Intent(context , CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
         return list.size
    }
    // Create a function to function to edit the address details and pass the existing details through intent.
    /**
     * A function to edit the address details and pass the existing details through intent.
     *
     * @param activity
     * @param position
     */
    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        //  Pass the address details through intent to edit the address.
        // START
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        // END
        activity.startActivityForResult(intent,Constants.ADD_ADDRESS_REQUEST_CODE)

        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    private class MyViewHolder(val binding: ItemAddressLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}