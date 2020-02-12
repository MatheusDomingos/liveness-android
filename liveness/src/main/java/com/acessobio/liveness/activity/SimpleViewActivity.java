package com.acessobio.liveness.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.acessobio.liveness.R;


/**
 * Created by matheusdomingos on 18/05/17.
 */
public class SimpleViewActivity extends BaseActivity {

    static public final String NAME_STATUS = "NAME_STATUS";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view);

    }

    @Override
    protected void onStop() {
        super.onStop();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}