package com.fintrainer.fintrainer.views.discussions.fragments

import android.os.Bundle
import android.view.View
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.utils.Constants.ADD_DISCUSSIONS_FRAGMENT_TAG
import com.fintrainer.fintrainer.views.BaseFragment

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentAddDiscussion : BaseFragment() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun getFragmentLayout(): Int = R.layout.fragment_add_discussion

    override fun getFragmentTag(): String  = ADD_DISCUSSIONS_FRAGMENT_TAG
}