package com.example.carpartsalpha.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.carpartsalpha.R
import com.example.carpartsalpha.databinding.ActivityUserProfileBinding
import com.example.carpartsalpha.firestore.FirestoreClass
import com.example.carpartsalpha.models.User
import com.example.carpartsalpha.outils.Constants
import com.example.carpartsalpha.outils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var userDetails : User
    private lateinit var binding: ActivityUserProfileBinding
    private var mSelectImageFileUri: Uri? = null
    private var mUserProfileImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Create a instance of the User model class.

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra.
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        // END
        if (userDetails.profileCompleted == 0) {
            // Update the title of the screen to complete profile.
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)

            // Here, the some of the edittext components are disabled because it is added at a time of Registration.
            binding.etFirstName.isEnabled = false
            binding.etFirstName.setText(userDetails.firstName)

            binding.etLastName.isEnabled = false
            binding.etLastName.setText(userDetails.lastName)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(userDetails.email)
        } else {

            // Call the setup action bar function.

            // Update the title of the screen to edit profile.
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)

            // Load the image using the GlideLoader class with the use of Glide Library.
            GlideLoader(this@UserProfileActivity).loadUserPicture(userDetails.image, binding.ivUserPhoto)

            // Set the existing values to the UI and allow user to edit except the Email ID.
            binding.etFirstName.setText(userDetails.firstName)
            binding.etLastName.setText(userDetails.lastName)

            binding.etEmail.isEnabled = false
            binding.etEmail.setText(userDetails.email)

            if (userDetails.mobile != 0L) {
                binding.etMobileNumber.setText(userDetails.mobile.toString())
            }
            if (userDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }
        }
        // Assign the on click event to the user profile photo.
        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        // Assign the on click event to the SAVE button.
        binding.btnSubmit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(V: View?) {
        if (V != null) {
            when (V.id) {

                R.id.iv_user_photo -> {

                    // Here we will check if the permission is already allowed or we need to request for it.
                    // First of all we will check the READ_EXTERNAL_STORAGE permission and if it is not allowed we will request for the same.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {

                        Constants.showImageChooser(this)
                    } else {

                        /*Requests permissions to be granted to this application. These permissions
                         must be requested in your manifest, they should not be granted to your app,
                         and they should have protection level*/

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if(validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if(mSelectImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(
                                this@UserProfileActivity,
                                mSelectImageFileUri,
                                Constants.USER_PROFILE_IMAGE)
                        }else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }
    // END





    /**
     * This function will identify the result of runtime permission after the user allows or deny permission based on the unique code.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    // END
    private fun updateUserProfileDetails(){

        // hashmap is collection that contain pair of objects
        val userHashMap = HashMap<String,Any>()
        // Here the field which are not editable needs no update. So, we will update user Mobile Number and Gender for now.
        // Get the FirstName from editText and trim the space
        val firstName = binding.etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != userDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        // Get the LastName from editText and trim the space
        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != userDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        // Here we get the text from editText and trim the space
        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked ) {Constants.MALE} else {Constants.FEMALE}


        if(mUserProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageUrl
        }

        if (gender.isNotEmpty() && gender != userDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != userDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        userHashMap[Constants.GENDER] = gender

        if (userDetails.profileCompleted == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }
        // call the registerUser function of FireStore class to make an entry in the database.
        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )

        // END

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        // The uri of selected image from phone storage.
                        mSelectImageFileUri = data.data!!

                       // binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                        GlideLoader(this@UserProfileActivity).loadUserPicture(mSelectImageFileUri!!,binding.ivUserPhoto)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    /**
     * A function to validate the input entries for profile details.
     */
    private fun validateUserProfileDetails(): Boolean {
        return when {

            // We have kept the user profile picture is optional.
            // The FirstName, LastName, and Email Id are not editable when they come from the login screen.
            // The Radio button for Gender always has the default selected value.

            // Check if the mobile number is not empty as it is mandatory to enter.
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }
    }
    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity ,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity , DashboardActivity::class.java))
        finish()
    }
    /**
     * A function to notify the success result of image upload to the Cloud Storage.
     *
     * @param imageURL After successful upload the Firebase Cloud returns the URL.
     */
    fun imageUploadSuccess(imageURL: String) {
            mUserProfileImageUrl = imageURL
            updateUserProfileDetails()
    }
    // END
}