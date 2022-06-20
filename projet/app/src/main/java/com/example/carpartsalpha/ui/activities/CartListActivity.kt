package com.example.carpartsalpha.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityCartListBinding
import com.example.carpartsalpha.databinding.ActivityLoginBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.CartItem
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.ui.adapter.CartItemsListAdapter

class CartListActivity : BaseActivity() {
    private lateinit var binding: ActivityCartListBinding
    private lateinit var ProductsList: ArrayList<Product>
    private lateinit var CartListItems: ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //  Assign the click event to the checkout button and proceed to the next screen.
        // START
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
        // END
        setupActionBar()
    }
    // Override the onResume function and call the function to getCartItemsList.
    // START
    override fun onResume() {
        super.onResume()

       //  getCartItemsList()

        getProductList()
    }
    // END


    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCartListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END
    //  Create a function to get the list of cart items in the activity.
    // START
    private fun getCartItemsList() {

        // Show the progress dialog.
        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getCartList(this@CartListActivity)
    }
    // END



    // Create a function to notify the success result of the cart items list from cloud firestore.
    // START
    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     */
    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        //  Compare the product id of product list with product id of cart items list and update the stock quantity in the cart items list from the latest product list.
        // START
        for (product in ProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {

                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }
        //  Initialize the global variable of cart list items.
        // START
        CartListItems = cartList


        // Now onwards use the global variable of the cart list items as mCartListItems instead of cartList.
        // Hide progress dialog.
        hideProgressDialog()
        if (CartListItems.size > 0) {

            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding.rvCartItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, CartListItems , true)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in CartListItems) {
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()

                    subTotal += (price * quantity)
                }
            }

            binding.tvSubTotal.text = "$subTotal MAD"
            // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
            binding.tvShippingCharge.text = "35.0 MAD"

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE

                val total = subTotal + 35
                binding.tvTotalAmount.text = "$total MAD"
            } else {
                binding.llCheckout.visibility = View.GONE
            }

        } else {
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }
    //  Create a function to get product list to compare the current stock with the cart items.
    // START
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CartListActivity)
    }
    // END
    //  Create a function to get the success result of product list.
    // START
    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        //  Initialize the product list global variable once we have the product list.
        // START
        ProductsList = productsList
        // END

        //  Once we have the latest product list from cloud firestore get the cart items list from cloud firestore.
        // START
        getCartItemsList()

        // END
    }
    // END
    // TODO Step 5: Create a function to notify the user about the item removed from the cart list.
    // START
    /**
     * A function to notify the user about the item removed from the cart list.
     */
    fun itemRemovedSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }


    // Create a function to notify the user about the item quantity updated in the cart list.
    // START
    /**
     * A function to notify the user about the item quantity updated in the cart list.
     */
    fun itemUpdateSuccess() {

        hideProgressDialog()

        getCartItemsList()
    }
    // END


    }
    // END


