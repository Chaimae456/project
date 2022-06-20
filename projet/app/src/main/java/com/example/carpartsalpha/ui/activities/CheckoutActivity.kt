package com.example.carpartsalpha.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityAddressListBinding
import com.example.carpartsalpha.databinding.ActivityCheckoutBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.Address
import com.example.carpartsalpha.models.CartItem
import com.example.carpartsalpha.models.Order
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.ui.adapter.CartItemsListAdapter

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var addressDetails: Address? = null
    private lateinit var ProductsList: ArrayList<Product>
    private lateinit var CartItemsList: ArrayList<CartItem>
    private var SubTotal: Double = 0.0
    private var TotalAmount: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()
        getProductList()
        //Get the selected address details through intent.
        // START
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            addressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        // END

        // TODO Step 5: Set the selected address details to UI that is received through intent.
        // START
        if (addressDetails != null) {
            binding.tvCheckoutAddressType.text = addressDetails?.type
            binding.tvCheckoutFullName.text = addressDetails?.name
            binding.tvCheckoutAddress.text = "${addressDetails!!.address}, ${addressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = addressDetails?.additionalNote

            if (addressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = addressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = addressDetails?.mobileNumber
        }

        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }

    }



    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Create a function to get product list to compare it with the cart items stock.
    // START
    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
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

        // Initialize the global variable of all product list.
        // START
        ProductsList = productsList
        // END

        //  Call the function to get the latest cart items.
        // START
        getCartItemsList()
        // END
    }

    //TODO Step 2: Create a function to prepare the Order details to place an order.
    // START
    /**
     * A function to prepare the Order details to place an order.
     */
    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Now prepare the order details based on all the required details.
        // START
        val order = Order(
            FirestoreClass().getCurrentUserID(),
            CartItemsList,
            addressDetails!!,
            "My order ${System.currentTimeMillis()}",
            CartItemsList[0].image,
            SubTotal.toString(),
            "35.0", // The Shipping Charge is fixed as $10 for now in our case.
            TotalAmount.toString(),
        )
        // END

        //  Call the function to place the order in the cloud firestore.
        // START
        FirestoreClass().placeOrder(this@CheckoutActivity, order)
        // END
    }
    // END


    //  Create a function to get the list of cart items in the activity.
    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }
// END
//  Create a function to notify the success result of the cart items list from cloud firestore.
    // START
    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()
        //  Update the stock quantity in the cart list from the product list.
        // START
        for (product in ProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        // Initialize the cart list.
        // START
        CartItemsList = cartList
        // END

        //Populate the cart items in the UI.
        // START
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, CartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter


        for (item in CartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                SubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = "$SubTotal MAD"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        binding.tvCheckoutShippingCharge.text = "35.0 MAD"

        if (SubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            TotalAmount = SubTotal + 35
            binding.tvCheckoutTotalAmount.text = "$TotalAmount MAD"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
    }
    //  Create a function to notify the success result of the order placed.
    // START
    /**
     * A function to notify the success result of the order placed.
     */
    fun orderPlacedSuccess() {

        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    // END

}