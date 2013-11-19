package com.jayway.oglhelloworld.util;

/**
 * Created by Andreas Nilsson
 */
public class Log {

    private final String tag;

    public Log(Class clazz){
        tag = clazz.getSimpleName();
    }

    public void d(String msg){
        android.util.Log.d(tag, msg);
    }

    public void e(String msg){
        android.util.Log.e(tag, msg);
    }

    public void w(final String msg) {
        android.util.Log.w(tag, msg);
    }

    public void e(final String msg, final Throwable throwable) {
        android.util.Log.e(tag, msg, throwable);
    }
}
