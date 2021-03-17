package com.hospicehounsou.animatedswipebutton

import android.animation.*
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.hospicehounsou.swipebuttontest.R


class AnimatedSwipeButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(
        context,
        attrs
    ) {
    private lateinit var slidingButton: ImageView
    private var active = false
    private var initialButtonWidth = 0
    private lateinit var centerText: TextView

    private lateinit var disabledDrawable: Drawable
    private lateinit var enabledDrawable: Drawable

    private lateinit var onStateChangeListener: OnStateChangeListener

    init {
        val background = background(context)
        buttonTextview(context, background)
        movingIcon(context)
    }

    private fun movingIcon(context: Context) {
        //The moving icon
        val swipeButton = ImageView(context)
        slidingButton = swipeButton
        disabledDrawable =
            ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_open_black_24dp)!!
        enabledDrawable =
            ContextCompat.getDrawable(getContext(), R.drawable.ic_lock_outline_black_24dp)!!

        slidingButton.setImageDrawable(disabledDrawable)
        slidingButton.setPadding(40, 40, 40, 40)
        val layoutParamsButton = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsButton.addRule(ALIGN_PARENT_LEFT, TRUE)
        layoutParamsButton.addRule(CENTER_VERTICAL, TRUE)
        swipeButton.background = ContextCompat.getDrawable(context, R.drawable.shape_button)
        swipeButton.setImageDrawable(disabledDrawable)
        addView(swipeButton, layoutParamsButton)
        setOnTouchListener(getButtonTouchListener())

    }

    private fun buttonTextview(context: Context, background: RelativeLayout) {
        // The text in the center
        val centerText = TextView(context)
        this.centerText = centerText
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(CENTER_IN_PARENT, TRUE)
        centerText.text = "SWIPE" //add any text you need
        centerText.setTextColor(Color.WHITE)
        centerText.setPadding(35, 35, 35, 35)
        background.addView(centerText, layoutParams)
    }

    private fun background(context: Context): RelativeLayout {
        // The rounded background of the button
        val background = RelativeLayout(context)
        val layoutParamsView = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParamsView.addRule(CENTER_IN_PARENT, TRUE)
        background.background = ContextCompat.getDrawable(context, R.drawable.shape_rounded)
        addView(background, layoutParamsView)
        return background
    }


    private fun getButtonTouchListener() = object : OnTouchListener {
        override fun onTouch(view: View?, motionevent: MotionEvent?): Boolean {
            when (motionevent?.action) {
                MotionEvent.ACTION_DOWN -> {
                    return true
                }
                MotionEvent.ACTION_MOVE -> {

                    motionEventMoveIcon(motionevent)

                    return true
                }
                MotionEvent.ACTION_UP -> {
                    motionEventUpIcon()
                    return true
                }

                else -> return false
            }
        }

        private fun motionEventUpIcon() {
            if (active) {
                collapseButton()
            } else {
                initialButtonWidth = slidingButton.width

                if (slidingButton.x + slidingButton.width > width * 0.85) {
                    expandButton()
                } else {
                    moveButtonBack()
                }
            }
        }

        private fun motionEventMoveIcon(motionevent: MotionEvent) {
            val slidingButtonHalfWidth = slidingButton.width / 2

            if (motionevent.x < slidingButtonHalfWidth) {
                slidingButton.x = 0F
            } else {
                if (motionevent.x + slidingButtonHalfWidth < width) {
                    println("Move the button 1 : ${motionevent.x - slidingButtonHalfWidth}")
                    centerText.alpha =
                        1 - 1.3f * (slidingButton.x + slidingButton.width) / width
                    slidingButton.x = motionevent.x - slidingButtonHalfWidth

                }

                if (motionevent.x + slidingButtonHalfWidth > width) {
                    println("Move the button 2")
                    slidingButton.x = (width - slidingButton.width).toFloat()
                }
            }


        }

    }

    private fun expandButton() {
        val positionAnimator = ValueAnimator.ofFloat(slidingButton.x, 0f)
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            slidingButton.x = x
        }
        val widthAnimator = ValueAnimator.ofInt(
            slidingButton.width,
            width
        )
        widthAnimator.addUpdateListener {
            val params = slidingButton.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            slidingButton.layoutParams = params
        }

        val animatorSet = AnimatorSet()
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                active = true
                onStateChangeListener.opennStateChange(active)
                slidingButton.setImageDrawable(enabledDrawable)
            }
        })
        animatorSet.playTogether(positionAnimator, widthAnimator)
        animatorSet.start()
    }

    private fun collapseButton() {
        val widthAnimator = ValueAnimator.ofInt(
            slidingButton.width,
            initialButtonWidth
        )
        widthAnimator.addUpdateListener {
            val params = slidingButton.layoutParams
            params.width = (widthAnimator.animatedValue as Int)
            slidingButton.layoutParams = params
        }
        widthAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                active = false
                onStateChangeListener.opennStateChange(active)
                slidingButton.setImageDrawable(disabledDrawable)
            }
        })
        val objectAnimator = ObjectAnimator.ofFloat(
            centerText, "alpha", 1f
        )
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimator, widthAnimator)
        animatorSet.start()
    }

    private fun moveButtonBack() {
        val positionAnimator = ValueAnimator.ofFloat(slidingButton.x, 0f)
        positionAnimator.interpolator = AccelerateDecelerateInterpolator()
        positionAnimator.addUpdateListener {
            val x = positionAnimator.animatedValue as Float
            slidingButton.x = x
        }
        val objectAnimator = ObjectAnimator.ofFloat(
            centerText, "alpha", 1f
        )
        positionAnimator.duration = 200
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimator, positionAnimator)
        animatorSet.start()
    }

    fun setOnStateChangeListener(onStateChangeListener: OnStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener
    }
}

inline fun AnimatedSwipeButton.setListener(crossinline isActive: (Boolean) -> Unit) {
    setOnStateChangeListener(object : OnStateChangeListener {
        override fun opennStateChange(newValue: Boolean) {
            isActive(newValue)
        }
    })
}