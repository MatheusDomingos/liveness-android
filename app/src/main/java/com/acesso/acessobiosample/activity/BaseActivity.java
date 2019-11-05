package com.acesso.acessobiosample.activity;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.fragment.CustomFragment;

/**
 * Created by matheusdomingos on 06/05/17.
 */
public abstract class BaseActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle b = getIntent().getExtras();

        Object customFragment =  b.getSerializable(CustomFragment.FRAGMENT);
        String title = b.getString(CustomFragment.TITLE_PAGE);

        if(customFragment instanceof CustomFragment){
            Fragment customFragment2 = (Fragment) b.getSerializable(CustomFragment.FRAGMENT);
            setNewFragment(customFragment2, title, this, false);

        } else {
            Class customFragment2 = (Class) b.getSerializable(CustomFragment.FRAGMENT);
            setNewFragment(customFragment2, title, this, false);

        }

    }

    /**
     * @param fragmentClass {@link Class}
     * @param title {@link CharSequence}
     * @param activity {@link androidx.fragment.app.FragmentActivity}
     */
    public static void setNewFragment(Class fragmentClass, CharSequence title, FragmentActivity activity, boolean back) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set action bar title
        activity.setTitle(title);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment);
        if( back ) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public static void setNewFragment(Fragment fragment, CharSequence title, FragmentActivity activity, boolean back) {

        // Set action bar title
        activity.setTitle(title);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContent, fragment);
        if( back ) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }



}
