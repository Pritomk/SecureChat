package com.example.securechat.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View


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

}