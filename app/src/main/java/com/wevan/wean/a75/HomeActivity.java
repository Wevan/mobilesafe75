package com.wevan.wean.a75;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * Created by Wean on 2016/11/16.
 */

public class HomeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        //加载布局
        setContentView(R.layout.activity_home);
    }
}
