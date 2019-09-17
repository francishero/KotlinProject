package com.webaddicted.kotlinproject.view.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.android.boxlty.global.annotationDef.MediaPickerType
import com.android.boxlty.global.common.showImage
import com.android.boxlty.view.dialog.ImagePickerDialog
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.databinding.ActivityProfileBinding
import com.webaddicted.kotlinproject.global.common.GlobalUtility
import com.webaddicted.kotlinproject.view.base.BaseFragment
import com.webaddicted.kotlinproject.view.interfac.OnImageActionListener
import java.io.File

class ProfileFrm : BaseFragment() {
    private lateinit var imgPickerDialog: ImagePickerDialog
    private lateinit var mBinding: ActivityProfileBinding

    companion object {
        val TAG = ProfileFrm::class.java.simpleName
        fun getInstance(bundle: Bundle): ProfileFrm {
            val fragment = ProfileFrm()
            fragment.setArguments(bundle)
            return ProfileFrm()
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_profile
    }

    override fun onViewsInitialized(binding: ViewDataBinding?, view: View) {
        mBinding = binding as ActivityProfileBinding
        init()
        clickListener();
    }

    private fun init() {
        mBinding.toolbar.imgBack.visibility = View.VISIBLE
        mBinding.toolbar.txtToolbarTitle.text = resources.getString(R.string.my_profile)
    }

    private fun clickListener() {
        mBinding.btnCaptureImage.setOnClickListener(this)
        mBinding.btnPickImage.setOnClickListener(this)
        mBinding.toolbar.imgBack.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btn_capture_image -> {
                requestCamera(MediaPickerType.CAPTURE_IMAGE)
            }
            R.id.btn_pick_image -> {
                requestCamera(MediaPickerType.SELECT_IMAGE)
            }
            R.id.img_back -> activity?.onBackPressed()
        }
    }
    private fun requestCamera(@MediaPickerType.MediaType captureImage: Int) {
        imgPickerDialog = ImagePickerDialog.dialog(captureImage,
            object : OnImageActionListener {
                override fun onAcceptClick(file: List<File>) {
                    mBinding.imgProfile.showImage(file.get(0), getPlaceHolder(1))
                }
            })
        imgPickerDialog?.show(fragmentManager, ImagePickerDialog.TAG)
    }

}

