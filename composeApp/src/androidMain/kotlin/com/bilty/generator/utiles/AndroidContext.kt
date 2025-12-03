package com.bilty.generator.utiles

import android.app.Activity
import android.content.Context

lateinit var androidContext: Context

fun initializeApplicationContext(context: Context) {
    androidContext = context.applicationContext
}

var androidContextActivity: Activity? = null

fun setCurrentActivity(activity: Activity) {
    androidContextActivity = activity
}
