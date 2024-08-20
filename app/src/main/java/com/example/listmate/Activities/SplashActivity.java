package com.example.listmate.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.listmate.R;
import com.google.android.material.imageview.ShapeableImageView;

public class SplashActivity extends AppCompatActivity {

    ShapeableImageView splash_IMG_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        splash_IMG_icon = findViewById(R.id.splash_IMG_icon);
        animate(splash_IMG_icon);
    }

    private void animate(ShapeableImageView view) {

        view.setAlpha(0.0f);
        view.setScaleX(0.5f);
        view.setScaleY(0.5f);

        view.animate()
                .alpha(1.0f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(1000)
                .setInterpolator(new LinearInterpolator())
                .withEndAction(() -> {

                    view.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(500)
                            .setInterpolator(new BounceInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        startApp();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) { }

                    @Override
                    public void onAnimationRepeat(Animator animator) { }
                });
                });
    }
    private void startApp() {
            startActivity(new Intent(this, LoginActivity.class));

    }
}