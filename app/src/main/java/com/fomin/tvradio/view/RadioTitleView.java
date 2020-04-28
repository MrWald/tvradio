package com.fomin.tvradio.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.leanback.widget.TitleViewAdapter;

import com.fomin.tvradio.R;

public class RadioTitleView extends RelativeLayout implements TitleViewAdapter.Provider {
    private final TextView mTitleView;

    private final TitleViewAdapter mTitleViewAdapter = new TitleViewAdapter() {
        @Override
        public View getSearchAffordanceView() {
            return null;
        }

        @Override
        public void setTitle(CharSequence titleText) {
            RadioTitleView.this.setTitle(titleText);
        }

        @Override
        public void setBadgeDrawable(Drawable drawable) {
            RadioTitleView.this.setBadgeDrawable(drawable);
        }

        @Override
        public void updateComponentsVisibility(int flags) {
            if ((flags & BRANDING_VIEW_VISIBLE) == BRANDING_VIEW_VISIBLE) {
                mTitleView.setVisibility(View.VISIBLE);
            } else {
                mTitleView.setVisibility(View.GONE);
            }
        }
    };

    public RadioTitleView(Context context) {
        this(context, null);
    }

    public RadioTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioTitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View root  = LayoutInflater.from(context).inflate(R.layout.radio_titleview, this);
        mTitleView = root.findViewById(R.id.title_tv);
    }

    public void setTitle(CharSequence title) {
        if (title != null) {
            mTitleView.setText(title);
            mTitleView.setVisibility(View.VISIBLE);
        }
    }


    public void setBadgeDrawable(Drawable drawable) {
        if (drawable != null) {
            mTitleView.setVisibility(View.GONE);
        }
    }

    @Override
    public TitleViewAdapter getTitleViewAdapter() {
        return mTitleViewAdapter;
    }
}
