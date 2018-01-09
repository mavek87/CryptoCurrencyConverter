package matteoveroni.com.cryptocurrencyconverter.web;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Matteo Veroni
 */

public final class WebConversionServiceBuilder {
    public static WebConversionAPI build() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebConversionAPI.WEB_CONVERSION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebConversionAPI.class);
    }
}
