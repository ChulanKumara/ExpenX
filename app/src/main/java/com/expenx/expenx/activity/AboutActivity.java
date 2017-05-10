package com.expenx.expenx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.expenx.expenx.R;

public class AboutActivity extends AppCompatActivity {

    ViewGroup mAboutMainRelativeLayout;

    ImageView mCircleAroundE;

    //EditText mEmailText, mPasswordText;

    TextView mExpenxText, mEText, mThisAppDevText, mGroupNameText, mEmailComText, mProjectDescText, mAllRightsText;

    //Button mLoginButton, mLoginGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //animation -- start
        final RotateAnimation rotateAnimationCircle = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimationCircle.setRepeatCount(Animation.INFINITE);
        rotateAnimationCircle.setDuration(6000);
        rotateAnimationCircle.setInterpolator(new LinearInterpolator());
        rotateAnimationCircle.start();

        Animation scaleAnimationCircle = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimationCircle.setDuration(500);
        scaleAnimationCircle.setInterpolator(new DecelerateInterpolator());
        scaleAnimationCircle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                View[] Views = new View[]{mExpenxText, mEText};

                for (View v : Views) {
                    v.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCircleAroundE.setAnimation(rotateAnimationCircle);

                View[] scaleInViews = new View[]{mExpenxText, mEText};

                for (View v : scaleInViews) {
                    Animation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scale.setDuration(500);
                    scale.setInterpolator(new DecelerateInterpolator());
                    v.setAnimation(scale);
                    scale.start();
                    v.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mCircleAroundE.setAnimation(scaleAnimationCircle);
        scaleAnimationCircle.start();


        final View[] views = new View[]{mThisAppDevText, mGroupNameText, mEmailComText, mProjectDescText, mAllRightsText};

        long delayBetweenAnimations = 100l;

        for (final View view : views) {
            view.setVisibility(View.INVISIBLE);
        }

        for (int i = views.length - 1; i >= 0; i--) {
            final View view = views[i];

            long delay = i * delayBetweenAnimations;

            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(AboutActivity.this, R.anim.fade_in_animation);
                    Animation translateAnimation1 = new TranslateAnimation(0, 0, 1000, 0);
                    translateAnimation1.setInterpolator(new AccelerateDecelerateInterpolator());
                    translateAnimation1.setDuration(1000);
                    translateAnimation1.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    view.startAnimation(fadeInAnimation);
                    view.setAnimation(translateAnimation1);
                    translateAnimation1.start();
                }
            }, delay);
        }

        //animation -- end
    }
}
