package com.whitbread.premierinn.landing

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.view.isVisible
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.debugmenu.DebugMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LandingActivity : BaseLandingActivity() {

    @Inject lateinit var stringProvider: StringResourceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding.versionLabel.root.apply {
                text = stringProvider.versionLabel
                isVisible = true
                setOnClickListener {
                    openDebugMenu()
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun openDebugMenu() {
        Intent(this, DebugMenuActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also {
            startActivity(it)
        }
    }
}