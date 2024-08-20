package com.example.listmate.Models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listmate.Adapters.ListPartnerAdapter;
import com.example.listmate.DbUser;
import com.example.listmate.Interfaces.PoppingPanelPrimaryBtnCallBack;
import com.example.listmate.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class PoppingPanel {

    public enum PanelType {ADD_ITEM,WATCH_PARTNERS,ADD_PARTNERS};
    private RelativeLayout popping_panel_layout;
    private MaterialButton btn_add_item;
    private TextInputEditText main_input;
    private TextInputEditText optional_input;
    private MaterialTextView txt_popping_panel_title;
    private ArrayList<User> partnersList = new ArrayList<>();
    private LinearLayoutCompat view_phones_list;
    private LinearLayoutCompat input_view_add_partner;
    private RecyclerView rec_view_list_partners;
    private TextInputEditText input_partners_phones;
    private ImageView img_ic_add_partner;
    private Context context;
    private String currentListId;
    private ListPartnerAdapter partnerAdapter;
    private TextInputLayout filledTextField_optional_input;
    private TextInputLayout filledTextField_mainInput;


    public PoppingPanel(Context context,String currentListId,View container) {
        this.context = context;
        this.currentListId = currentListId;
        this.popping_panel_layout = container.findViewById(R.id.popping_panel_layout);
        this.btn_add_item = container.findViewById(R.id.btn_add_item);
        this.main_input = container.findViewById(R.id.main_input);
        this.optional_input = container.findViewById(R.id.optional_input);
        this.txt_popping_panel_title = container.findViewById(R.id.txt_popping_panel_title);
        this.view_phones_list = container.findViewById(R.id.view_email_list);
        this.input_view_add_partner = container.findViewById(R.id.input_view_add_partner);
        this.rec_view_list_partners = container.findViewById(R.id.rec_view_list_partners);
        this.input_partners_phones = container.findViewById(R.id.input_partners_email);
        this.img_ic_add_partner =container.findViewById(R.id.img_ic_add_partner) ;
        this.filledTextField_optional_input=container.findViewById(R.id.filledTextField_optional_input);
        this.filledTextField_mainInput =container.findViewById(R.id.filledTextField_mainInput);

    }

    private void setPanelDetails(String title, int emailVisibility, int mainInputVisibility, String mainInputText, String mainInputHint, int partnerInputVisibility, int phoneListVisibility, int noteInputVisibility, String noteInputText, String noteInputHint, int buttonVisibility, String buttonText) {
        txt_popping_panel_title.setText(title);
        rec_view_list_partners.setVisibility(emailVisibility);
        setTextInputVisibility(main_input, filledTextField_mainInput, mainInputVisibility, mainInputText);
        input_view_add_partner.setVisibility(partnerInputVisibility);
        view_phones_list.setVisibility(phoneListVisibility);
        setTextInputVisibility(optional_input, filledTextField_optional_input, noteInputVisibility, noteInputText);
        btn_add_item.setVisibility(buttonVisibility);
        btn_add_item.setText(buttonText);
        initAdapter();
    }

    private void setTextInputVisibility(TextInputEditText input, TextInputLayout layout, int visibility, String text) {
        layout.setVisibility(visibility);
        input.setText(text);
    }

    private void initAdapter(){
        partnerAdapter = new ListPartnerAdapter(partnersList);
        LinearLayoutManager llManager = new LinearLayoutManager(context);
        llManager.setOrientation(RecyclerView.VERTICAL);
        rec_view_list_partners.setLayoutManager(llManager);
        rec_view_list_partners.setAdapter(partnerAdapter);
    }

    public void close(){
        popping_panel_layout.setVisibility(View.INVISIBLE);
    }

    private void activatePoppingPanelEffects(){
        popping_panel_layout.setVisibility(View.VISIBLE);
        popping_panel_layout.setTranslationY(popping_panel_layout.getHeight());
        popping_panel_layout.animate().translationY(0).setDuration(150).start();
    }

    public void show(PanelType type, PoppingPanelPrimaryBtnCallBack callBack){
        activatePoppingPanelEffects();

        switch(type){
            case ADD_ITEM:
                setPanelDetails("Add item", View.INVISIBLE, View.VISIBLE, "", "ENTER ITEM", View.INVISIBLE, View.INVISIBLE, View.VISIBLE, "", "ENTER NOTE", View.VISIBLE, "ADD ITEM");
                btn_add_item.setOnClickListener(v-> {
                    callBack.CBPoppingPanelBtnClicked(partnersList);
                });
                break;

            case WATCH_PARTNERS:
                setPanelDetails("Lists partners", View.VISIBLE, View.INVISIBLE, "", "", View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, "", "", View.INVISIBLE, "");
                rec_view_list_partners.removeAllViews();
                fetchListPartners();
                break;

            case ADD_PARTNERS:
                setPanelDetails("Add partners", View.INVISIBLE, View.INVISIBLE, "", "ENTER PARTNER'S PHONE", View.VISIBLE, View.VISIBLE, View.INVISIBLE, "", "", View.VISIBLE, "ADD PARTNERS");
                input_partners_phones.setText("");
                view_phones_list.removeAllViews();
                img_ic_add_partner.setOnClickListener(view -> {
                    String phone = input_partners_phones.getText().toString().trim();
                    if (!phone.isEmpty()) {
                        input_partners_phones.setError(null);
                        DbUser.isUserExist(phone,(userExist)->{
                            if(userExist) {
                                DbUser.getUserByPhone(phone, user->{
                                    input_partners_phones.setError(null);
                                    createViewPhonesList(user);
                                });
                            }
                            else {
                                input_partners_phones.setError("user doesn't exist in the system!");
                            }
                        });
                    } else {
                        input_partners_phones.setError("this field is required");
                    }
                });
                btn_add_item.setOnClickListener(v-> {
                    if(partnersList.size()>0) {
                        input_partners_phones.setError(null);
                        callBack.CBPoppingPanelBtnClicked(partnersList);
                    }
                    else{
                        input_partners_phones.setError("this field is required");
                    }
                });
                break;
        }
    }

    private void createViewPhonesList(User user){
        partnersList.add(user);
        input_partners_phones.setText("");
        TextView txt_partner_details = new TextView(context);
        txt_partner_details.setTextSize(20);
        txt_partner_details.setText(user.getPhone() +" "+ user.getName());
        view_phones_list.addView(txt_partner_details);
    }

    private void fetchListPartners(){
        DbUser.getListPartners(currentListId, res -> {
            partnersList.clear();
            partnersList.addAll(res);
            partnerAdapter.notifyDataSetChanged();
        });
    }

}
