package matteoveroni.com.cryptocurrencyconverter;

/**
 * @author Matteo Veroni
 */

public enum FamousCurrencies {
    Bitcoin("BTC"), Dollar("USD"), Euro("EUR"), Ethereum("ETH"), Litecoin("LTC");

    private String currencyCode;

    private FamousCurrencies(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCode() {
        return currencyCode;
    }
}
