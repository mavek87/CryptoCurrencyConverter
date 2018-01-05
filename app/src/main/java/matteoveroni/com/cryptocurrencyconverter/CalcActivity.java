package matteoveroni.com.cryptocurrencyconverter;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import matteoveroni.com.cryptocurrencyconverter.web.WebConversionService;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.conversions.WebConversion;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.Currency;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.WebCurrenciesList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalcActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = CalcActivity.class.getSimpleName();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final int DECIMAL_DIGITS = 14;
    private static final String SPINNER_TITLE_SELECT_CURRENCY = "Select currency";

    @BindView(R.id.spinnerConvertFrom)
    SearchableSpinner spinnerConvertFrom;

    @BindView(R.id.spinnerConvertTo)
    SearchableSpinner spinnerConvertTo;

    @BindView(R.id.txt_amountToConvert)
    TextView txt_amountToConvert;

    @BindView(R.id.lbl_conversionResult)
    TextView lbl_conversionResult;

    private ArrayAdapter currencyAdapter;
    private WebConversionService conversionService;

    private int spinnerConvertFromId;
    private int spinnerConvertToId;

    private Currency currencyToConvertFrom;
    private Currency currencyToConvertTo;

    private Map<String, Currency> currencies = new HashMap<>();

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final int parentID = parent.getId();

        if (parentID == spinnerConvertFromId)
            currencyToConvertFrom = (Currency) spinnerConvertFrom.getSelectedItem();

        else if (parentID == spinnerConvertToId)
            currencyToConvertTo = (Currency) spinnerConvertTo.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        throw new RuntimeException("Unexpected exception. It should be impossible to not select anything in a spinner..");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        ButterKnife.bind(this);
        initWebConversionService();

        DECIMAL_FORMAT.setMaximumFractionDigits(DECIMAL_DIGITS);


        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.btn_switchCurrencies), iconFont);

        spinnerConvertFromId = spinnerConvertFrom.getId();
        spinnerConvertFrom.setOnItemSelectedListener(this);
        spinnerConvertFrom.setTitle(SPINNER_TITLE_SELECT_CURRENCY);

        spinnerConvertToId = spinnerConvertTo.getId();
        spinnerConvertTo.setOnItemSelectedListener(this);
        spinnerConvertTo.setTitle(SPINNER_TITLE_SELECT_CURRENCY);

        populateCurrenciesSpinners();
    }

    @OnClick(R.id.btn_switchCurrencies)
    public void onButtonSwitchCurrenciesClicked() {
        lbl_conversionResult.setText("0");

        Currency oldCurrencyToConvertFrom = currencyToConvertFrom;
        Currency oldCurrencyToConvertTo = currencyToConvertTo;

        Currency app = currencyToConvertFrom;
        currencyToConvertFrom = currencyToConvertTo;
        currencyToConvertTo = app;

        selectCurrencyInSpinner(oldCurrencyToConvertTo.getCode(), spinnerConvertFrom);
        selectCurrencyInSpinner(oldCurrencyToConvertFrom.getCode(), spinnerConvertTo);

        double amountToConvert = Double.valueOf(txt_amountToConvert.getText().toString());
        convert(amountToConvert, currencyToConvertFrom.getCode(), currencyToConvertTo.getCode());
    }

    @OnClick(R.id.btn_convertCurrencies)
    public void onButtonConvertCurrenciesClicked() {
        if (currencyToConvertFrom == null) {
            Toast.makeText(this, "Select a currency to convert from", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currencyToConvertTo == null) {
            Toast.makeText(this, "Select a currency to convert from", Toast.LENGTH_SHORT).show();
            return;
        }

        final String str_amountToConvert = txt_amountToConvert.getText().toString();
        if (StringUtils.isEmpty(str_amountToConvert) || !NumberUtils.isCreatable(str_amountToConvert)) {
            txt_amountToConvert.setText("0");
            lbl_conversionResult.setText("0");
            return;
        }

        final double amountToConvert = Double.valueOf(str_amountToConvert);

        lbl_conversionResult.setText("Calculating...");

        String convertFrom = currencyToConvertFrom.getCode().toLowerCase();
        String convertTo = currencyToConvertTo.getCode().toLowerCase();

        convert(amountToConvert, convertFrom, convertTo);
    }

    private void convert(final double amountToConvert, String convertFrom, String convertTo) {
        final Call<WebConversion> conversionRequest = conversionService.getConversion(convertFrom, convertTo);
        conversionRequest.enqueue(new Callback<WebConversion>() {
            @Override
            public void onResponse(Call<WebConversion> request, Response<WebConversion> response) {
                WebConversion conversion = response.body();

                if (conversion == null) {
                    Toast.makeText(
                            CalcActivity.this,
                            "Unknown error! Try to select different currencies",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                if (conversion.getTicker() == null) {
                    lbl_conversionResult.setText(conversion.getError());
                } else {

                    double price = Double.valueOf(conversion.getTicker().getPrice());

                    double result = amountToConvert * price;
                    lbl_conversionResult.setText(DECIMAL_FORMAT.format(result));
                }
            }

            @Override
            public void onFailure(Call<WebConversion> request, Throwable t) {
                Toast.makeText(CalcActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateCurrenciesSpinners() {
        final Call<WebCurrenciesList> currenciesRequest = conversionService.getAllCurrenciesList();
        currenciesRequest.enqueue(new Callback<WebCurrenciesList>() {

            @Override
            public void onResponse(Call<WebCurrenciesList> request, Response<WebCurrenciesList> response) {
                WebCurrenciesList currenciesList = response.body();

                if (currenciesList == null) {
                    Toast.makeText(getApplicationContext(), "currenciesList == null", Toast.LENGTH_SHORT).show();
                } else if (currenciesList.getRows() == null) {
                    Toast.makeText(getApplicationContext(), "currenciesList.getRows() == null", Toast.LENGTH_SHORT).show();
                } else {
                    currencies.clear();
                    for (Currency currency : currenciesList.getRows()) {
                        currencies.put(currency.getCode(), currency);
                    }

                    currencyAdapter = new CurrencyAdapter(getApplicationContext(), currenciesList.getRows());
                    spinnerConvertFrom.setAdapter(currencyAdapter);
                    spinnerConvertTo.setAdapter(currencyAdapter);

                    selectCurrencyInSpinner(FamousCurrencies.Bitcoin.getCode(), spinnerConvertFrom);
                    selectCurrencyInSpinner(FamousCurrencies.Dollar.getCode(), spinnerConvertTo);
                }
            }

            @Override
            public void onFailure(Call<WebCurrenciesList> request, Throwable t) {
                Toast.makeText(CalcActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectCurrencyInSpinner(String currencyCode, SearchableSpinner spinner) {
        Currency currency = currencies.get(currencyCode.toUpperCase());
        int currencyPosition = currencyAdapter.getPosition(currency);
        spinner.setSelection(currencyPosition);
    }

    private void initWebConversionService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebConversionService.WEB_CONVERSION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        conversionService = retrofit.create(WebConversionService.class);
    }
}
