package com.madanes.myapp.utils

import android.util.Log
import java.lang.StringBuilder

object Logger
{

    private val TAG = Logger::class.java.simpleName

    fun logDebug()
    {
        Log.d(TAG, generateLogText(""))
    }

    fun logDebug(tag: String, logText: String)
    {
        Log.d(tag, generateLogText(logText))
    }

    fun logError(tag: String = TAG, logText: String = "")
    {
        Log.e(tag, generateLogText(logText))
    }

    fun logError(tag: String, logText: String, tr: Throwable)
    {
        Log.w(tag, generateLogText(logText), tr)
    }

    fun logInfo(tag: String, logText: String)
    {
        Log.i(tag, generateLogText(logText))
    }

    private fun generateLogText(logText: String): String
    {
        var response = logText

        val stackTrace = Thread.currentThread().stackTrace

        if (stackTrace != null && stackTrace.size > 4)
        {
            val element = stackTrace[4]
            val fullClassName = element.className
            val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1) //no package

            //add class and method data to logText
            val builder = StringBuilder("T:")
            builder.append(Thread.currentThread().id)
                    .append(" | ")
                    .append(className)
                    .append(".")
                    .append(element.methodName)
                    .append("()")
                    .append(" | ")
                    .append(logText)

            response = builder.toString()
        }

        return response
    }
}