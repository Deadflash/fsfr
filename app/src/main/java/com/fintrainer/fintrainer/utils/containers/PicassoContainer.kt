package com.fintrainer.fintrainer.utils.containers

import android.content.Context
import android.widget.ImageView
import com.fintrainer.fintrainer.R
import com.squareup.picasso.Picasso

/**
 * Created by krotk on 24.10.2017.
 */
class PicassoContainer(val context: Context) {

    fun loadImage(imageView: ImageView, resId: Int) {
        Picasso.with(context)
                .load(resId)
                .fit()
                .centerInside()
                .error(R.color.blue_grey_300)
                .into(imageView)
    }
}