package com.whitbread.premierinn.loading

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.activity.BaseActivity
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.databinding.ActivityForceUpdateBinding
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FORCE_UPDATE_DESCRIPTION
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FORCE_UPDATE_TITLE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ForceUpdateActivity : BaseActivity<ActivityForceUpdateBinding>() {

    @Inject lateinit var resource: ContentManagedResourceRepository

    override fun inflateBinding(inflater: LayoutInflater): ActivityForceUpdateBinding {
        return ActivityForceUpdateBinding.inflate(inflater)
    }

    override fun getToolbar(): Toolbar? {
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.forceUpdateTitle.text = resource.getString(FORCE_UPDATE_TITLE.value)
        binding.forceUpdateDescription.text = resource.getString(FORCE_UPDATE_DESCRIPTION.value)
        binding.forceUpdateBtn.setOnClickListener {
            try {
                startActivity(IntentUtils.createPlaystoreIntent(this))
            } catch (e: ActivityNotFoundException) {
                startActivity(IntentUtils.createWebLinkIntent(String.format(Urls.PLAYSTORE, this.packageName)))
            }
        }
    }

    override fun attachInternetInfoView(): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent {
            return Intent(context, ForceUpdateActivity::class.java)
        }
    }
}