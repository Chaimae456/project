package com.example.carpartsalpha.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityProductDetailsBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.outils.Constants
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.carpartsalpha.models.CartItem
import com.example.carpartsalpha.outils.GlideLoader

class ProductDetailsActivity : BaseActivity() , View.OnClickListener {
    private  var ProductId: String = ""
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var ProductDtails : Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            ProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product ID" , ProductId)
        }
        var productOwnerId : String = ""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Product owner ID" , productOwnerId)
        }

        if(FirestoreClass().getCurrentUserID() == productOwnerId){
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        }else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }


        binding.btnAddToCart.setOnClickListener(this)

        binding.btnGoToCart.setOnClickListener(this)
        setupActionBar()

        getProductDetails()
    }

    // TODO Step 7: Create a function to notify the success result of item exists in the cart.
    // START
    /**
     * A function to notify the success result of item exists in the cart.
     */
    fun productExistsInCart() {

        // Hide the progress dialog.
        hideProgressDialog()

        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }



    // Create a function to setup the action bar.
    // START
    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    /**
     * A function to call the firestore class function that will get the product details from cloud firestore based on the product id.
     */
    private fun getProductDetails() {

        // Show the product dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of FirestoreClass to get the product details.
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, ProductId)
    }
    // END
    /**
     * A function to notify the success result of the product details based on the product id.
     *
     * @param product A model class with product details.
     */
    fun productDetailsSuccess(product: Product) {
         hideProgressDialog()
        ProductDtails = product
        // Populate the product details in the UI.
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "${product.price} MAD"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
        hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.text = resources.getString(R.string.lbl_out_of_stock)
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(this , R.color.colorSnackBarError)
            )
        }else{
            // Call the function to check the product exist in the cart or not from the firestore class.
            // START
            // There is no need to check the cart list if the product owner himself is seeing the product details.
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                // Hide Progress dialog.
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, ProductId)
            }
            // END
        }

    }

    /**
     * A function to prepare the cart item to add it to the cart.
     */
    private fun addToCart() {

        val addToCart = CartItem(
            FirestoreClass().getCurrentUserID(),
            ProductId,
            ProductDtails.title,
            ProductDtails.price,
            ProductDtails.image,
            Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)

    }

    override fun onClick(v: View?) {
        //  Handle the click event of the Add to cart button and call the addToCart function.
        // START
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart -> {

                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
        // END
    }
    // END

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // Change the buttons visibility once the item is added to the cart.
        // Hide the AddToCart button if the item is already in the cart.
        binding.btnAddToCart.visibility = View.GONE
        // Show the GoToCart button if the item is already in the cart. User can update the quantity from the cart list screen if he wants.
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}