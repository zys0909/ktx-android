package com.dev.zhaoys.base

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dev.zhaoys.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        lifecycleScope.launch(context = Dispatchers.Main, block = {
            init(savedInstanceState)
        })
    }

    @LayoutRes
    protected abstract fun layoutId(): Int

    protected abstract suspend fun init(savedInstanceState: Bundle?)

    protected fun initToolbar(title: CharSequence? = null, showHomeAsUp: Boolean = true) {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
        supportActionBar?.title = title
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when {
        item?.itemId == android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    protected fun dial(phone: String?) {
        when {
            phone.isNullOrEmpty() -> {
                AlertDialog.Builder(this).setMessage(String.format(Locale.CHINA, "您要拨打的号码为空！"))
                    .setPositiveButton("确定", null).create().show()
            }
            else -> {
                AlertDialog.Builder(this).setMessage(String.format(Locale.CHINA, "确认拨打:$phone"))
                    .setPositiveButton("确定") { _, _ -> dialing(phone) }
                    .setNegativeButton("取消", null).create().show()
            }
        }
    }

    private var phone = ""

    private fun dialing(phone: String) {
        if (Build.VERSION.SDK_INT >= 23) {
            this.phone = phone
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 999)
        } else {
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999 && phone.isNotEmpty())
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")))
    }
}