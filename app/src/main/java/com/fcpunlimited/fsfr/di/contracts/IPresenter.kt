package com.fcpunlimited.fsfr.di.contracts

/**
 * Created by krotk on 21.10.2017.
 */
interface IPresenter {
    fun bind(iView: IView)
    fun unBind()
}