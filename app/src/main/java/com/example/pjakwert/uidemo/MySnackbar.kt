package com.example.pjakwert.uidemo

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View


fun Snackbar.applyBackgroundColor( colorResId : Int ) : Snackbar {
    val snackbarView = this.view
    val bgId: Int = ContextCompat.getColor(snackbarView.context, colorResId)
    snackbarView.setBackgroundColor(bgId)
    return this
}