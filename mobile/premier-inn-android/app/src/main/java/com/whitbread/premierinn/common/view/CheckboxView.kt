package com.whitbread.premierinn.common.view

import android.content.Context
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.widget.checkedChanges
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.utils.HtmlUtils
import com.whitbread.premierinn.common.utils.bind
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

class CheckboxView: LinearLayout {

    private lateinit var checkBox: CheckBox
    private lateinit var label: TextView
    private lateinit var error: View
    private val checkboxStateObservable = ReplaySubject.create<CheckboxState>(1)

    constructor(ctx: Context): super(ctx) {
        init(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs) {
        init (ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int): super(ctx, attrs, defStyleAttr) {
        init(ctx)
    }

    private fun init(ctx: Context) {
        LayoutInflater.from(ctx).inflate(R.layout.view_terms_conditions_checkbox, this, true)
        orientation = VERTICAL
        bindViews()

        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        setLabelText()

        checkBox.checkedChanges()
                .map { checked -> if (checked) CheckboxState.CHECKED else CheckboxState.NOT_CHECKED }
                .doOnNext { checkboxStateObservable.onNext(it) }
                .subscribe()
    }

    fun checkboxState(): Observable<CheckboxState> {
        return checkboxStateObservable
    }

    fun setText(text: Spanned?) {
        label.text = text
    }

    fun showError(show: Boolean) {
        error.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    private fun setLabelText() {
        label.text = HtmlUtils.parseTags(context.getString(R.string.review_booking_terms_conditions_label_html,
                                         context.getString(R.string.terms_conditions_web_url)))
        label.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun bindViews() {
        checkBox = bind<CheckBox>(R.id.terms_conditions_checkbox).value
        label = bind<TextView>(R.id.terms_conditions_text).value
        error = bind<View>(R.id.terms_conditions_error).value
    }
}

enum class CheckboxState { CHECKED, NOT_CHECKED }