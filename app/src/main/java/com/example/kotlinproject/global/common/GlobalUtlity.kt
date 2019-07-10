package com.example.kotlinproject.global.common

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import com.bumptech.glide.DrawableTypeRequest
import com.bumptech.glide.Glide
import com.example.kotlinproject.R
import com.example.kotlinproject.global.common.AppApplication.Companion.context
import com.example.kotlinproject.global.common.AppApplication.Companion.mCurrencyActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.reflect.Modifier
import java.util.*
/**
 * Created by Deepak Sharma on 01/07/19.
 */
class GlobalUtility {

    companion object {

        fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        fun datePicker(dateListener: DatePickerDialog.OnDateSetListener): DatePickerDialog {
            val calendar = Calendar.getInstance()
            return DatePickerDialog(
                mCurrencyActivity,
                R.style.TimePicker,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun timePicker(timeListener: TimePickerDialog.OnTimeSetListener): TimePickerDialog {
            val calendar = Calendar.getInstance()
            return TimePickerDialog(
                mCurrencyActivity,
                R.style.TimePicker,
                timeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(context)
            )
        }

//    {START HIDE SHOW KEYBOARD}

        /**
         * Method to hide keyboard
         *
         * @param activity Context of the calling class
         */
        fun hideKeyboard(activity: Activity) {
            try {
                val inputManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            } catch (ignored: Exception) {
                Log.d("TAG", "hideKeyboard: "+ignored.message)
            }

        }

        /***
         * Show SoftInput Keyboard
         * @param activity reference of current activity
         */
        fun showKeyboard(activity: Activity?) {
            if (activity != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }

//    {END HIDE SHOW KEYBOARD}


//      {START STRING TO JSON & JSON TO STRING}

        /**
         * @param json  json String converted by Gson to string
         * @param clazz referance of class type like MyBean.class
         * @param <T>
         * @return bean referance
        </T> */
        fun <T> stringToJson(json: String, clazz: Class<T>): T {
            return GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
                .create().fromJson(json, clazz)
        }

        /**
         * @param clazz referance of any bean
         * @return
         */
        fun jsonToString(clazz: Class<*>): String {
            return Gson().toJson(clazz)
        }

        //{END STRING TO JSON & JSON TO STRING}

        /**
         * Gets network state.
         *
         * @return the network state
         */
        fun isNetworkAvailable(): Boolean {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connMgr?.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
        }

        /**
         * show internet connection toast
         */
        fun showNoNetworkToast() {
            val msg = context.getResources().getString(R.string.no_network_msg)
            showToast(msg)
        }


//    {START SHOW IMAGE USING GLIDE}

        /**
         * show image using glide
         *
         * @param imagePath       image url
         * @param targetIv        image view
         * @param imageLoaderType type of place holder show which is define in string
         */
        fun showImageUsingGLIDE(imagePath: String, targetIv: ImageView, imageLoaderType: String) {
            val drawableTypeRequest = Glide.with(targetIv.context)
                .load(imagePath)
            getPlaceHolder(drawableTypeRequest, imageLoaderType).into(targetIv)
        }


        //block up when loder show on screen
        /**
         * handle ui
         *
         * @param activity
         * @param view
         * @param isBlockUi
         */
        fun handleUI(activity: Activity, view: View, isBlockUi: Boolean) {
            if (isBlockUi) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                view.visibility = View.VISIBLE
            } else {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                view.visibility = View.GONE
            }
        }

        /**
         * show image using glide
         *
         * @param imgUrl          local image file path
         * @param targetIv        image view
         * @param imageLoaderType type of place holder show which is define in string
         */
        fun showImageUsingGLIDE(imgUrl: File, targetIv: ImageView, imageLoaderType: String) {
            val drawableTypeRequest = Glide.with(targetIv.context)
                .load(Uri.fromFile(imgUrl))
            getPlaceHolder(drawableTypeRequest, imageLoaderType).into(targetIv)
        }

        /**
         * apply different type of place holder
         *
         * @param drawableTypeRequest
         * @param imageLoaderType     type of place holder show
         * @param <T>
         * @return
        </T> */
        private fun <T> getPlaceHolder(
            drawableTypeRequest: DrawableTypeRequest<T>,
            imageLoaderType: String
        ): DrawableTypeRequest<T> {
            val imageLoadersArray = context.getResources().getStringArray(R.array.image_loader)
            if (imageLoadersArray[0] == imageLoaderType) {
                drawableTypeRequest.error(R.color.app_color)
                drawableTypeRequest.placeholder(R.color.app_color)
            } else if (imageLoadersArray[1] == imageLoaderType) {
                drawableTypeRequest.error(R.drawable.logo)
                drawableTypeRequest.placeholder(R.drawable.logo)
            } else {
                drawableTypeRequest.error(R.color.app_color)
                drawableTypeRequest.placeholder(R.color.app_color)
            }
            return drawableTypeRequest
        }

        //    {END SHOW IMAGE USING GLIDE}
    }
}