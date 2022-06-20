package com.example.carpartsalpha.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ItemCartLayoutBinding
import com.example.carpartsalpha.databinding.ItemDashboardLayoutBinding
import com.example.carpartsalpha.databinding.ItemListLayoutBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.CartItem
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.outils.GlideLoader
import com.example.carpartsalpha.ui.activities.CartListActivity

open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItems : Boolean
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemCartLayoutBinding.inflate(
                    LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(
                model.image,
                holder.binding.ivCartItemImage
            )
            holder.binding.tvCartItemTitle.text = model.title
            holder.binding.tvCartItemPrice.text = "${model.price} MAD"
            holder.binding.tvCartQuantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.binding.ibRemoveCartItem.visibility = View.GONE
                holder.binding.ibAddCartItem.visibility = View.GONE

                //  Update the UI components as per the param.
                // START
                if (updateCartItems) {
                    holder.binding.ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    holder.binding.ibDeleteCartItem.visibility = View.GONE
                }

                holder.binding.tvCartQuantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {

                if(updateCartItems){
                    holder.binding.ibRemoveCartItem.visibility = View.VISIBLE
                    holder.binding.ibAddCartItem.visibility = View.VISIBLE
                    holder.binding.ibDeleteCartItem.visibility= View.VISIBLE
                }else{
                    holder.binding.ibRemoveCartItem.visibility = View.GONE
                    holder.binding.ibAddCartItem.visibility = View.GONE
                    holder.binding.ibDeleteCartItem.visibility= View.GONE
                }


                holder.binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.lightGray
                    )
                )
            }


            // T Assign the onclick event to the ib_delete_cart_item.
            // START
            holder.binding.ibDeleteCartItem.setOnClickListener {

                //  Call the firestore class function to remove the item from cloud firestore.
                // START

                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.id)
                // END
            }
            // END
            // Assign the click event to the ib_remove_cart_item.
            // START
            holder.binding.ibRemoveCartItem.setOnClickListener {

                // Call the update or remove function of firestore class based on the cart quantity.
                // START
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
                // END
            }
            // END
            holder.binding.ibAddCartItem.setOnClickListener {
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }



            //

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}






