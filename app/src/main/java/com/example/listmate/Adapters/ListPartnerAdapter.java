package com.example.listmate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.listmate.Models.ListIntro;
import com.example.listmate.Models.User;
import com.example.listmate.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ListPartnerAdapter extends  RecyclerView.Adapter<ListPartnerAdapter.ListPartnerViewHolder>{

        private ArrayList<User> listPartners;
        private Context context;

    public ListPartnerAdapter(ArrayList<User> partners) {
            this.listPartners = partners;
        }

        @NonNull
        @Override
        public ListPartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_partner_view,parent,false);

            return new ListPartnerAdapter.ListPartnerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListPartnerViewHolder holder, int position) {
            User listParnter = getList(position);

            Glide.with(context).load(listParnter.getImage()).placeholder(R.drawable.img_anonymous)
                    .centerCrop().into(holder.img_user_photo);
            holder.partners_name.setText(listParnter.getName());

            holder.partners_phone.setText(listParnter.getPhone());

        }

        @Override
        public int getItemCount() {
            return listPartners ==null? 0: listPartners.size();
        }

        public User getList(int position){
            return listPartners.get(position);
        }

        public class ListPartnerViewHolder extends RecyclerView.ViewHolder{

            private final ShapeableImageView img_user_photo;
            private  final MaterialTextView partners_name;
            private final MaterialTextView partners_phone;

            public ListPartnerViewHolder(@NonNull View itemView) {
                super(itemView);
                img_user_photo= itemView.findViewById(R.id.img_user_photo);
                partners_name = itemView.findViewById(R.id.partners_name);
                partners_phone = itemView.findViewById(R.id.partners_phone);
            }
        }

    }

