package com.whitbread.premierinn.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.PasswordFormValidation
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.databinding.ViewPasswordFormBinding
import com.whitbread.premierinn.domain.common.COMMA_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

@AndroidEntryPoint
class PasswordForm @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var passwordFormBinding: ViewPasswordFormBinding
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var acceptedColor: Int = 0
    private var errorColor: Int = 0

    private var password: String = EMPTY_STRING_DOMAIN

    private var passwordAcceptableRelay: PublishRelay<Boolean> = PublishRelay.create()
    private var passwordRelay: PublishRelay<String> = PublishRelay.create()

    @Inject lateinit var resource: ContentManagedResourceRepository
    private var passwordValidator = resource.getString(ContentManagedResourceRepository.Key.PASSWORD_VALIDATOR.value)
    private var rules = passwordValidator.substring(2, passwordValidator.length - 2).split("\",\"")
            .map { it.substringBefore("\":") to it.substringAfter(":\"") }
    private val fullValidationMessage = rules.joinToString { it.second }
    private val prefix = context.getString(R.string.password_validation_prefix_message)
    private var message = EMPTY_STRING_DOMAIN
    private var validation: PasswordFormValidation? = null

    init {
        inflateLayout()
        bindCustomAttributes(attrs)
        init()
    }

    private fun inflateLayout() {
        passwordFormBinding = ViewPasswordFormBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun bindCustomAttributes(attributeSet: AttributeSet?) {
        if (attributeSet == null) {
            return
        }

        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.PasswordForm)

        val acceptedColorRes = typedArray.getResourceId(R.styleable.PasswordForm_passwordAcceptedColor, 0)
        if (acceptedColorRes != 0) {
            acceptedColor = acceptedColorRes
        }

        val errorColorRes = typedArray.getResourceId(R.styleable.PasswordForm_passwordErrorColor, 0)
        if (errorColorRes != 0) {
            errorColor = errorColorRes
        }

        typedArray.recycle()
    }

    private fun validation(password: String): PasswordFormValidation {
        var validationError = EMPTY_STRING_DOMAIN

        if (password.isEmpty()) {
            return PasswordFormValidation("$prefix $fullValidationMessage")
        }

        if (Validator.hasSpecialCharacters(password)) {
            return PasswordFormValidation(context.getString(R.string.password_validation_no_special_characters), true)
        }

        rules.forEach {
            if (!Validator.verifyPassword(password, it.first)) {
                validationError += "${it.second.plus(COMMA_STRING_DOMAIN)} "
            }
        }

        message = if (validationError != EMPTY_STRING_DOMAIN) {
            String.format("%s %s", prefix, validationError)
        } else {
            EMPTY_STRING_DOMAIN
        }

        return PasswordFormValidation(message.substring(0, if (message.length > 1) message.length - 2 else message.length))
    }

    private fun init() {
        passwordAcceptableRelay.accept(false)

        passwordFormBinding.passwordFormMessage.text = String.format("%s %s", prefix, fullValidationMessage)

        compositeDisposable.add(Observable.combineLatest(passwordFormBinding.passwordEditText.textChanges(),
            passwordFormBinding.passwordEditText.focusChanges().skipInitialValue(),
                BiFunction { first: CharSequence?, second: Boolean? -> Pair(first, second) })
                .doOnNext { pair ->
                    password = pair.first.toString()
                }.subscribe {
                    validation = validation(password)
                    passwordRelay.accept(password)

                    if (validation!!.error) {
                        passwordFormBinding.passwordLayout.isErrorEnabled = true
                        passwordFormBinding.passwordLayout.error = validation!!.message
                        passwordFormBinding.passwordFormMessage.visibility = View.GONE
                        passwordAcceptableRelay.accept(false)
                    } else {
                        if (validation!!.message != EMPTY_STRING_DOMAIN) {
                            passwordFormBinding.passwordLayout.isErrorEnabled = false
                            passwordAcceptableRelay.accept(false)
                            passwordFormBinding.passwordLayout.error = EMPTY_STRING_DOMAIN
                            passwordFormBinding.passwordFormMessage.visibility = View.VISIBLE
                            passwordFormBinding.passwordFormMessage.text = validation!!.message
                            passwordFormBinding.passwordFormMessage.setTextColor(context.resources.getColor(R.color.dark_grey_2))
                        } else {
                            passwordFormBinding.passwordLayout.isErrorEnabled = false
                            passwordAcceptableRelay.accept(true)
                            passwordFormBinding.passwordLayout.error = EMPTY_STRING_DOMAIN
                            passwordFormBinding.passwordFormMessage.visibility = View.VISIBLE
                            passwordFormBinding.passwordFormMessage.text = context.getString(R.string.password_validation_acceptable_password)
                            passwordFormBinding.passwordFormMessage.setTextColor(context.resources.getColor(acceptedColor))
                        }
                    }
                })
    }

    fun getPassword(): Observable<String> {
        return passwordRelay
    }

    fun isAcceptablePassword(): Observable<Boolean> {
        return passwordAcceptableRelay
    }

    override fun onDetachedFromWindow() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
        super.onDetachedFromWindow()
    }
}