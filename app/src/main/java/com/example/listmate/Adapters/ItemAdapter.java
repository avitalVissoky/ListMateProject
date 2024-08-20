package com.example.listmate.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmate.DbShoppingLists;
import com.example.listmate.DbUser;
import com.example.listmate.Models.Item;
import com.example.listmate.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

        private ArrayList<Item> items;


        public ItemAdapter(ArrayList<Item> items) {
            this.items=items;

        }

        @NonNull
        @Override
        public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view,parent,false);

            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
            Item item = getItem(position);

            holder.txt_item.setText(item.getTitle());
            holder.checkbox_item.setChecked(item.isChecked());
            holder.checkbox_item.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setChecked(isChecked);
                DbShoppingLists.updateListItem(item, success -> {
                });
            });

            holder.txt_item.setOnClickListener(view -> {
                Context context = view.getContext();
                String message;
                if (item.getNote().equals("")) {
                    message = "No notes here :)";
                } else {
                    message = item.getNote();
                }

                new MaterialAlertDialogBuilder(context)
                        .setTitle(item.getTitle())
                        .setMessage(message)  // Set the message directly
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Respond to positive button press
                            dialog.dismiss(); // Dismiss the dialog
                        })
                        .show();
            });

            holder.img_ic_notification.setOnClickListener(view -> {
                String message = item.getTitle() + " : this list item need to be handeled ASAP!";
                DbUser.sendReminderToPartners(item.getListId(), message, success->{
                    if(success){
                        Toast.makeText(view.getContext(), " reminder sent!",Toast.LENGTH_SHORT).show();
                    }
                });
            });

            holder.img_ic_delete_item.setOnClickListener(view -> {
                DbShoppingLists.removeListItem(item.getListId(), item.getId(), success -> {
                    if (success) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, items.size());
                    }
                });
            });

        }

        @Override
        public int getItemCount() {
            return items==null? 0:items.size();
        }

        public Item getItem(int position){
            return items.get(position);
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{

            private  final MaterialTextView txt_item;
            private final CheckBox checkbox_item;
            private final AppCompatImageView img_ic_notification;
            private AppCompatImageView img_ic_delete_item;


            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_item = itemView.findViewById(R.id.txt_item);
                checkbox_item = itemView.findViewById(R.id.checkbox_item);
                img_ic_notification = itemView.findViewById(R.id.img_ic_notification);
                img_ic_delete_item = itemView.findViewById(R.id.img_ic_delete_item);
            }
        }
    }

