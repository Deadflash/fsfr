package com.fintrainer.fintrainer.views.discussions.fragments

import android.os.Bundle
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.utils.Constants.COMMENTS_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.BaseFragment

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentComments : BaseFragment() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getFragmentLayout(): Int = R.layout.fragment_comments

    override fun getFragmentTag(): String = COMMENTS_FRAGMENT_TAG
}