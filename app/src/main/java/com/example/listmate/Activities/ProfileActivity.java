package com.example.listmate.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.listmate.DbUser;
import com.example.listmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView profile_IMG_avatar;
    private MaterialButton profile_BTN_editImage;
    private MaterialTextView txt_user_name;
    private ImageView back_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViews();

        back_arrow.setOnClickListener(view ->goToMainIntent());
        profile_BTN_editImage.setOnClickListener(v ->editImage());
        updateUI();

    }

    private void goToMainIntent(){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    private void findViews(){
        profile_IMG_avatar = findViewById(R.id.profile_IMG_avatar);
        profile_BTN_editImage = findViewById(R.id.profile_BTN_editImage);
        txt_user_name = findViewById(R.id.txt_user_name);
        back_arrow = findViewById(R.id.back_arrow);
    }

    private void updateUI() {

        DbUser.getCurrentUserId((activeUserId)-> {
            DbUser.getUserImage(activeUserId, photoUrl -> {
                if (photoUrl != null) {
                    Glide.with(this).load(photoUrl).placeholder(R.drawable.img_white)
                            .centerCrop().into(profile_IMG_avatar);
                }
            });
            DbUser.getUserName(activeUserId,userName->{
                txt_user_name.setText(userName);
            });
         });
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    uploadImage(uri);
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    private void editImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void uploadImage(Uri uri) {
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("USERS").child(userUid + ".jpg");

        UploadTask uploadTask = imageRef.putFile(uri);


        uploadTask.addOnFailureListener(exception -> Toast.makeText(ProfileActivity.this, "Failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnCompleteListener(task -> {
            String imageUrl = task.getResult().toString();
            DbUser.updateUserImage(imageUrl, res -> updateUI());
        }));
    }
}
