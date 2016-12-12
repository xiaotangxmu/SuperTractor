package com.xmu.supertractor.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.xmu.supertractor.R;

public class AboutDialog extends AlertDialog {

    public AboutDialog(Context context) {

        super(context);

        final View view = getLayoutInflater().inflate(R.layout.about,

                null);

        setButton(context.getText(R.string.close), (OnClickListener) null);

        setIcon(R.drawable.ww);

        setTitle("帮助" );

        setView(view);

    }

}