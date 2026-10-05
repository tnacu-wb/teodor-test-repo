package com.whitbread.premierinn.criteria.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.rxbinding3.view.clicks
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.IntentUtils
import com.whitbread.premierinn.databinding.ViewCallUsCardBinding
import io.reactivex.disposables.CompositeDisposable

class CallUsViewCard : ConstraintLayout {

    private lateinit var binding: ViewCallUsCardBinding

    private val compositeDisposable = CompositeDisposable()
    private var telephoneNumber: String? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        binding = ViewCallUsCardBinding.inflate(LayoutInflater.from(getContext()), this, true)
        if (attrs != null) {
            val attributesArray = context.obtainStyledAttributes(attrs, R.styleable.CallUsViewCard)
            val descriptiveText =
                attributesArray.getString(R.styleable.CallUsViewCard_descriptive_text)
            if (!descriptiveText.isNullOrEmpty()) {
                binding.viewCallUsDescription.text = descriptiveText
            }
            attributesArray.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        compositeDisposable.addAll(
                binding.viewCallUsButton.clicks().subscribe {
                    telephoneNumber?.let { IntentUtils.openTelephone(context, it) }
                },
                binding.callUsContainer.clicks().subscribe {
                    telephoneNumber?.let { IntentUtils.openTelephone(context, it) }
                }
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        compositeDisposable.clear()
    }

    fun setTelephoneNumber(telephoneNumber: String) {
        this.telephoneNumber = telephoneNumber
    }

    fun setDescription(text: String) {
        binding.viewCallUsDescription.text = text
    }

    fun setLabel(text: String) {
        binding.viewCallUsLabel.text = text
    }
}
