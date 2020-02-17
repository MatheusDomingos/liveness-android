package com.acesso.acessobiosample.activity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.support.LivenessXHomolog;

import java.util.HashMap;

/**
 * Created by matheusdomingos on 18/05/17.
 */
public class SimpleViewActivity extends BaseActivity  {

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

}