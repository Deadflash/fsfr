package com.fintrainer.fintrainer.di.contracts

import android.content.Intent

/**
 * Created by krotk on 28.10.2017.
 */
interface DiscussionsContract {

    interface View:IView{
        fun realmStatus(code: Int)
    }

    interface DiscussionsView: IView{
        fun showDiscussions()
    }

    interface CommentsView: IView{
        fun showComments()
    }

    interface AddDiscussionView : IView{
        fun addDiscussionStatus(code: Int)
    }

    interface Presenter: IPresenter{
        fun bindDiscussionsView(iView: IView)
        fun unbindDiscussionsView()
        fun bindDiscussionsCommentsView(iView: IView)
        fun unbindDiscussionsCommentsView()
        fun bindAddDiscussionsView(iView: IView)
        fun unbindAddDiscussionsView()
        fun initDiscussionsRealm()
        fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent)
    }
}