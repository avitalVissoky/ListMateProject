package com.example.listmate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmate.Adapters.ItemAdapter;
import com.example.listmate.DbShoppingLists;
import com.example.listmate.DbUser;
import com.example.listmate.Models.Item;
import com.example.listmate.Models.PoppingPanel;
import com.example.listmate.Models.User;
import com.example.listmate.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    private ImageView back_arrow;
    private ImageView img_ic_add_item;
    private ImageView img_ic_group;
    private ImageView img_ic_share;
    private View view_disable;
    private ImageView img_ic_exit;
    private MaterialTextView txt_list_name;
    private TextInputEditText main_input;
    private TextInputEditText optional_input;
    private Button btn_delete_list;
    private RecyclerView rec_view_items;
    private ArrayList <Item> items = new ArrayList<Item>();
    private String currentListId;
    private ItemAdapter itemAdapter;
    private PoppingPanel poppingPanel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        currentListId = getIntent().getExtras().getString("KEY_LIST_ID");

        findViews();
        initViews();
        fetchListItems();

    }

    private void findViews(){
        txt_list_name= findViewById(R.id.txt_list_name);
        btn_delete_list = findViewById(R.id.btn_delete_list);
        back_arrow = findViewById(R.id.back_arrow);
        img_ic_add_item = findViewById(R.id.img_ic_add_item);
        img_ic_group = findViewById(R.id.img_ic_group);
        img_ic_share = findViewById(R.id.img_ic_share);
        img_ic_exit = findViewById(R.id.img_ic_exit);
        view_disable = findViewById(R.id.view_disable);
        rec_view_items = findViewById(R.id.rec_view_items);
        main_input = findViewById(R.id.main_input);
        optional_input = findViewById(R.id.optional_input);

    }

    private void initViews(){

        btn_delete_list.setOnClickListener(view -> removeList());
        back_arrow.setOnClickListener(view ->backArrowClicked());
        img_ic_add_item.setOnClickListener(view -> openPoppingPanel(PoppingPanel.PanelType.ADD_ITEM));
        img_ic_group.setOnClickListener(view -> openPoppingPanel(PoppingPanel.PanelType.WATCH_PARTNERS));
        img_ic_share.setOnClickListener(view -> openPoppingPanel(PoppingPanel.PanelType.ADD_PARTNERS));
        img_ic_exit.setOnClickListener(view -> closePoppingPanel());
        view_disable.setOnClickListener(view -> closePoppingPanel());

        DbShoppingLists.getListTitle(currentListId, res -> {
            txt_list_name.setText(res);
        });

        setupRecyclerView();

        View listLayoutView = getWindow().getDecorView().findViewById(android.R.id.content);
        poppingPanel = new PoppingPanel(ListActivity.this,currentListId,listLayoutView);

    }

    private void setupRecyclerView() {
        itemAdapter = new ItemAdapter(items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rec_view_items.setLayoutManager(linearLayoutManager);
        rec_view_items.setAdapter(itemAdapter);
    }

    private void removeList(){
        DbUser.removeShoppingListFromUser(currentListId, isListRemoved->{
            if(isListRemoved){
                returnToMainIntent();
            }
            else {
                //list is still exist.
            }
        });
    }

    private void returnToMainIntent(){
        Intent i = new  Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void backArrowClicked(){
        returnToMainIntent();
    }
    private void openPoppingPanel(PoppingPanel.PanelType panelType){
        poppingPanel.show(panelType,(partnersList)->{
            if(panelType == PoppingPanel.PanelType.ADD_ITEM) {
                String itemName = main_input.getText().toString().trim();
                if(itemName.length()>12){
                    main_input.setError("name should be between 1-12 characters!");
                } else {
                    String itemNote = optional_input.getText().toString().trim();
                    if (!itemName.isEmpty()) {
                        main_input.setError(null);
                        Item newItem = new Item(currentListId,itemName, itemNote, false);
                        DbShoppingLists.addItemToList(currentListId, newItem, itemId -> {
                            if (itemId!=null) {
                                closePoppingPanel();
                                fetchListItems();
                            } else {
                            }
                        });
                    } else {
                        main_input.setError("this field is required");
                    }
                }
            }

            if (panelType == PoppingPanel.PanelType.ADD_PARTNERS){
                if (!partnersList.isEmpty()) {
                    ArrayList<String> phoneLst = new ArrayList<>();
                        // Retrieve current partners' phones from the database
                    DbUser.getListPartners(currentListId, currentPartners -> {
                        if (currentPartners != null) {
                            for (User partner : partnersList) {
                                String phone = partner.getPhone();
                                phoneLst.add(phone);
                                if (!currentPartners.contains(partner)) {
                                    currentPartners.add(partner);
                                }
                            }
                        } else {
                            currentPartners = new ArrayList<>(partnersList);
                        }
                        // Update the database with the new list of phones
                        DbUser.addPartnersToList(currentListId, partnersList, success -> {
                            if (success) {
                                Toast.makeText(ListActivity.this, "Partners added successfully", Toast.LENGTH_SHORT).show();
                                closePoppingPanel();
                            } else {
                                Toast.makeText(ListActivity.this, "Failed to add partners", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    Toast.makeText(ListActivity.this, "No partners to add", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closePoppingPanel(){
        poppingPanel.close();
    }


    private void fetchListItems(){
        DbShoppingLists.getListItems(currentListId, res -> {
            items.clear();
            items.addAll(res);
            itemAdapter.notifyDataSetChanged();
        });
    }

}