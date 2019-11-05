package com.acesso.acessobiosample.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lombok.NonNull;

/**
 * Created by thomaz on 04/10/16.
 */
public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> tList;
    private int line;
    protected Activity activity;

    public BaseAdapter(@NonNull List<T> tList, @LayoutRes int line, Activity activity) {
        this.tList = tList;
        this.line = line;
        this.activity = activity;
    }

    public BaseAdapter(@NonNull List<T> tList, @LayoutRes int line) {
        this.tList = tList;
        this.line = line;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(line, parent, false);

        return getViewHolder(v);
    }

    @Override
    public abstract void onBindViewHolder(VH holder, int position);

    @Override
    public int getItemCount() {
        return tList.size();
    }

    /**
     * @param v View
     * @return VH
     */
    protected abstract VH getViewHolder(View v);

    /**
     * @param items {@link List}
     */
    public void add(List<T> items) {
        // int previousDataSize = tList.size();
        tList.addAll(items);
        // notifyItemRangeInserted(previousDataSize, items.size());
        notifyDataSetChanged();
    }

}
