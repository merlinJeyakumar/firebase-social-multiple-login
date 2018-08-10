package com.androidbash.androidbashfirebaseupdated.Application;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.sync.BaseModule;

/**
 * Created by Merlin on 5/29/2018.
 */

public class AppController extends Application {
    public static AppController mAppInstance;
    private static Realm realm;

    public AppController() {
        super();
    }

    private static void initRealm() {
        Realm.init(mAppInstance);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(mAppInstance.getPackageName())
                .schemaVersion(42)
                .modules(new BaseModule())
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(config);
    }

    public static Realm getRealm() {
        return realm;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mAppInstance = this;
        MultiDex.install(this);

        initRealm();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
