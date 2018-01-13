package com.fintrainer.fintrainer.views.discussions.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fintrainer.fintrainer.R
import com.fintrainer.fintrainer.di.contracts.DiscussionsContract
import com.fintrainer.fintrainer.utils.Constants.ADD_DISCUSSIONS_FRAGMENT_TAG
import com.fintrainer.fintrainer.utils.Constants.REALM_ERROR_CODE
import com.fintrainer.fintrainer.utils.Constants.REALM_SUCCES_CODE
import com.fintrainer.fintrainer.views.App
import com.fintrainer.fintrainer.views.BaseFragment
import com.fintrainer.fintrainer.views.discussions.DiscussionsPresenter
import kotlinx.android.synthetic.main.fragment_add_discussion.*
import org.jetbrains.anko.support.v4.toast
import javax.inject.Inject

/**
 * Created by krotk on 23.10.2017.
 */
class FragmentAddDiscussion : BaseFragment(), DiscussionsContract.AddDiscussionView {

    @Inject
    lateinit var presenter: DiscussionsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.initDiscussionsComponent()?.inject(this)

        presenter.bindAddDiscussionsView(this)
    }

    override fun onCreateDiscussionClicked() {
        if (!etQuestion?.text.toString().trim().isEmpty()) {
            if (activity!!.intent.getIntExtra("testType", -1) != -1) {
                presenter.createDiscussion(etQuestion?.text.toString(), activity!!.intent.getStringExtra("questionCode"), activity!!.intent.getIntExtra("testType", -1))
            }else{
                toast(R.string.something_went_wrong)
            }
        } else {
            etQuestion.error = activity!!.getString(R.string.ask_question)
        }
    }

    override fun createDiscussionResult(code: Int) {
        when (code){
            REALM_SUCCES_CODE -> {
                activity!!.onBackPressed()
                val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
            REALM_ERROR_CODE -> {
                toast("ERROR CREATE")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unbindAddDiscussionsView()
    }

    override fun getFragmentLayout(): Int = R.layout.fragment_add_discussion

    override fun getFragmentTag(): String = ADD_DISCUSSIONS_FRAGMENT_TAG
}