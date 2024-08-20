package com.example.listmate;

import androidx.annotation.NonNull;

import com.example.listmate.Models.ListIntro;
import com.example.listmate.Models.ShoppingList;
import com.example.listmate.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DbUser {

    public interface CallBack<T> {
        void res(T res);
    }

    public static void getCurrentUserId (CallBack<String> callBack){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        callBack.res(userId);
    }

    public static void updateUserImage(String imageUrl, CallBack callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userUid).child("image").setValue(imageUrl)
                .addOnSuccessListener(unused -> {
                    callBack.res(null);
                });
    }

    public static void getUserImage(String userId, CallBack<String> callBack) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!= null){
                    callBack.res(user.getImage());
                }
                else {
                    callBack.res(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.res(null);
            }
        });
    }

    public static void getUserName(String userId, CallBack<String> callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if (user != null) {
                    callBack.res(user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static void getShoppingListsIdsFromUser(String userId, CallBack<ArrayList<String>> callBack) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("shoppingLists");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> listIds = new ArrayList<>();
                for (DataSnapshot listSnapshot : snapshot.getChildren()) {
                    listIds.add(listSnapshot.getKey());
                }
                callBack.res(listIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getListDetails(String listId, CallBack<ListIntro> callBack) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId);
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShoppingList shoppingList = snapshot.getValue(ShoppingList.class);
                if (shoppingList != null) {
                    com.example.listmate.DbShoppingLists.getNumberOfCheckedItems(listId, result -> {
                        int checkedItems = result[0];
                        int totalItems = result[1];
                        int percentCompleted = totalItems > 0 ? (int) ((checkedItems / (double) totalItems) * 100) : 0;

                        ListIntro listIntro = new ListIntro(
                            shoppingList.getId(),
                            shoppingList.getListName(),
                            percentCompleted,
                            shoppingList.getLastUpdated());
                    callBack.res(listIntro);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public static void addShoppingListToUser(String listId, CallBack<Boolean> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(userUid).child("shoppingLists").child(listId).setValue(0, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                callBack.res(true);
            } else {
                callBack.res(false);
            }
        });
    }

    public static void createShoppingList(String listTitle, ArrayList<User> partners, CallBack<String> callBack) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference listsRef = database.getReference("shoppingLists");
        String listId = listsRef.push().getKey();
        if (listId == null) {
            callBack.res(null);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        ShoppingList shoppingList = new ShoppingList(listId, listTitle, currentDate, new HashMap<>());
        listsRef.child(listId).setValue(shoppingList).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addShoppingListToUser(listId, listAdded -> {
                    if (listAdded) {
                        if (!partners.isEmpty())
                        {
                            addPartnersToList(listId,partners,isPartnarAddedToList->{
                                for (User partner : partners) {
                                    addNewShoppingListToPartner(partner.getId(), listId);
                                }
                                callBack.res(listId);
                            });
                        }
                        else {
                            callBack.res(listId);
                        }
                    } else {
                        callBack.res(null);
                    }
                });
            } else {
                callBack.res(null);
            }
        });
    }


    public static void addNewShoppingListToPartner(String userId, String listId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(userId).child("shoppingLists").child(listId).setValue(0);

    }

    public static void getListPartners(String listId, CallBack<ArrayList<User>> callBack) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference().child("shoppingLists").child(listId).child("partnersPhones");
        listRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> partners = new ArrayList<>();
                int totalPhones = (int) snapshot.getChildrenCount();
                final int[] processedPhones = {0};

                if (totalPhones == 0) {
                    callBack.res(partners);
                    return;
                }

                for (DataSnapshot phoneSnapshot : snapshot.getChildren()) {
                    String phone = phoneSnapshot.getValue(String.class);
                    if (phone != null) {
                        getUserByPhone(phone, user -> {
                            if (user != null) {
                                partners.add(user);
                            }
                            processedPhones[0]++;
                            if (processedPhones[0] == totalPhones) {
                                callBack.res(partners);
                            }
                        });
                    } else {
                        processedPhones[0]++;
                        if (processedPhones[0] == totalPhones) {
                            callBack.res(partners);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getUserByPhone(String phone, CallBack<User> callBack){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        callBack.res(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getActiveUser( CallBack<User> callBack ){
        getCurrentUserId(userId-> getUserById(userId, user-> callBack.res(user)));
    }

    public static void getUserById(String uId, CallBack<User> callBack){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("id").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        callBack.res(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void addPartnersToList(String listId, ArrayList<User> partners, CallBack<Boolean> callBack) {
        DatabaseReference listRef = FirebaseDatabase.getInstance().getReference("shoppingLists").child(listId).child("partnersPhones");

        Map<String, String> partnersMap = new HashMap<>();
        for (User partner : partners) {
            partnersMap.put(partner.getId(), partner.getPhone());
        }

        listRef.setValue(partnersMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (User partner : partners) {
                    addNewShoppingListToPartner(partner.getId(), listId);
                }
                callBack.res(true);
            } else {
                callBack.res(false);
            }
        });
    }

    public static void sendReminderToPartners(String listId, String message, CallBack<Boolean> callBack) {
        getListPartners(listId, partners -> {
            if (partners != null && !partners.isEmpty()) {
                int totalPartners = partners.size();
                final int[] processedCount = {0};

                for (User partner : partners) {
                    addRemindertoUser(partner, message);
                    processedCount[0]++;
                    if (processedCount[0] == totalPartners) {
                        callBack.res(true);
                    }
                }
            } else {
                callBack.res(false);
            }
        });
    }

    public static void addRemindertoUser(User user, String reminder) {
        String userUid = user.getId();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference remindersRef = database.getReference("users").child(userUid).child("reminders");

        remindersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> reminders = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String existingReminder = dataSnapshot.getValue(String.class);
                        if (existingReminder != null) {
                            reminders.add(existingReminder);
                        }
                    }
                }
                reminders.add(reminder);

                remindersRef.setValue(reminders).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }


    public static void isUserExist(String phone, CallBack<Boolean> callBack) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callBack.res(true);
                } else {
                    callBack.res(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callBack.res(false);
            }
        });
    }

    public static void removeShoppingListFromUser(String listId, CallBack<Boolean> callBack) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userShoppingListsRef = database.getReference("users").child(userUid).child("shoppingLists").child(listId);
        DatabaseReference listPartnersRef = database.getReference("shoppingLists").child(listId).child("partnersPhones");
        DatabaseReference shoppingListRef = database.getReference("shoppingLists").child(listId);

        userShoppingListsRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listPartnersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> partnersPhones = new ArrayList<>();
                        for (DataSnapshot phoneSnapshot : snapshot.getChildren()) {
                            String phone = phoneSnapshot.getValue(String.class);
                            if (phone != null) {
                                partnersPhones.add(phone);
                            }
                        }
                        partnersPhones.remove(getCurrentUserPhone());
                            if (partnersPhones.isEmpty()) {
                                shoppingListRef.removeValue().addOnCompleteListener(removeTask -> {
                                    if (removeTask.isSuccessful()) {
                                        callBack.res(true);
                                    } else {
                                        callBack.res(false);
                                    }
                                });
                            } else {
                                listPartnersRef.setValue(partnersPhones).addOnCompleteListener(partnersTask -> {
                                    if (partnersTask.isSuccessful()) {
                                        callBack.res(true);
                                    } else {
                                        callBack.res(false);
                                    }
                                });
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callBack.res(false);
                    }
                });
            } else {
                callBack.res(false);
            }
        });
    }

    private static String getCurrentUserPhone() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(); // Placeholder
    }
}
