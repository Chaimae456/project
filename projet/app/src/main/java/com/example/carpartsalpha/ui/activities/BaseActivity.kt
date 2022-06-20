package com.example.carpartsalpha.ui.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.carpartsalpha.R
import com.example.carpartsalpha.outils.TextView
import com.google.android.material.snackbar.Snackbar

 open class BaseActivity : AppCompatActivity() {
     private var doubleBackToExitPressedOnce = false
     /**
      * This is a progress dialog instance which we will initialize later on.
      */
     private lateinit var mProgressDialog: Dialog
     // END


    /**
     * A function to show the success and error messages in snack bar component.
     */
    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity,
                    R.color.colorSnackBarSuccess
                )
            )
        }
        snackBar.show()
    }
    // END
     /**
      * This function is used to show the progress dialog with the title and message to user.
      */
     fun showProgressDialog(text: String) {
         mProgressDialog = Dialog(this)

         /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
         mProgressDialog.setContentView(R.layout.dialog_progress)

         mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text

         mProgressDialog.setCancelable(false)
         mProgressDialog.setCanceledOnTouchOutside(false)

         //Start the dialog and display it on screen.
         mProgressDialog.show()
     }

     /**
      * This function is used to dismiss the progress dialog if it is visible to user.
      */
     fun hideProgressDialog() {
         mProgressDialog.dismiss()
     }
     /**
      * A function to implement the double back press feature to exit the app.
      */
     fun doubleBackToExit() {

         if (doubleBackToExitPressedOnce) {
             super.onBackPressed()
             return
         }

         this.doubleBackToExitPressedOnce = true

         Toast.makeText(
             this,
             resources.getString(R.string.please_click_back_again_to_exit),
             Toast.LENGTH_SHORT
         ).show()

         @Suppress("DEPRECATION")
         Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
     }


     // END
}