package com.example.acessobiosample.activity;

import android.os.Bundle;


import androidx.annotation.Nullable;

import com.example.acessobiosample.R;

/**
 * Created by matheusdomingos on 18/05/17.
 */
public class FormViewActivity extends BaseActivity {

    static public final String NAME_STATUS = "NAME_STATUS";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view);

    }

}