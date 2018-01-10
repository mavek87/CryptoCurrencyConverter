package matteoveroni.com.cryptocurrencyconverter.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;

import matteoveroni.com.cryptocurrencyconverter.R;

/**
 * @author Matteo Veroni
 */

public class DialogBuilder {

    private Context context;
    private final AlertDialog.Builder builder;

    public DialogBuilder(Context context) {
        builder = new AlertDialog.Builder(context);
        this.context = context;
    }

    public AlertDialog build(String title, String message, boolean isStatusPositive, DialogInterface.OnClickListener dialogOnClickListener) {
        Drawable img_alertDialog;

        if (isStatusPositive)
            img_alertDialog = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_correct, null);
        else
            img_alertDialog = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_wrong, null);

        AlertDialog dialog = builder
                .setTitle(title)
                .setMessage(message)
                .setIcon(img_alertDialog)
                .setPositiveButton("Retry", dialogOnClickListener)
                .create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
