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
public class Currency {
    @SerializedName("name")
    @Expose
    @Getter
    @Setter
    private String name;

    @SerializedName("code")
    @Expose
    @Getter
    @Setter
    private String code;

    @SerializedName("statuses")
    @Expose
    @Getter
    @Setter
    private String[] statuses;

    @Override
    public String toString() {
        return String.format("%s (%s)", name, code);
    }
}
