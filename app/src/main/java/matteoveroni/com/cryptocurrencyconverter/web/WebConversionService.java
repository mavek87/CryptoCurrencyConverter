package matteoveroni.com.cryptocurrencyconverter.web;

import matteoveroni.com.cryptocurrencyconverter.web.pojos.conversions.WebConversion;
import matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies.WebCurrenciesList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Matteo Veroni
 */

public interface WebConversionService {

    // Website API: https://www.cryptonator.com/api/

    // https://api.cryptonator.com/api/ticker/
    public static final String WEB_CONVERSION_BASE_URL = "https://api.cryptonator.com/api/";

    // https://api.cryptonator.com/api/full/
    public static final String WEB_COMPLETE_CONVERSION_BASE_URL = "https://api.cryptonator.com/api/full/";

    // SUPPORTED CURRENCIES => https://www.cryptonator.com/api/currencies

    @GET("ticker/{convertFrom}-{convertTo}")
    Call<WebConversion> getConversion(@Path("convertFrom") String convertFrom, @Path("convertTo") String convertTo);

    @GET("currencies")
    Call<WebCurrenciesList> getAllCurrenciesList();
}
