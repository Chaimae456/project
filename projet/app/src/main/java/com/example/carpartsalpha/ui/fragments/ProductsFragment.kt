package com.example.carpartsalpha.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
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
                 ProductListsAdapter(requireActivity(), productsList)
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
}