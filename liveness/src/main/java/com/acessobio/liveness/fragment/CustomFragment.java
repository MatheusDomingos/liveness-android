package com.acessobio.liveness.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.acessobio.liveness.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.Serializable;

import retrofit2.Response;

/**
 * Created by matheusdomingos on 06/05/17.
 */

public class CustomFragment extends Fragment implements Serializable {

    static public final String FRAGMENT = "FRAGMENT";
    static public final String TITLE_PAGE = "TITLE_PAGE";
    static public final String OBJECT = "OBJECT";
    static public final String OBJECT_EXTRA = "OBJECT_EXTRA";

    public static final String TAG = "CustomFragment";


    protected Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle = getActivity().getIntent().getExtras();

    }

    protected void setDecorationLine(RecyclerView rv) {
        // Drawable dividerDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider_line);
        // rv.addItemDecoration(new DividerItemDecoration(dividerDrawable));
    }

    public void showSnackbarError(String text) {
        if (text == null) {
            Log.e(TAG, text);
            return;
        }

        Snackbar snackbar = Snackbar.make(getView(), text, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.RED);
        TextView textView = snackbarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public boolean showSnackbarError(Response response) {
        try {
            JSONObject j = new JSONObject(response.errorBody().string());
            JSONObject error = j.getJSONObject("Error");
            String message = error.getString("Description");
            showSnackbarError(message);
            return true;
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
            return false;
        }
    }



}
