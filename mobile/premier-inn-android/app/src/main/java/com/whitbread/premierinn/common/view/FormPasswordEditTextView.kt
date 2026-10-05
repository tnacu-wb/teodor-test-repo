package com.whitbread.premierinn.common.view

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.TextViewCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.PasswordFormValidation
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.databinding.ViewCompoundPasswordEdittextBinding
import com.whitbread.premierinn.domain.common.COMMA_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Predicate
import javax.inject.Inject

@AndroidEntryPoint
class FormPasswordEditTextView@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private lateinit var passwordFormEditTextBinding: ViewCompoundPasswordEdittextBinding
    private var passwordEditText: TextInputEditText
    private var required: Boolean? = null
    private var mask: CharSequence? = null
    private var updating = false
    private var deleting = false

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
        passwordEditText = passwordFormEditTextBinding.passwordEt
        bindCustomAttributes(attrs)
        init()
    }

    private fun inflateLayout() {
        passwordFormEditTextBinding = ViewCompoundPasswordEdittextBinding.inflate(LayoutInflater.from(context), this)
    }

    private fun bindCustomAttributes(attributeSet: AttributeSet?) {
        if (attributeSet == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FormEditTextView)
        val typedArray2 = context.obtainStyledAttributes(attributeSet, R.styleable.PasswordForm)

        val editTextIdRes = typedArray.getResourceId(R.styleable.FormEditTextView_editTextId, 0)
        passwordEditText!!.id = editTextIdRes
        val editTextInputType =
            typedArray.getInt(R.styleable.FormEditTextView_android_inputType, EditorInfo.TYPE_NULL)
        passwordEditText!!.inputType = editTextInputType
        TextViewCompat.setTextAppearance(passwordEditText!!, R.style.Body)
        val editTextImeOption =
            typedArray.getInt(R.styleable.FormEditTextView_android_imeOptions, EditorInfo.IME_NULL)
        passwordEditText!!.imeOptions = editTextImeOption
        required = if (typedArray.hasValue(R.styleable.FormEditTextView_required)) {
            typedArray.getBoolean(R.styleable.FormEditTextView_required, false)
        } else {
            null
        }
        if (required != null && !required!!) {
            hint = hint.toString() + " (optional)"
        }
        setMask(typedArray.getString(R.styleable.FormEditTextView_mask))

        val acceptedColorRes = typedArray2.getResourceId(R.styleable.PasswordForm_passwordAcceptedColor, 0)
        if (acceptedColorRes != 0) {
            acceptedColor = acceptedColorRes
        }

        val errorColorRes = typedArray2.getResourceId(R.styleable.PasswordForm_passwordErrorColor, 0)
        if (errorColorRes != 0) {
            errorColor = errorColorRes
        }
        typedArray.recycle()
        typedArray2.recycle()
    }

    val inputText: String
        get() = getEditText()!!.text.toString()

    fun setText(text: CharSequence?) {
        passwordEditText!!.setText(text)
    }

    fun resetEvent(): Observable<Action> {
        return getEditText()!!.afterTextChangeEvents()
            .skipInitialValue()
            .map {
                if (error == null) {
                    return@map StringUtils.EMPTY_STRING
                } else {
                    return@map error
                }
            }
            .filter(allowIfErrorMessageExists())
            .map {
                ResetInputErrorAction.create(
                    id
                )
            }
    }

    private fun allowIfErrorMessageExists(): Predicate<in CharSequence?>? {
        return Predicate { error: CharSequence? -> !StringUtils.isBlank(error) }
    }

    private fun setMask(mask: CharSequence?) {
        this.mask = mask
        if (hasMask()) {
            setMaxLength(mask!!.length)
            getEditText()!!.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (updating || !hasMask()) {
                        return
                    }
                    updating = true
                    applyMask(s)
                    updating = false
                }
            })

            //detect delete
            getEditText()!!.setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent ->
                deleting = (event.action == KeyEvent.ACTION_DOWN
                        && event.keyCode == KeyEvent.KEYCODE_DEL)
                false
            }
        }
    }

    private fun applyMask(textField: Editable) {
        if (TextUtils.isEmpty(textField) || !hasMask()) {
            return
        }

        //save cursor position
        val cursorPosition = getEditText()!!.selectionStart

        //remove input filters to ignore input type
        val filters = textField.filters
        textField.filters = arrayOfNulls(0)
        val maskedText = textField.toString()
        val unmaskedText = getUnMaskedText(maskedText)
        textField.clear()
        if (deleting) {
            deleteWithMask(textField, cursorPosition, maskedText, unmaskedText)
            deleting = false
        } else {
            applyMaskAfterCharacterEntered(textField, unmaskedText)
        }

        //reset filters
        textField.filters = filters
    }

    private fun deleteWithMask(
        textField: Editable,
        cursorPosition: Int,
        maskedText: String,
        unmaskedText: String
    ) {
        var maskedText = maskedText
        var i = 1
        while (i < maskedText.length && i < unmaskedText.length) {
            if (maskedText[maskedText.length - 1] != unmaskedText[unmaskedText.length - i]) {
                maskedText = maskedText.substring(0, maskedText.length - 1)
            }
            i++
        }
        textField.append(maskedText)
        val currentTextLen = textField.length
        //check if deleting
        if (deleting && cursorPosition < currentTextLen) {
            getEditText()!!.setSelection(cursorPosition)
        }
    }

    private fun applyMaskAfterCharacterEntered(textField: Editable, unmaskedText: String) {
        val unmaskedTextSb = StringBuilder(unmaskedText)
        var i = 0
        while (i < mask!!.length && (unmaskedTextSb.length > 0 || mask!![i] != EXPECTED_CHAR_PLACEHOLDER)) {
            val maskChar = mask!![i]
            if (maskChar == EXPECTED_CHAR_PLACEHOLDER) {
                if (unmaskedTextSb.length > 0) {
                    val unmaskedChar = unmaskedTextSb[0]
                    textField.append(unmaskedChar)
                    unmaskedTextSb.deleteCharAt(0)
                }
            } else {
                textField.append(maskChar)
            }
            i++
        }
    }

    private fun hasMask(): Boolean {
        return !TextUtils.isEmpty(mask)
    }

    private fun setMaxLength(length: Int) {
        getEditText()!!.filters = arrayOf<InputFilter>(LengthFilter(length))
    }

    private fun getUnMaskedText(text: String): String {
        if (TextUtils.isEmpty(text) || !hasMask()) {
            return text
        }
        val maskLen = mask!!.length
        val textLen = text.length
        val sb = StringBuilder()
        var i = 0
        while (i < maskLen && i < textLen) {
            val m = mask!![i]
            val t = text[i]
            if (t != m) {
                sb.append(t)
            }
            i++
        }
        return sb.toString()
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

        passwordFormEditTextBinding.passwordFormMessage.text = String.format("%s %s", prefix, fullValidationMessage)

        compositeDisposable.add(Observable.combineLatest(passwordEditText.textChanges(),
            passwordEditText.focusChanges().skipInitialValue(),
            BiFunction { first: CharSequence?, second: Boolean? -> Pair(first, second) })
            .doOnNext { pair ->
                password = pair.first.toString()
            }.subscribe {
                validation = validation(password)
                passwordRelay.accept(password)

                if (validation!!.error) {
                    passwordAcceptableRelay.accept(false)
                    passwordFormEditTextBinding.passwordFormMessage.visibility = View.VISIBLE
                    passwordFormEditTextBinding.passwordFormMessage.text = validation!!.message
                    passwordFormEditTextBinding.passwordFormMessage.setTextColor(context.resources.getColor(errorColor))
                } else {
                    if (validation!!.message != EMPTY_STRING_DOMAIN) {
                        passwordAcceptableRelay.accept(false)
                        passwordFormEditTextBinding.passwordFormMessage.visibility = View.VISIBLE
                        passwordFormEditTextBinding.passwordFormMessage.text = validation!!.message
                        passwordFormEditTextBinding.passwordFormMessage.setTextColor(context.resources.getColor(R.color.dark_grey_2))
                    } else {
                        passwordAcceptableRelay.accept(true)
                        passwordFormEditTextBinding.passwordFormMessage.visibility = View.VISIBLE
                        passwordFormEditTextBinding.passwordFormMessage.text = context.getString(R.string.password_validation_acceptable_password)
                        passwordFormEditTextBinding.passwordFormMessage.setTextColor(context.resources.getColor(acceptedColor))
                    }
                }
            })
    }

    override fun onDetachedFromWindow() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
        super.onDetachedFromWindow()
    }

    companion object {
        private const val EXPECTED_CHAR_PLACEHOLDER = '#'
    }
}