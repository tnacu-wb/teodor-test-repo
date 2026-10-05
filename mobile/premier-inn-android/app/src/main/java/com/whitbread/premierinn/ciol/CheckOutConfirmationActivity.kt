package com.whitbread.premierinn.ciol

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.databinding.ActivityCheckOutConfirmationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckOutConfirmationActivity : BaseActivity<ActivityCheckOutConfirmationBinding>() {

    override fun inflateBinding(inflater: LayoutInflater): ActivityCheckOutConfirmationBinding {
        return ActivityCheckOutConfirmationBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return null;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.doneButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        val name = intent.extras?.getString(GUEST_NAME_EXTRA) ?: EMPTY_STRING
        binding.checkedOutTitle.text = getString(R.string.checkout_confirmation_title, name)
    }

    companion object {
        const val REQUEST_CODE = 10003
        private const val GUEST_NAME_EXTRA = "guest_name_extra"

        fun newIntent(context: Context, guestName: String) : Intent  =
                Intent(context, CheckOutConfirmationActivity::class.java).apply {
                    putExtra(GUEST_NAME_EXTRA, guestName)
                }
    }
}
