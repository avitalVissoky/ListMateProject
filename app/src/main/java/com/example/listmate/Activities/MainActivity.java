package com.example.listmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.listmate.Adapters.ListIntroAdapter;
import com.example.listmate.DbUser;
import com.example.listmate.Models.ListIntro;
import com.example.listmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView img_ic_add_list;
    private RecyclerView rec_view_all_lists;
    private RecyclerView rec_view_completed_lists;
    private ArrayList<ListIntro> listIntros = new ArrayList<>();
    private ArrayList<ListIntro> completedListIntros = new ArrayList<>();
    private ListIntroAdapter listIntroAdapter;
    private ListIntroAdapter completedListIntroAdapter;
    private MaterialButton btn_logout;
    private AppCompatTextView txt_user_name;
    private ShapeableImageView img_user_photo;
    private AppCompatImageView img_ic_reminders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViews();
        initAdapters();
        initViews();
        fetchUserLists();
    }

    private void createNewListIsClicked(){
        Intent i = new Intent(getApplicationContext(), ListCreationActivity.class);
        startActivity(i);
    }

    private void icRemindersIsClicked(){
        DbUser.getActiveUser(user->{
            ArrayList<String> reminders = new ArrayList<>();
            if (user.getReminders().size()==0) {
                reminders.add( "you have no reminders!\nkeep up with the good work!");
            } else {
                reminders = user.getReminders();
            }
            String[] remindersArray = reminders.toArray(new String[0]);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("REMINDERS")
                    .setItems(remindersArray,(dialog, which)->{})
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }

    private void initViews() {
          DbUser.getCurrentUserId((activeUserId)->{
              DbUser.getUserName(activeUserId,(userName) ->{
                  txt_user_name.setText("Hello,"+ userName);
              });

              DbUser.getUserImage(activeUserId, photoUrl -> {
                  if (photoUrl != null) {
                      Glide.with(this).load(photoUrl).into(img_user_photo);
                  } else {
                      // Handle the case where the photoUrl is null
                  }
              });
              
          });

        img_user_photo.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);
        });

        btn_logout.setOnClickListener(view -> logout());
        img_ic_add_list.setOnClickListener(view -> createNewListIsClicked());
        img_ic_reminders.setOnClickListener(view ->icRemindersIsClicked());
    }

    private void initAdapters() {
        listIntroAdapter = new ListIntroAdapter(listIntros, listIntro -> openListActivity(listIntro.getListId()));
        completedListIntroAdapter = new ListIntroAdapter(completedListIntros, listIntro -> openListActivity(listIntro.getListId()));

        initListIntroAdapter(listIntroAdapter, rec_view_all_lists);
        initListIntroAdapter(completedListIntroAdapter, rec_view_completed_lists);
    }
    private void openListActivity(String listId) {
        Intent intent = new Intent(MainActivity.this, ListActivity.class);
        intent.putExtra("KEY_LIST_ID", listId);
        startActivity(intent);
    }

    private void initListIntroAdapter(ListIntroAdapter adapter, RecyclerView recView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recView.setLayoutManager(linearLayoutManager);
        recView.setAdapter(adapter);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    void findViews() {
        img_ic_add_list = findViewById(R.id.img_ic_add_list);
        rec_view_all_lists = findViewById(R.id.rec_view_all_lists);
        rec_view_completed_lists = findViewById(R.id.rec_view_completed_lists);
        btn_logout = findViewById(R.id.btn_logout);
        txt_user_name = findViewById(R.id.txt_user_name);
        img_user_photo = findViewById(R.id.img_user_photo);
        img_ic_reminders = findViewById(R.id.img_ic_reminders);
    }

    private void fetchUserLists() {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DbUser.getShoppingListsIdsFromUser(userUid, (listIds )-> {
            listIntros.clear();
            completedListIntros.clear();
            for (String listId : listIds) {
                DbUser.getListDetails(listId, listIntro -> {
                    if(listIntro.getPrecentCompleted() == 100){
                        completedListIntros.add(listIntro);
                        completedListIntroAdapter.notifyDataSetChanged();
                    }
                    else {
                        listIntros.add(listIntro);
                        listIntroAdapter.notifyDataSetChanged();
                    }
                });
            }

        });
    }
}
