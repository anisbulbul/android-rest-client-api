package com.example.restclientlib.utilities;

import android.util.Log;

/**
 * Created by Anis on 3/15/16.
 * Author : Adaptive Enterprise Limited
 */

public class LoggerUtils {

    private static final String TAG = "RestClientAPI";
    private static boolean isLogShow = true;

    public static String logTrace = "";

    public static void e(String subTag, String msg) {
        if (isLogShow) {
            Log.e(TAG, subTag + " : " + msg);
        }
        logTrace += "(e) " + TAG + " : " + subTag + " : " + msg + "\n";
    }

    public static void e(String subTag, String msg, Throwable tr) {
        if (isLogShow) {
            Log.e(TAG, subTag + " : " + msg);
        }
        logTrace += "(e) " + TAG + " : " + subTag + " : " + msg + "Exception : " + tr.getMessage() + "\n";
    }

    public static void v(String subTag, String msg) {
        if (isLogShow) {
            Log.v(TAG, subTag + " : " + msg);
        }
        logTrace += "(v) " + TAG + " : " + subTag + " : " + msg + "\n";
    }

    public static void d(String subTag, String msg) {
        if (isLogShow) {
            Log.d(TAG, subTag + " : " + msg);
        }
//        logTrace += "(d) " + SUB_TAG + " : " + subTag + " : " + msg + "\n";
    }

    public static void i(String subTag, String msg) {
        if (isLogShow) {
            Log.i(TAG, subTag + " : " + msg);
        }
        logTrace += "(i) " + TAG + " : " + subTag + " : " + msg + "\n";
    }

    public static void w(String subTag, String msg) {
        if (isLogShow) {
            Log.w(TAG, subTag + " : " + msg);
        }
        logTrace += "(w) " + TAG + " : " + subTag + " : " + msg + "\n";
    }

    public static void w(String subTag, Throwable tr) {
        if (isLogShow) {
            Log.w(TAG, subTag + " : " + tr.getMessage());
        }
        logTrace += "(w) " + TAG + " : " + subTag + " : " + tr.getMessage() + "\n";
    }

    public static void bigD(String subTag, String msg) {
        if (isLogShow) {
            if (msg.length() > 4000) {
                int chunkCount = msg.length() / 4000;
                for (int i = 0; i <= chunkCount; i++) {
                    int max = 4000 * (i + 1);
                    if (max >= msg.length()) {
                        Log.d(TAG, subTag + " : " + "partition " + i + " of " + chunkCount + ":" + msg.substring(4000 * i));
                    } else {
                        Log.d(TAG, subTag + " : " + "partition " + i + " of " + chunkCount + ":" + msg.substring(4000 * i, max));
                    }
                }
            } else {
                Log.d(TAG, subTag + " : " + msg);
            }
        }
        logTrace += "(d) " + TAG + " : " + subTag + " : " + msg + "\n";
    }
    public static void setIsLogShow(boolean isLogShow) {
        LoggerUtils.isLogShow = isLogShow;
    }
}
