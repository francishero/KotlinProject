package com.webaddicted.kotlinproject.view.deviceinfo

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.webaddicted.kotlinproject.R
import com.webaddicted.kotlinproject.databinding.FrmDevProcessorBinding
import com.webaddicted.kotlinproject.global.common.FileUtils.Companion.calculatePercentage
import com.webaddicted.kotlinproject.global.common.FileUtils.Companion.formatSize
import com.webaddicted.kotlinproject.model.bean.deviceinfo.FeaturesHW
import com.webaddicted.kotlinproject.view.adapter.CPUAdapter
import com.webaddicted.kotlinproject.view.base.BaseFragment
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class CPUFrm : BaseFragment() {
    private lateinit var mAdapter: CPUAdapter
    private lateinit var mBinding: FrmDevProcessorBinding
    private var cpuList: ArrayList<FeaturesHW>? = ArrayList<FeaturesHW>()

    companion object {
        val TAG = CPUFrm::class.java.simpleName
        fun getInstance(bundle: Bundle): CPUFrm {
            val fragment = CPUFrm()
            fragment.arguments = bundle
            return CPUFrm()
        }
    }

    override fun getLayout(): Int {
        return R.layout.frm_dev_processor
    }

    override fun initUI(binding: ViewDataBinding?, view: View) {
        mBinding = binding as FrmDevProcessorBinding
        init()
    }

    private fun init() {
        setAdapter()
        GlobalScope.launch(Dispatchers.Main + Job()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) getMemoryInfo()
            val totalRamValue = totalRamMemorySize()
            val freeRamValue = freeRamMemorySize()
            val usedRamValue = totalRamValue - freeRamValue
            mBinding.arcRam.progress =
                calculatePercentage(usedRamValue.toDouble(), totalRamValue.toDouble())
            withContext(Dispatchers.Default) {
                GlobalScope.launch(Dispatchers.Main + Job()) {
                    val appList = withContext(Dispatchers.Default) {
                        getCpuInfoMap()
                    }
                    mAdapter.notifyAdapter(appList)
                }

            }
        }
    }

    private fun setAdapter() {
        mAdapter = CPUAdapter(cpuList)
        mBinding.rvCpuFeature.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mBinding.rvCpuFeature.adapter = mAdapter
    }

    private fun getCpuInfoMap(): ArrayList<FeaturesHW> {
        val lists = ArrayList<FeaturesHW>()
        try {
            val s = Scanner(File("/proc/cpuinfo"))
            while (s.hasNextLine()) {
                val vals = s.nextLine().split(": ")
                if (vals.size > 1) {
                    lists.add(FeaturesHW(vals[0].trim { it <= ' ' }, vals[1].trim { it <= ' ' }))
                }
            }
        } catch (e: Exception) {
            Log.e("getCpuInfoMap", Log.getStackTraceString(e))
        }
        return lists
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun getMemoryInfo() {
        val activityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        val freeMemory = memoryInfo.availMem
        val totalMemory = memoryInfo.totalMem
        val usedMemory = freeMemory.let { totalMemory.minus(it) }
        mBinding.tvSystemAppsMemory.text =
            resources.getString(R.string.system_and_apps) + ":  ".plus(formatSize(usedMemory))
        mBinding.tvAvailableRam.text =
            resources.getString(R.string.available_ram) + ":  ".plus(formatSize(freeMemory))
        mBinding.tvTotalRamSpace.text =
            resources.getString(R.string.total_ram_space) + ":  ".plus(formatSize(totalMemory))
    }


    private fun freeRamMemorySize(): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)

        return mi.availMem
    }

    private fun totalRamMemorySize(): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi.totalMem
    }
}
