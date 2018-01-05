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
public class Ticker {
    @SerializedName("price")
    @Expose
    @Getter
    @Setter
    private String price;

    @SerializedName("change")
    @Expose
    @Getter
    @Setter
    private String change;

    @SerializedName("volume")
    @Expose
    @Getter
    @Setter
    private String volume;

    @SerializedName("target")
    @Expose
    @Getter
    @Setter
    private String target;

    @SerializedName("base")
    @Expose
    @Getter
    @Setter
    private String base;
}
