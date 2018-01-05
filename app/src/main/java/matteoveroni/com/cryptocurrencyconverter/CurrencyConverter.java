package matteoveroni.com.cryptocurrencyconverter;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import matteoveroni.com.cryptocurrencyconverter.web.WebConversionService;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.conversions.Ticker;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.conversions.WebConversion;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.Currency;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author Matteo Veroni
 */

public class CurrencyConverter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final int DECIMAL_DIGITS = 8;

    private final Context context;
    private WebConversionService conversionService;
    private final TextView lbl_conversionResult;

    public CurrencyConverter(Context context, WebConversionService conversionService, TextView lbl_conversionResult) {
        this.context = context;
        this.conversionService = conversionService;
        this.lbl_conversionResult = lbl_conversionResult;
        DECIMAL_FORMAT.setMaximumFractionDigits(DECIMAL_DIGITS);
    }

    public void convert(final double amountToConvert, String convertFrom, String convertTo) {
        final Call<WebConversion> conversionRequest = conversionService.getWebConversion(convertFrom, convertTo);
        conversionRequest.enqueue(new Callback<WebConversion>() {
            @Override
            public void onResponse(Call<WebConversion> request, Response<WebConversion> response) {
                int responseCode = response.code();
                WebConversion conversion = response.body();
                if (conversion == null) {
                    showMessageInView("Invalid response, retry later");
                    return;
                } else if (responseCode == 400 || !conversion.getError().trim().isEmpty()) {
                    showMessageInView("Bad request to server. Retry later or if it persists try to select different currencies\nConversion.getError(): " + conversion.getError());
                    return;
                }

                if (conversion.getTicker() != null) {
                    Ticker ticker = conversion.getTicker();

                    double price = Double.valueOf(ticker.getPrice());
                    double result = amountToConvert * price;

//                    lbl_conversionResult.setText(String.format("%s %s", DECIMAL_FORMAT.format(result), ticker.getTarget()));
                    lbl_conversionResult.setText(String.format("%s %s", DECIMAL_FORMAT.format(result), ticker.getTarget()));

                }
            }

            @Override
            public void onFailure(Call<WebConversion> request, Throwable t) {
                showMessageInView(t.getMessage());
            }
        });
    }

    private void showMessageInView(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
