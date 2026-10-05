package com.whitbread.premierinn.qrkiosk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityQrCodeInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QRCodeInfoActivity : BaseActivity<ActivityQrCodeInfoBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ActivityQrCodeInfoBinding {
        return ActivityQrCodeInfoBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
       return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(getString(R.string.qr_code_info_title), true)

    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, QRCodeInfoActivity::class.java)
        }
    }

}