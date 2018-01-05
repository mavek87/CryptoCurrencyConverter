package matteoveroni.com.cryptocurrencyconverter.web.pojos.currencies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Matteo Veroni
 */

@NoArgsConstructor
@ToString
public class WebCurrenciesList {

    @SerializedName("rows")
    @Expose
    @Getter
    @Setter
    private Currency[] rows;
}
