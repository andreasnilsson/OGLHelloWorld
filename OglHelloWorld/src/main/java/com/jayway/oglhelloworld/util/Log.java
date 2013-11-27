package com.jayway.oglhelloworld.util;

/**
 * Wraps {@link android.util.Log}.
 *
 * @author Andreas Nilsson
 */
public class Log {

    private final String tag;

    /**
     * Uses {@link Class#getSimpleName()} as tag.
     *
     * @param clazz The class.
     */
    public Log(Class clazz) {
        tag = clazz.getSimpleName();
    }

    /**
     * @see {@link android.util.Log#d(String, String)}
     */
    public void d(String msg) {
        android.util.Log.d(tag, msg);
    }

    /**
     * @see {@link android.util.Log#e(String, String)}
     */
    public void e(String msg) {
        android.util.Log.e(tag, msg);
    }

    /**
     * @see {@link android.util.Log#w(String, String)}
     */
    public void w(final String msg) {
        android.util.Log.w(tag, msg);
    }

    /**
     * @see {@link android.util.Log#e(String, String, Throwable)}
     */
    public void e(final String msg, final Throwable throwable) {
        android.util.Log.e(tag, msg, throwable);
    }

    /**
     * @see {@link android.util.Log#i(String, String)}
     */
    public void i(final String msg) {
        android.util.Log.i(tag, msg);
    }
}
