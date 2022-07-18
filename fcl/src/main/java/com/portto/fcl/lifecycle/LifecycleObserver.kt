package com.portto.fcl.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.util.*

internal class LifecycleObserver : Application.ActivityLifecycleCallbacks {

    private val activityStack = Stack<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
    }

    companion object {
        private var isRegistered = false

        private val instance by lazy { LifecycleObserver() }

        private var application: Application? = null

        fun register(application: Application) {
            Companion.application = application
            if (!isRegistered) {
                application.registerActivityLifecycleCallbacks(instance)
            }
            isRegistered = true
        }

        private fun topActivity() = if (instance.activityStack.isEmpty()) null else instance.activityStack.lastElement()

        fun context(): Context? = topActivity() ?: application
    }
}