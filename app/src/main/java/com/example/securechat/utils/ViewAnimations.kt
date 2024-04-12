package com.example.securechat.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.securechat.R


object ViewAnimations {
    fun rotateFab(view: View, rotate: Boolean): Boolean {
        view.animate()
            .setDuration(200)
            .rotation(if (rotate) 135f else 0f)
        return rotate
    }

    fun showIn(v: View) {
        v.visibility = View.VISIBLE
        v.setAlpha(0f)
        v.translationY = v.height.toFloat()
        v.animate()
            .setDuration(200)
            .translationY(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                }
            })
            .alpha(1f)
            .start()
    }

    fun showOut(v: View) {
        v.visibility = View.VISIBLE
        v.setAlpha(1f)
        v.translationY = 0f
        v.animate()
            .setDuration(200)
            .translationY(v.height.toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    v.visibility = View.GONE
                    super.onAnimationEnd(animation)
                }
            }).alpha(0f)
            .start()
    }

    fun init(v: View) {
        v.visibility = View.GONE
        v.translationY = v.height.toFloat()
        v.setAlpha(0f)
    }

    fun roundAnimation(view: ImageView, resourceId: Int) {
        view.animate()
            .rotationBy(360f)
            .withEndAction {
                view.animate().rotationBy(360f).setDuration(1000).setInterpolator(null).start()
            }
            .setDuration(300)
            .setInterpolator(null)
            .start()
        view.postDelayed({
            view.setImageResource(resourceId)
        }, 150)
    }

}