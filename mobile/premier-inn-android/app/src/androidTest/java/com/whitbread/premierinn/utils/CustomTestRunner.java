package com.whitbread.premierinn.utils;

import android.os.Bundle;

import com.squareup.rx2.idler.Rx2Idler;

import io.appflate.restmock.RESTMockServerStarter;
import io.appflate.restmock.android.AndroidAssetsFileParser;
import io.appflate.restmock.android.AndroidLogger;
import io.appflate.restmock.android.RESTMockTestRunner;
import io.reactivex.plugins.RxJavaPlugins;

public class CustomTestRunner extends RESTMockTestRunner {

    @Override
    public void onStart() {
        RxJavaPlugins.setInitIoSchedulerHandler(Rx2Idler.create("RxJava 2.x Io Scheduler"));
        super.onStart();
    }

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        RESTMockServerStarter.startSync(new AndroidAssetsFileParser(getContext()), new AndroidLogger());
    }

/*    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestPIApplication.class.getName(), context);
    }*/
}
