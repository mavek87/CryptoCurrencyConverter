package matteoveroni.com.cryptocurrencyconverter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import matteoveroni.com.cryptocurrencyconverter.web.WebConversionAPI;
import matteoveroni.com.cryptocurrencyconverter.web.WebConversionServiceBuilder;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.Currency;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.FamousCurrencies;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.WebCurrenciesList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalcActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = CalcActivity.class.getSimpleName();

    private static final String SPINNER_TITLE_SELECT_CURRENCY = "Select currency";

    @BindView(R.id.lbl_amountToConvert)
    TextView lbl_amountToConvert;

    @BindView(R.id.txt_amountToConvert)
    EditText txt_amountToConvert;

    @BindView(R.id.spinnerConvertFrom)
    SearchableSpinner spinnerConvertFrom;

    @BindView(R.id.spinnerConvertTo)
    SearchableSpinner spinnerConvertTo;

    @BindView(R.id.lbl_conversionResult)
    TextView lbl_conversionResult;

    private ArrayAdapter currencyAdapter;
    private WebConversionAPI webConversionAPI;
    private ConnectionChecker connectionChecker;

    private int spinnerConvertFromId;
    private int spinnerConvertToId;

    private CurrencyConverter currencyConverter;
    private Currency currencyToConvertFrom;
    private Currency currencyToConvertTo;

    private Map<String, Currency> currencies = new HashMap<>();

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        lbl_conversionResult.setText("");

        final int parentID = parent.getId();

        if (parentID == spinnerConvertFromId) {
            currencyToConvertFrom = (Currency) spinnerConvertFrom.getSelectedItem();
            lbl_amountToConvert.setText(String.format("%s %s (%s):", "Amount of", currencyToConvertFrom.getName(), currencyToConvertFrom.getCode()));
        } else if (parentID == spinnerConvertToId) {
            currencyToConvertTo = (Currency) spinnerConvertTo.getSelectedItem();
        }
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

        Typeface iconFont = FontManager.getTypeface(getApplicationContext(), FontManager.FONTAWESOME);
        FontManager.markAsIconContainer(findViewById(R.id.btn_switchCurrencies), iconFont);

        txt_amountToConvert.setText("1");

        webConversionAPI = WebConversionServiceBuilder.build();
        currencyConverter = new CurrencyConverter(getApplicationContext(), webConversionAPI, lbl_conversionResult);
        connectionChecker = new ConnectionChecker(getApplicationContext());

        spinnerConvertFromId = spinnerConvertFrom.getId();
        spinnerConvertFrom.setOnItemSelectedListener(this);
        spinnerConvertFrom.setTitle(SPINNER_TITLE_SELECT_CURRENCY);

        spinnerConvertToId = spinnerConvertTo.getId();
        spinnerConvertTo.setOnItemSelectedListener(this);
        spinnerConvertTo.setTitle(SPINNER_TITLE_SELECT_CURRENCY);

        populateCurrenciesSpinners();
    }

    @OnTextChanged(R.id.txt_amountToConvert)
    public void onTextAmountToConvertChange() {
        lbl_conversionResult.setText("");
    }

    @OnTextChanged(value = R.id.txt_amountToConvert, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onEditTxtAmountToConvertChanged() {
        // Move the insertion cursor in the last position of the EditText
        txt_amountToConvert.setSelection(txt_amountToConvert.getText().length());
    }

    @OnClick(R.id.lbl_conversionResult)
    public void onClickOntoResult() {
        if (lbl_conversionResult.getText().toString().trim().isEmpty()) return;

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipboardData = ClipData.newPlainText("conversionResult", lbl_conversionResult.getText().toString());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clipboardData);
            Toast.makeText(this, "Copied into your clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_switchCurrencies)
    public void onButtonSwitchCurrenciesClicked() {
        lbl_conversionResult.setText("");

        Currency oldCurrencyToConvertFrom = currencyToConvertFrom;
        Currency oldCurrencyToConvertTo = currencyToConvertTo;

        Currency app = currencyToConvertFrom;
        currencyToConvertFrom = currencyToConvertTo;
        currencyToConvertTo = app;

        selectCurrencyInSpinner(oldCurrencyToConvertTo.getCode(), spinnerConvertFrom);
        selectCurrencyInSpinner(oldCurrencyToConvertFrom.getCode(), spinnerConvertTo);

        double amountToConvert = Double.valueOf(txt_amountToConvert.getText().toString());
        currencyConverter.convert(amountToConvert, currencyToConvertFrom.getCode(), currencyToConvertTo.getCode());
    }

    @OnClick(R.id.btn_convertCurrencies)
    public void onButtonConvertCurrenciesClicked() {
        if (currencyToConvertFrom == null || currencyToConvertTo == null) {
            Toast.makeText(this, "Select a currency to convert from and a currency to convert to", Toast.LENGTH_SHORT).show();
            return;
        }

        final String str_amountToConvert = txt_amountToConvert.getText().toString();
        if (StringUtils.isEmpty(str_amountToConvert) || !NumberUtils.isCreatable(str_amountToConvert)) {
            txt_amountToConvert.setText("0");
            lbl_conversionResult.setText("0");
            return;
        }

        lbl_conversionResult.setText("Calculating...");

        String convertFrom = currencyToConvertFrom.getCode().toLowerCase();
        String convertTo = currencyToConvertTo.getCode().toLowerCase();

        final double amountToConvert = Double.valueOf(str_amountToConvert);
        currencyConverter.convert(amountToConvert, convertFrom, convertTo);
    }

    private void populateCurrenciesSpinners() {
        final Call<WebCurrenciesList> currenciesRequest = webConversionAPI.getAllCurrenciesList();
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

                    populateSpinnersUsingDollarsAndBitcoin();
                }
            }

            @Override
            public void onFailure(Call<WebCurrenciesList> request, Throwable t) {
                Toast.makeText(CalcActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateSpinnersUsingDollarsAndBitcoin() {
        selectCurrencyInSpinner(FamousCurrencies.Bitcoin.getCode(), spinnerConvertFrom);
        selectCurrencyInSpinner(FamousCurrencies.Dollar.getCode(), spinnerConvertTo);
    }

    private void selectCurrencyInSpinner(String currencyCode, Spinner spinner) {
        Currency currency = currencies.get(currencyCode.toUpperCase());
        int currencyPosition = currencyAdapter.getPosition(currency);
        spinner.setSelection(currencyPosition);
    }
}
