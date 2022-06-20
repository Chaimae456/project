package com.example.carpartsalpha.ui.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivitySettingsBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.User
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.outils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener  {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       setupActionBar()

        binding.tvEdit.setOnClickListener(this@SettingsActivity)
        binding.btnLogout.setOnClickListener(this@SettingsActivity)
        binding.llAddress.setOnClickListener(this)
    }
    override fun onResume() {
        super.onResume()

        getUserDetails()
    }



    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                //  Call the User Profile Activity to add the Edit Profile feature to the app. Pass the user details through intent.
                // START
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                // END

                //  Add Logout feature when user clicks on logout button.
                // START
                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address->{
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
                // END
            }
        }
    }
    // END

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }
    }
    // END

    /**
     * A function to get the user details from firestore.
     */
    private fun getUserDetails() {

        // Show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class to get the user details from firestore which is already created.
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }
    // END

    /** * A function to receive the user details and populate it in the UI.
     */
    fun userDetailsSuccess(user: User) {

        mUserDetails = user
        // START
        // Hide the progress dialog
        hideProgressDialog()

        // Load the image using the Glide Loader class.
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
        // END
    }
    // END


}