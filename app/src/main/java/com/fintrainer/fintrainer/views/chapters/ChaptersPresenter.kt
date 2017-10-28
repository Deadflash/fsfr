package com.fintrainer.fintrainer.views.chapters

import com.fintrainer.fintrainer.di.contracts.ChaptersContract
import com.fintrainer.fintrainer.di.contracts.IView
import com.fintrainer.fintrainer.structure.ChapterRealm
import com.fintrainer.fintrainer.utils.RealmContainer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by krotk on 25.10.2017.
 */
class ChaptersPresenter(private val realmContainer: RealmContainer) : ChaptersContract.Presenter {

    var view: ChaptersContract.View? = null
    var chapters: List<ChapterRealm> = emptyList()

    override fun bind(iView: IView) {
        view = iView as ChaptersContract.View
    }

    override fun loadChapters(examId: Int) {
        if (chapters.isEmpty()) {
            doAsync {
                chapters = realmContainer.getChaptersAsync(examId)
                uiThread {
                    view?.showChapters(chapters)
                }
            }
        }else{
            view?.showChapters(chapters)
        }
    }

    override fun unBind() {
        view = null
    }
}