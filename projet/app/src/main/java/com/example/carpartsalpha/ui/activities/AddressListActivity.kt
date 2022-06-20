package com.example.carpartsalpha.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityAddressListBinding
import com.example.carpartsalpha.databinding.ActivitySettingsBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.Address
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.outils.SwipeToDeleteCallback
import com.example.carpartsalpha.outils.SwipeToEditCallback
import com.example.carpartsalpha.ui.adapter.AddressListAdapter

class AddressListActivity : BaseActivity() {
    private var SelectAddress: Boolean = false
    private lateinit var binding: ActivityAddressListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()



        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)

            // Now to notify the address list about the latest address added we need to make neccessary changes as below.
            // START
            // startActivity(intent)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
            // END
        }
        getAddressList()
        // : If it is about to select the address then update the title.
        // START
        if (SelectAddress) {
            binding.tvTitle.text = resources.getString(R.string.title_select_address)
        }
        // END
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            SelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddressListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }

    }



    // Override the onActivityResult function and get the latest address list based on the result code.
    // START
    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
    }
    // END

    //  Create a function to get the success result of address list from cloud firestore.
    // START
    /**
     * A function to get the success result of address list from cloud firestore.
     *
     * @param addressList
     */
    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // Hide the progress dialog
        hideProgressDialog()


        // Print all the list of addresses in the log with name.
        // for (i in addressList) {

           //  Log.i("Name and Address", "${i.name} ::  ${i.address}")
        //}

        if (addressList.size > 0) {

            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE

            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList ,SelectAddress)
            binding.rvAddressList.adapter = addressAdapter

            //  Add the swipe to edit feature.
            // START
            if (!SelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        //  Call the notifyEditItem function of the adapter class.
                        // START
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                        // END
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        // Call the function to delete the address from cloud firetore.
                        // START
                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                        // END
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)
                // END
                // END
            }

        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }
    }

    //  Create a function to get the list of address from cloud firestore.
    // START
    /**
     * A function to get the list of address from cloud firestore.
     */
    private fun getAddressList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAddressesList(this@AddressListActivity)
    }
    // END
    //  Create a function to notify the user that the address is deleted successfully.
    // START
    /**
     * A function notify the user that the address is deleted successfully.
     */
    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
}