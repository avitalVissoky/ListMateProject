package com.example.listmate.Activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.listmate.Models.User;
import com.example.listmate.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private AppCompatImageView img_login_background;
    private MaterialButton btn_login;
    private MaterialTextView login_LBL_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        img_login_background = findViewById(R.id.img_login_background);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> login());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            img_login_background.setRenderEffect(RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP));
        }
        validateLogin();
    }

    private void login() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                )).setTheme(R.style.FirebaseAuthUI)
                .build();

        signInLauncher.launch(signInIntent);
    }

    private void validateLogin(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ACTIVE_USER_ID",user.getUid());
            i.putExtras(bundle);
            startActivity(i);
        } else {

        }
    }

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            (result) -> {
                checkForNewUser();
                validateLogin();
            });

    private void checkForNewUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user == null) {
                    updateUserData();
                }
            }

            @Override

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUserData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setName("Avital");
        user.setPhone(firebaseUser.getPhoneNumber());
        usersRef.child(firebaseUser.getUid()).setValue(user);
    }
}