package matteoveroni.com.cryptocurrencyconverter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.Currency;

/**
 * @author Matteo Veroni
 */

public class CurrencyAdapter extends ArrayAdapter<Currency> {

    // Your sent context
    private Context context;

    public CurrencyAdapter(@NonNull Context context, @NonNull Currency[] values) {
        super(context, android.R.layout.simple_list_item_1, values);
        this.context = context;
    }

    public CurrencyAdapter(@NonNull Context context, int textViewResourceId, @NonNull Currency[] values) {
        super(context, textViewResourceId, values);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewOptimize(position, convertView, parent);
    }

    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            TextView lbl_currency = new TextView(context);
            lbl_currency.setTextColor(Color.BLACK);

            viewHolder = new ViewHolder(lbl_currency);

            convertView = lbl_currency;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Currency currency = getItem(position);
        viewHolder.lbl_currency.setText(currency.toString());
        return convertView;
    }

    private final static class ViewHolder {
        TextView lbl_currency;

        ViewHolder(TextView lbl_currency) {
            this.lbl_currency = lbl_currency;
        }
    }
}
