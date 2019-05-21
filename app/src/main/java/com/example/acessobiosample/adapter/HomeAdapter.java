package com.example.acessobiosample.adapter;

import android.app.Activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.acessobiosample.R;
import com.example.acessobiosample.dto.Attachments;
import com.example.acessobiosample.dto.GetProcessByUserResult;
import com.example.acessobiosample.dto.Process;
import com.example.acessobiosample.dto.Subject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

/**
 * Created by matheusdomingos on 24/07/17.
 */
public class HomeAdapter<TPI> extends BaseAdapter<TPI, HomeAdapter.ViewHolder> {

    private ArrayList<Process> processes;

    public HomeAdapter(@NonNull List<TPI> tpis, @LayoutRes int line, Activity activity) {
        super(tpis, line, activity);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        GetProcessByUserResult tpi = (GetProcessByUserResult) tList.get(position);
        Process process = tpi.getProcess();
        Subject subject = process.getSubject();

        String subjectName =  subject.getName();
        String[] array = subjectName.split(" ");
        String name = "";

        if (array.length > 1) {
            String strName = array[0];
            String strLastname = array[1];

            name  = strName.substring(0, 1).toUpperCase() + strName.substring(1).toLowerCase() + " " + strLastname.substring(0, 1).toUpperCase() + ".";

        } else {
            String str = array[0];
            if (str != null && str.length() > 1) {
                name  = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
            }
        }

        ArrayList<Attachments> attachments = process.getAttachments();

        if(attachments.size() > 0) {

            Attachments attObj = attachments.get(0);

            Glide.with(activity).load("https://www2.acesso.io/seres/" + attObj.getUri()).fitCenter().into(holder.ivUser);

        }


        holder.tvName.setText(name);
        holder.tvCPF.setText(subject.getCode());




        Integer status  = process.getStatus();


        if(status == 2) {
            holder.tvStatus.setText("Em análise");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorOrange));

        }else{
            holder.tvStatus.setText("Autenticado");
            holder.tvStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorGreen));
        }

        if(process.getScore() > 0) {
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvScore.setText(String.format("Score: %s", Float.toString(process.getScore())));
        }else{
            holder.tvScore.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    protected ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvCPF;
        private TextView tvStatus;
        private TextView tvScore;
        private CircularImageView ivUser;


        public ViewHolder(View itemView) {
            super(itemView);


            tvName = ((TextView) itemView.findViewById(R.id.tvName));
            tvCPF = ((TextView) itemView.findViewById(R.id.tvCPF));
            tvStatus = ((TextView) itemView.findViewById(R.id.tvStatus));
            tvScore = ((TextView) itemView.findViewById(R.id.tvScore));
            ivUser = ((CircularImageView) itemView.findViewById(R.id.ivUser));

            //  tvCityUF = ((TextView) itemView.findViewById(R.id.tv_city_UF));
        }
    }

}