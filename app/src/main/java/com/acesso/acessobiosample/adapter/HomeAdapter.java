package com.acesso.acessobiosample.adapter;

import android.app.Activity;

import android.graphics.PorterDuff;
import android.view.View;


import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.acesso.acessobiosample.R;
import com.acesso.acessobiosample.dto.Attachments;
import com.acesso.acessobiosample.dto.GetProcessByUserResult;
import com.acesso.acessobiosample.dto.Process;
import com.acesso.acessobiosample.dto.Subject;
import com.acesso.acessobiosample.utils.enumetators.SharedKey;
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


            for(int i = 0; i < attachments.size(); i ++) {
                Attachments attObj = attachments.get(i);
                if(attObj.getName().equals("Foto do Cliente")) {


                    if(Hawk.contains(SharedKey.INSTANCE)) {
                     if(Hawk.get(SharedKey.INSTANCE).equals("https://crediariohomolog.acesso.io/treinamento/services/v2/credService.svc/"))  {
                         Glide.with(activity).load("https://crediariohomolog.acesso.io/treinamento/" + attObj.getUri()).fitCenter().into(holder.ivUser);
                     } else{
                         Glide.with(activity).load("https://www2.acesso.io/seres/" + attObj.getUri()).fitCenter().into(holder.ivUser);
                     }

                    }

                }

            }

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

        Integer liveness  = process.getLiveness();

        if(liveness == 0) {
            holder.tvLiveness.setText("Desligado");
            holder.tvLiveness.setTextColor(ContextCompat.getColor(activity, R.color.darkGrey));
            holder.icLiveness.setColorFilter(holder.icLiveness.getContext().getResources().getColor(R.color.darkGrey), PorterDuff.Mode.SRC_ATOP);

        }else if (liveness == 1){
            holder.tvLiveness.setText("Aprovado");
            holder.tvLiveness.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen));
            holder.icLiveness.setColorFilter(holder.icLiveness.getContext().getResources().getColor(R.color.colorGreen), PorterDuff.Mode.SRC_ATOP);
        }else if (liveness == 2){
            holder.tvLiveness.setText("Reprovado");
            holder.tvLiveness.setTextColor(ContextCompat.getColor(activity, R.color.red_btn_bg_pressed_color));
            holder.icLiveness.setColorFilter(holder.icLiveness.getContext().getResources().getColor(R.color.red_btn_bg_pressed_color), PorterDuff.Mode.SRC_ATOP);

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
        private TextView tvLiveness;
        private ImageView icLiveness;

        private CircularImageView ivUser;


        public ViewHolder(View itemView) {
            super(itemView);


            tvName = ((TextView) itemView.findViewById(R.id.tvName));
            tvCPF = ((TextView) itemView.findViewById(R.id.tvCPF));
            tvStatus = ((TextView) itemView.findViewById(R.id.tvStatus));
            tvScore = ((TextView) itemView.findViewById(R.id.tvScore));
            tvLiveness = ((TextView) itemView.findViewById(R.id.tvLiveness));
            icLiveness = ((ImageView) itemView.findViewById(R.id.ic_camera));

            ivUser = ((CircularImageView) itemView.findViewById(R.id.ivUser));

            //  tvCityUF = ((TextView) itemView.findViewById(R.id.tv_city_UF));
        }
    }

}