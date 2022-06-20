package com.example.carpartsalpha.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityAddEditAddressBinding
import com.example.carpartsalpha.databinding.ActivityAddressListBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.Address
import com.example.carpartsalpha.outils.Constants

class AddEditAddressActivity : BaseActivity() {


    private var addressDetails: Address? = null
    private lateinit var binding: ActivityAddEditAddressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupActionBar()

        // TODO Step 9: Receive the address details through intent and initialize the global variable.
        // START
        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            addressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
        // END

        if (addressDetails != null) {
            if (addressDetails!!.id.isNotEmpty()) {

                binding.tvTitle.text = resources.getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)

                binding.etFullName.setText(addressDetails?.name)
                binding.etPhoneNumber.setText(addressDetails?.mobileNumber)
                binding.etAddress.setText(addressDetails?.address)
                binding.etZipCode.setText(addressDetails?.zipCode)
                binding.etAdditionalNote.setText(addressDetails?.additionalNote)

                when (addressDetails?.type) {
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }
                    Constants.WORK -> {
                        binding.rbWork.isChecked = true
                    }
                    else -> {
                        binding.rbOther.isChecked = true
                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(addressDetails?.otherDetails)
                    }
                }
            }

        }
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarAddEditAddressActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }


        binding.btnSubmitAddress.setOnClickListener {
           saveAddressToFirestore()
        }



        //  Assign the checked change listener on click of radio buttons for the address type.

         /* binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetails.visibility = View.GONE
            }
        } */
    }


    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(binding.etFullName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }


    // Create a function to save the address to the cloud firestore.
    // START
    /**
     * A function to save the address to the cloud firestore.
     */
    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }
        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }
        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }
        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }
        val otherDetails: String = binding.rbOther.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME
                }
                binding.rbWork.isChecked -> {
                    Constants.WORK
                }
                else -> {
                    Constants.OTHER
                }
            }

            // Prepare address info in data model class.
            // START
            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )
            // END

            if (addressDetails != null && addressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    addressDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }
    // Create a function to notify the success result of address saved.
    // START
    /**
     * A function to notify the success result of address saved.
     */
    fun addUpdateAddressSuccess() {
        hideProgressDialog()
        val notificationMessage: String = if (addressDetails != null && addressDetails!!.id.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.err_your_address_added_successfully)
        }
        // Hide progress dialog


        Toast.makeText(
            this@AddEditAddressActivity,
            notificationMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }
// END
}
