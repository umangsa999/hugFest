package com.usc.itp476.contact.contactproject;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.usc.itp476.contact.contactproject.POJO.GameMarker;

public class ContactApplication extends Application {
    private static ContactApplication singleton;

    public ContactApplication getSingleton(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        ParseObject.registerSubclass(GameMarker.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this,
                "ellChjDHP7hNM4CBQLHrBNWzDMoOzElwUgy3MpEc",
                "aXSv9sdHcVcnjSIaqy8KuymGh16K5I53MiWXGgnN");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
