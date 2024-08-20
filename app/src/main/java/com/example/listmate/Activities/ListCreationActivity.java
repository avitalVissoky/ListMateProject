package com.example.listmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.listmate.DbUser;
import com.example.listmate.Models.User;
import com.example.listmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ListCreationActivity extends AppCompatActivity {

    private ImageView back_arrow;
    private ImageView img_ic_add_partner;
    private MaterialButton btn_add_list;
    private TextInputEditText input_list_title;
    private TextInputEditText input_partners_phones;
    private LinearLayoutCompat view_phones_list;
    private ArrayList<User> partnersList = new ArrayList<User>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_creation);

        findViews();
        initViews();
    }

    private void findViews(){
        back_arrow = findViewById(R.id.back_arrow);
        img_ic_add_partner = findViewById(R.id.img_ic_add_partner);
        btn_add_list = findViewById(R.id.btn_add_list);
        input_list_title = findViewById(R.id.input_list_title);
        input_partners_phones = findViewById(R.id.input_partners_email);
        view_phones_list = findViewById(R.id.view_email_list);
    }

    private void initViews(){
        back_arrow.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        });

        img_ic_add_partner.setOnClickListener(view -> {
            String phone = input_partners_phones.getText().toString().trim();
            if (!phone.isEmpty()) {
                DbUser.isUserExist(phone, (userExist)->{
                    if(userExist){
                        DbUser.getUserByPhone(phone, user-> {
                            partnersList.add(user);
                            input_partners_phones.setText("");
                            TextView txt_partner_details = new TextView(ListCreationActivity.this);
                            txt_partner_details.setTextSize(20);
                            txt_partner_details.setText(phone + " " + user.getName());
                            view_phones_list.addView(txt_partner_details);
                        });
                    }else {
                        input_partners_phones.setError("user doesn't exist in the system!");
                    }
                });
            } else {
                input_partners_phones.setError("this field is required");
            }
        });

        btn_add_list.setOnClickListener(view -> {
            String listTitle = input_list_title.getText().toString().trim();
            if (listTitle.isEmpty()) {
                input_list_title.setError("This field is required");
        } else {
            input_list_title.setError(null);
                DbUser.getActiveUser(user->{
                    if(user!=null) {
                        partnersList.add(user);

                        createNewList(listTitle, partnersList, listId -> {
                            if (listId != null) {
                                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("KEY_LIST_ID", listId);
                                i.putExtras(bundle);
                                startActivity(i);
                            } else {
                                Toast.makeText(ListCreationActivity.this, "Failed to create list", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
        }
        });

    }

    private void createNewList(String listTitle, ArrayList<User> partners, DbUser.CallBack<String> callBack) {
        DbUser.createShoppingList(listTitle, partners, callBack);
    }
}