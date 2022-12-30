package com.project.letsnote.heartAnimation;

/**
 * Created by Leosss on 07/05/2016.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.firebase.client.Firebase;
import com.project.letsnote.DetailActivity;
import com.project.letsnote.PerfilAjeno;
import com.project.letsnote.R;
import com.project.letsnote.login.Login;
import com.project.letsnote.starAnimation.CircleView;
import com.project.letsnote.starAnimation.DotsView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HeartButtonView extends FrameLayout implements View.OnClickListener {
    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateDecelerateInterpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    @Bind(R.id.heartImg)
    ImageView heartxml;
    @Bind(R.id.vDotsViewHeart)
    DotsViewHeart vDotsViewHeart;
    @Bind(R.id.vCircleHeart)
    CircleViewHeart vCircleHeart;

    private boolean isChecked;
    private AnimatorSet animatorSet;

    public HeartButtonView(Context context) {
        super(context);
        init();
    }

    public HeartButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeartButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.heart_like_button, this, true);
        ButterKnife.bind(this);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(DetailActivity.meGusta) {
            heartxml.setImageResource(R.drawable.heart_off);
            final Firebase refNota = new Firebase("https://letsnote.firebaseio.com/notas").child(DetailActivity.notaKey).child("likes").child(Login.FIREBASEKEY);
            refNota.setValue(false);
            int likes = Integer.parseInt(DetailActivity.likesNum.getText().toString());
            likes--;
            DetailActivity.likesNum.setText(String.valueOf(likes));
            DetailActivity.meGusta = false;
        }else if(!DetailActivity.meGusta){
            heartxml.setImageResource(R.drawable.heart_on);
            final Firebase refNota = new Firebase("https://letsnote.firebaseio.com/notas").child(DetailActivity.notaKey).child("likes").child(Login.FIREBASEKEY);
            refNota.setValue(true);
            int likes = Integer.parseInt(DetailActivity.likesNum.getText().toString());
            likes++;
            DetailActivity.likesNum.setText(String.valueOf(likes));
            DetailActivity.meGusta = true;
        }

        //heartxml.setImageResource(PerfilAjeno.esAmigo ? R.drawable.ic_star_rate_on : R.drawable.ic_star_rate_off);

        if (animatorSet != null) {
            animatorSet.cancel();
        }

        if (PerfilAjeno.esAmigo) {
            heartxml.animate().cancel();
            heartxml.setScaleX(0);
            heartxml.setScaleY(0);
            vCircleHeart.setInnerCircleRadiusProgress(0);
            vCircleHeart.setOuterCircleRadiusProgress(0);
            vDotsViewHeart.setCurrentProgress(0);

            animatorSet = new AnimatorSet();

            ObjectAnimator outerCircleAnimator = ObjectAnimator.ofFloat(vCircleHeart, CircleViewHeart.OUTER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            outerCircleAnimator.setDuration(250);
            outerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator innerCircleAnimator = ObjectAnimator.ofFloat(vCircleHeart, CircleViewHeart.INNER_CIRCLE_RADIUS_PROGRESS, 0.1f, 1f);
            innerCircleAnimator.setDuration(200);
            innerCircleAnimator.setStartDelay(200);
            innerCircleAnimator.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator starScaleYAnimator = ObjectAnimator.ofFloat(heartxml, ImageView.SCALE_Y, 0.2f, 1f);
            starScaleYAnimator.setDuration(350);
            starScaleYAnimator.setStartDelay(250);
            starScaleYAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator starScaleXAnimator = ObjectAnimator.ofFloat(heartxml, ImageView.SCALE_X, 0.2f, 1f);
            starScaleXAnimator.setDuration(350);
            starScaleXAnimator.setStartDelay(250);
            starScaleXAnimator.setInterpolator(OVERSHOOT_INTERPOLATOR);

            ObjectAnimator dotsAnimator = ObjectAnimator.ofFloat(vDotsViewHeart, DotsViewHeart.DOTS_PROGRESS, 0, 1f);
            dotsAnimator.setDuration(900);
            dotsAnimator.setStartDelay(50);
            dotsAnimator.setInterpolator(ACCELERATE_DECELERATE_INTERPOLATOR);

            animatorSet.playTogether(
                    outerCircleAnimator,
                    innerCircleAnimator,
                    starScaleYAnimator,
                    starScaleXAnimator,
                    dotsAnimator
            );

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    vCircleHeart.setInnerCircleRadiusProgress(0);
                    vCircleHeart.setOuterCircleRadiusProgress(0);
                    vDotsViewHeart.setCurrentProgress(0);
                    heartxml.setScaleX(1);
                    heartxml.setScaleY(1);
                }
            });

            animatorSet.start();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                heartxml.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150).setInterpolator(DECCELERATE_INTERPOLATOR);
                setPressed(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                boolean isInside = (x > 0 && x < getWidth() && y > 0 && y < getHeight());
                if (isPressed() != isInside) {
                    setPressed(isInside);
                }
                break;

            case MotionEvent.ACTION_UP:
                heartxml.animate().scaleX(1).scaleY(1).setInterpolator(DECCELERATE_INTERPOLATOR);
                if (isPressed()) {
                    performClick();
                    setPressed(false);
                }
                break;
        }
        return true;
    }
}