package com.whitbread.premierinn.qrkiosk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.databinding.ActivityQrCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val KIOSK_VIEW_INPUT = "KIOSK_VIEW_INPUT"

@AndroidEntryPoint
class QRCodeActivity : BaseActivity<ActivityQrCodeBinding>() {

    private val viewModel: QRCodeViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater): ActivityQrCodeBinding {
        return ActivityQrCodeBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return binding.toolbar
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar(getString(R.string.qr_code_title), true)

        viewModel.init()
        
        initViews()
        observeViewState()
    }

    private fun observeViewState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    showLoadingSpinner(state.isLoading)
                    when {
                        state.error != null -> {
                            showQrCodeFailedPopup()
                        }
                        state.qrCode != null -> {
                            showQrCode(state.qrCode)
                        }
                    }
                }
            }
        }
    }

    private fun showQrCode(qrCode: Bitmap?) {
        binding.ivQrCode.setImageBitmap(qrCode)
        binding.ivQrCode.visibility = View.VISIBLE
        binding.ivQrCodeIcon.visibility = View.VISIBLE
    }

    private fun showLoadingSpinner(visibility: Boolean) {
        binding.qrCodeLoadingSpinner.isVisible = visibility
    }

    private fun initViews() {
        binding.ivQrCode.visibility = View.INVISIBLE
        binding.ivQrCodeIcon.visibility = View.INVISIBLE
    }


    private fun showQrCodeFailedPopup() {
        androidx.appcompat.app.AlertDialog.Builder(this, R.style.PurpleDialog)
            .setTitle(getString(R.string.generic_error_title))
            .setMessage(getString(R.string.generic_error_message_with_try_again))
            .setPositiveButton(getText(android.R.string.ok))
            { _, _ ->
                setResult(Activity.RESULT_OK)
                finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_qrcode_info, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_qrcode -> {
                startActivity(QRCodeInfoActivity.createIntent(this))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context, reference: String): Intent? {
            return Intent(context, QRCodeActivity::class.java).apply {
                putExtra(KIOSK_VIEW_INPUT, reference)
            }
        }
    }

}