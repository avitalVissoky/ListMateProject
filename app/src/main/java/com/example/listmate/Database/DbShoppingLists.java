package com.example.listmate;

import androidx.annotation.NonNull;

import com.example.listmate.Models.Item;
import com.example.listmate.Models.ShoppingList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DbShoppingLists {


    public interface CallBack<T> {
        void res(T res);
    }

    public static void getListById(String listId, CallBack<ShoppingList> callBack){
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId);
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ShoppingList list = snapshot.getValue(ShoppingList.class);
                    callBack.res(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public static void getItemById(String itemId, CallBack<Item> callBack) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Item item = snapshot.getValue(Item.class);
                callBack.res(item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getListItems(String listId,CallBack<ArrayList<Item>> callBack) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId).child("items");
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Item> items = new ArrayList<>();
                int totalItems = (int) snapshot.getChildrenCount();
                if (totalItems == 0) {
                    callBack.res(items);
                    return;
                }
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String itemId = itemSnapshot.getKey();
                    getItemById(itemId,item->{
                        if(item!=null){
                            items.add(item);
                        }
                        if (items.size() == totalItems) {
                            callBack.res(items);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getListTitle(String listId, CallBack<String> callBack) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId).child("listName");
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ListTitle = snapshot.getValue(String.class);
                callBack.res(ListTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

public static void createItemInDb(Item item, CallBack<String> callBack){
    DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");
    String itemId = itemsRef.push().getKey();
    item.setId(itemId);
    if (itemId != null) {
        itemsRef.child(itemId).setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                callBack.res(item.getId());
            }
            else{
                callBack.res(null);
            }
        });
    }

}

    public static void addItemToList(String listId, Item item, CallBack<String> callBack) {
        DatabaseReference listItemsRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId).child("items");
        createItemInDb(item,itemId->{
            if (itemId!= null){
                listItemsRef.child(itemId).setValue(0).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        updateListLastUpdated(listId,res -> {
                            if(res){
                                callBack.res(itemId);
                            }
                        });
                    }
                    else{
                        callBack.res(null);
                    }
                });
            }
        });
    }

    public static void updateListItem(Item item, CallBack<Boolean> callBack) {
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(item.getId());
        itemRef.setValue(item).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateListLastUpdated(item.getListId(), (succsess)->{
                    if(succsess){
                        callBack.res(true);
                    }
                    else
                        callBack.res(false);
                });
            } else {
                callBack.res(false);
            }
        });
    }

    public static void updateListLastUpdated(String listId, CallBack<Boolean> callBack) {
        DatabaseReference lastUpdatedRef = FirebaseDatabase.getInstance().getReference()
                .child("shoppingLists")
                .child(listId)
                .child("lastUpdated");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        lastUpdatedRef.setValue(currentDate, (error, ref) -> {
            if (error == null) {
                callBack.res(true);
            } else {
                callBack.res(false);
            }
        });
    }

    public static void getNumberOfCheckedItems(String listId, CallBack<int[]> callBack) {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("items");

        itemsRef.orderByChild("listId").equalTo(listId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalItems = 0;
                int checkedItems = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        totalItems++;
                        if (item.isChecked()) {
                            checkedItems++;
                        }
                    }
                }
                callBack.res(new int[]{checkedItems, totalItems});
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void removeItemFromDb(String itemId, CallBack<Boolean> callBack){
        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference().child("items").child(itemId);
        itemRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callBack.res(true);
            } else {
                callBack.res(false);
            }
        });
    }

    public static void removeListItem(String listId, String itemId, CallBack<Boolean> callBack) {
        DatabaseReference listItemRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId).child("items").child(itemId);
        removeItemFromDb(itemId,isItemRemoved->{
            if(isItemRemoved){
                listItemRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateListLastUpdated(listId,success->{
                            if(success){
                                callBack.res(true);
                            }else {
                                callBack.res(false);
                            }
                        });
                    } else {
                        callBack.res(false);
                    }
                });
            }
        });
    }
}
