package com.whitbread.premierinn.reviewbooking.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.databinding.ViewReviewAndBookCreateAccountBinding
import io.reactivex.Observable

class CreateAccountView(context: Context, attributeSet: AttributeSet?) : ConstraintLayout(context, attributeSet) {

    private var paymentRelay: PublishRelay<Boolean> = PublishRelay.create()

    private lateinit var binding: ViewReviewAndBookCreateAccountBinding
    init {
        inflateLayout()
    }

    private fun inflateLayout() {
        binding = ViewReviewAndBookCreateAccountBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun isAcceptablePassword(): Observable<Boolean> {
        return binding.passwordForm.isAcceptablePassword()
    }

    fun getPassword(): Observable<String> {
        return binding.passwordForm.getPassword()
    }

}