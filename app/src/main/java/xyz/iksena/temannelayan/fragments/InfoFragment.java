package xyz.iksena.temannelayan.fragments;


import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.iksena.temannelayan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {


    public InfoFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @BindView(R.id.btn_toggle_tide)
    ImageButton btnToggleTide;
    @BindView(R.id.lyt_expand_tide)
    LinearLayout lytExpandTide;
    @BindView(R.id.btn_toggle_weather)
    ImageButton btnToggleWeather;
    @BindView(R.id.lyt_expand_weather)
    LinearLayout lytExpandWeather;
    @BindView(R.id.btn_toggle_iuu)
    ImageButton btnToggleIuu;
    @BindView(R.id.lyt_expand_iuu)
    LinearLayout lytExpandIuu;
    @BindView(R.id.btn_toggle_port)
    ImageButton btnTogglePort;
    @BindView(R.id.lyt_expand_port)
    LinearLayout lytExpandPort;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);
        btnToggleTide.setOnClickListener(v->toggleSectionText(v,lytExpandTide));
        lytExpandTide.setVisibility(View.GONE);
        btnToggleWeather.setOnClickListener(v->toggleSectionText(v,lytExpandWeather));
        lytExpandWeather.setVisibility(View.GONE);
        btnToggleIuu.setOnClickListener(v->toggleSectionText(v,lytExpandIuu));
        lytExpandIuu.setVisibility(View.GONE);
        btnTogglePort.setOnClickListener(v->toggleSectionText(v,lytExpandPort));
        lytExpandPort.setVisibility(View.GONE);

        if (getArguments()!=null){
            int which = getArguments().getInt("whichInfo", 0);
            switch (which){
                case 1:
                    btnToggleWeather.performClick();
                    break;
                case 0:
                    btnToggleTide.performClick();
                    break;
            }
        }
        return view;
    }

    private void toggleSectionText(View view, LinearLayout lyt) {
        boolean show = toggleArrow(view);
        if (lyt == null) return;
        if (show) {
            expand(lyt, () -> nestedScrollTo(nestedScrollView, lyt));
        } else {
            collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    public static void expand(final View v, final AnimListener animListener) {
        Animation a = expandAction(v);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animListener.onFinish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(a);
    }

    public static void expand(final View v) {
        Animation a = expandAction(v);
        v.startAnimation(a);
    }

    private static Animation expandAction(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return a;
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
        nested.post(new Runnable() {
            @Override
            public void run() {
                nested.scrollTo(500, targetView.getBottom());
            }
        });
    }

    public interface AnimListener {
        void onFinish();
    }

}
