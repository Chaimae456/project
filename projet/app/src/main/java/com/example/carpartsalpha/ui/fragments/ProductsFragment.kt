package com.example.carpartsalpha.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.FragmentProductsBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.Product
import com.example.carpartsalpha.ui.activities.AddProductActivity
import com.example.carpartsalpha.ui.adapter.ProductListsAdapter

class   ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If we want to use the option menu in fragment we need to add it.
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      //  val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //  Override the onCreateOptionMenu function and inflate the Dashboard menu file init.
    // START
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_products_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    // END

    //  Override the onOptionItemSelected function and handle the action items init.
    // START
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {

            R.id.navigation_add_products -> {

                // Launch the SettingActivity on click of action item.
                // START
                startActivity(Intent(activity, AddProductActivity::class.java))
                // END
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // END

    /**
     * A function to get the successful product list from cloud firestore.
     *
     * @param productsList Will receive the product list from cloud firestore.
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // Hide Progress dialog.
        hideProgressDialog()

        if (productsList.size > 0) {

           binding.rvMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE

             binding.rvMyProductItems.layoutManager = LinearLayoutManager(activity)
             binding.rvMyProductItems.setHasFixedSize(true)

             // Pass the third parameter value.
             // START
             val adapterProducts =
                 ProductListsAdapter(requireActivity(), productsList , this)
             // END
              binding.rvMyProductItems.adapter = adapterProducts

        }
        else {
            binding.rvMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        getProductListFromFireStore()
    }

    private fun getProductListFromFireStore() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getProductsList(this@ProductsFragment)
    }
    /**
     * A function that will call the delete function of FirestoreClass that will delete the product added by the user.
     *
     * @param productID To specify which product need to be deleted.
     */
    fun deleteProduct(productID: String) {

        // Here we will call the delete function of the FirestoreClass. But, for now lets display the Toast message and call this function from adapter class.
        showAlertDialogToDeleteProduct(productID)
    }
    // END

    fun productDeleteSuccess(){

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        // Get the latest products list from cloud firestore.
        getProductListFromFireStore()
    }

    /**
     * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
     */
    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(R.drawable.drawable_gender_tap_selector)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            //  Call the function to delete the product from cloud firestore.
            // START
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Call the function of Firestore class.
            FirestoreClass().deleteProduct(this@ProductsFragment, productID)
            // END

            dialogInterface.dismiss()
        }

        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    // END
}