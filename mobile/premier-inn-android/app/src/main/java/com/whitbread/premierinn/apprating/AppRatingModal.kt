package com.whitbread.premierinn.apprating

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.whitbread.premierinn.R

class AppRatingModal : BottomSheetDialogFragment() {

    interface ActionListener {
        fun onCancel()
        fun onRateUsClicked()
        fun onFeedbackClicked()
    }

    private lateinit var sceneInit: Scene
    private lateinit var sceneHappy: Scene
    private lateinit var sceneUnHappy: Scene
    private var actionListener: ActionListener? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_rate_app, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sceneRoot: ViewGroup = view.findViewById(R.id.rate_modal_container)
        val positiveImageBtn = view.findViewById<ImageView>(R.id.positive_btn)
        val positiveCaption = view.findViewById<TextView>(R.id.positive_caption)
        val negativeImageBtn = view.findViewById<ImageView>(R.id.negative_btn)
        val negativeCaption = view.findViewById<TextView>(R.id.negative_caption)

        positiveImageBtn.animate().scaleX(1f).scaleY(1f).setStartDelay(500L).start()
        positiveCaption.animate().scaleX(1f).scaleY(1f).setStartDelay(500L).start()
        negativeImageBtn.animate().scaleX(1f).scaleY(1f).setStartDelay(500L).start()
        negativeCaption.animate().scaleX(1f).scaleY(1f).setStartDelay(500L).start()

        setUpTransitionScences(sceneRoot)

        positiveImageBtn.setOnClickListener {
            animateToScene(scene = sceneHappy, happy = true)
        }

        negativeImageBtn.setOnClickListener {
            animateToScene(scene = sceneUnHappy, happy = false)
        }

        view.findViewById<TextView>(R.id.cancel_btn).setOnClickListener {
            actionListener?.onCancel()
            dismiss()
        }
    }


    private fun setUpTransitionScences(sceneRoot: ViewGroup){
        sceneInit = Scene.getSceneForLayout(sceneRoot, R.layout.view_rate_app, activity as Context)
        sceneHappy = Scene.getSceneForLayout(sceneRoot, R.layout.view_rate_app_happy, activity as Context)
        sceneUnHappy = Scene.getSceneForLayout(sceneRoot, R.layout.view_rate_app_key_unhappy, activity as Context)

        sceneHappy.setEnterAction {
            sceneRoot.findViewById<TextView>(R.id.ok_btn).setOnClickListener {
                actionListener?.onRateUsClicked()
                dismiss()
            }
            sceneRoot.findViewById<TextView>(R.id.cancel_btn).setOnClickListener {
                actionListener?.onCancel()
                dismiss()
            }
        }

        sceneUnHappy.setEnterAction {
            sceneRoot.findViewById<TextView>(R.id.ok_btn).setOnClickListener {
                actionListener?.onFeedbackClicked()
                dismiss()
            }
            sceneRoot.findViewById<TextView>(R.id.cancel_btn).setOnClickListener {
                actionListener?.onCancel()
                dismiss()
            }
        }
    }

    fun show(manager: FragmentManager) {
        manager.commit(allowStateLoss = true) {
            add(this@AppRatingModal, AppRatingModal::class.java.canonicalName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionListener = null
    }

    fun setListener(listener: ActionListener) {
        actionListener = listener
    }

    private fun animateToScene(scene: Scene, happy: Boolean) {

        fun createTransition(happy: Boolean): Transition {
            val fadeOut = Fade()
            fadeOut.mode = Visibility.MODE_OUT
            fadeOut.addTarget(R.id.title)
            fadeOut.addTarget(if (happy) R.id.negative_btn else R.id.positive_btn)

            val fadeIn = Fade()
            fadeIn.mode = Visibility.MODE_IN
            fadeIn.addTarget(R.id.title_step2)
            fadeIn.addTarget(R.id.description)
            fadeIn.addTarget(R.id.ok_btn)

            val changeBounds = ChangeBounds()
            changeBounds.addTarget(if (happy) R.id.positive_btn else R.id.negative_btn)
            changeBounds.addTarget(R.id.cancel_btn)

            val set = TransitionSet()
            set.addTransition(fadeOut)
            set.addTransition(changeBounds)
            set.addTransition(fadeIn)
            set.ordering = TransitionSet.ORDERING_SEQUENTIAL
            return set
        }
        TransitionManager.go(scene, createTransition(happy))
    }
}