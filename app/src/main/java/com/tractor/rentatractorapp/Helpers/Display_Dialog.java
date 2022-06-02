package com.tractor.rentatractorapp.Helpers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tractor.rentatractorapp.R;

public class Display_Dialog {

     Dialog dialog;

    public Display_Dialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.material);
        alertDialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
        alertDialog.setView(view);
        TextView Message = view.findViewById(R.id.progress_message);
        Message.setText(message);
        dialog = alertDialog.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM | Gravity.CENTER;
    }
    public void display_now(){
        dialog.show();
    }
    public void dismiss_now(){
        dialog.dismiss();
    }
}
