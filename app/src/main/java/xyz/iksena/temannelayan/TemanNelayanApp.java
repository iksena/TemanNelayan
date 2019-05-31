package xyz.iksena.temannelayan;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TemanNelayanApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("teman_nelayan.realm")
                .schemaVersion(0)
                .migration((realm, oldVersion, newVersion) -> {
                    //TODO add migration
                })
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
