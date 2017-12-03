package com.fintrainer.fintrainer.utils

import android.content.Context
import android.view.animation.Interpolator
import android.widget.Scroller

/**
 * Created by krotk on 24.10.2017.
 */
class CustomViewPagerScroller(context: Context?, interpolator: Interpolator?) : Scroller(context, interpolator) {

    private val mDuration = 200

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration)
    }
}