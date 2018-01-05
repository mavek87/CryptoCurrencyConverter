package matteoveroni.com.cryptocurrencyconverter.web.pojos.conversions;

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
public class WebConversion {
    @SerializedName("timestamp")
    @Expose
    @Getter
    @Setter
    private String timestamp;

    @SerializedName("ticker")
    @Expose
    @Getter
    @Setter
    private Ticker ticker;

    @SerializedName("error")
    @Expose
    @Getter
    @Setter
    private String error;

    @SerializedName("success")
    @Expose
    @Getter
    @Setter
    private String success;
}
