package com.example.listmate.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmate.Models.ListIntro;
import com.example.listmate.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


import android.widget.ProgressBar;

public class ListIntroAdapter extends RecyclerView.Adapter<ListIntroAdapter.ListIntroViewHolder>{

        private ArrayList<ListIntro> listsIntro;
        private OnItemClickListener onItemClickListener;

        public ListIntroAdapter(ArrayList<ListIntro> lists, OnItemClickListener onItemClickListener) {
            this.listsIntro=lists;
            this.onItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public ListIntroAdapter.ListIntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_intro_view,parent,false);

            return new ListIntroAdapter.ListIntroViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListIntroAdapter.ListIntroViewHolder holder, int position) {
            ListIntro listIntro = getList(position);

            holder.txt_list_name.setText(listIntro.getListName());
            holder.progress_bar.setProgress(listIntro.getPrecentCompleted());
            holder.txt_last_updated.setText("last updated: "+listIntro.getLastUpdated());
            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(listIntro);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listsIntro==null? 0:listsIntro.size();
        }

        public ListIntro getList(int position){
            return listsIntro.get(position);
        }

        public class ListIntroViewHolder extends RecyclerView.ViewHolder{

            private  final MaterialTextView txt_list_name;
            private final ProgressBar progress_bar;
            private final MaterialTextView txt_last_updated;

            public ListIntroViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_list_name = itemView.findViewById(R.id.txt_list_name);
                progress_bar = itemView.findViewById(R.id.progress_bar);
                txt_last_updated = itemView.findViewById(R.id.txt_last_updated);
            }
        }
        public interface OnItemClickListener {
            void onItemClick(ListIntro listIntro);
        }

}

