package com.android.charan.shareride;

import android.app.Application;
import android.content.Context;

/**
 * Created by harikanth on 5/15/17.
 */

public class App extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    public static void setContext(Context Context) {
        mContext = Context;
    }

}
