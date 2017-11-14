package com.leaderium.android;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.card.Action;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.OnActionClickListener;

import java.util.Locale;

public class TextViewAct extends Action {
    @ColorInt
    private int mActionTextColor;
    private String mActionText;
    @Nullable
    private OnActionClickListener mListener;

    public TextViewAct(@NonNull Context context) {
        super(context);
    }

    public int getTextColor() {
        return mActionTextColor;
    }

    public TextViewAct setTextColor(@ColorInt final int color) {
        this.mActionTextColor = color;
        notifyActionChanged();
        return this;
    }

    public TextViewAct setTextResourceColor(@ColorRes final int color) {
        return setTextColor(getContext().getResources().getColor(color));
    }

    public String getText() {
        return mActionText;
    }

    public TextViewAct setText(@Nullable final String text) {
        this.mActionText = text;
        notifyActionChanged();
        return this;
    }

    public TextViewAct setText(@StringRes final int textId) {
        return setText(getContext().getString(textId));
    }

    @Nullable
    public OnActionClickListener getListener() {
        return mListener;
    }

    public TextViewAct setListener(@Nullable final OnActionClickListener listener) {
        this.mListener = listener;
        notifyActionChanged();
        return this;
    }

    @Override
    protected void onRender(@NonNull final View view, @NonNull final Card card) {
        TextView textView = (TextView) view;
        textView.setText(mActionText != null ? mActionText : null);
        textView.setTextColor(mActionTextColor);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onActionClicked(view, card);
                }
            }
        });
    }
}
