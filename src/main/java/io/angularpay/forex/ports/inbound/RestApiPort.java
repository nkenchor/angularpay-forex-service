package io.angularpay.forex.ports.inbound;

import io.angularpay.forex.models.CurrencyConversionResponse;
import io.angularpay.forex.models.CurrencyRatesResponse;

import java.util.List;
import java.util.Map;

public interface RestApiPort {

    CurrencyRatesResponse latestRateForCurrency(String currencyCode, Map<String, String> headers);
    CurrencyRatesResponse historicalRateForCurrency(String currencyCode, String date, Map<String, String> headers);
    List<CurrencyRatesResponse> latestRateForCurrencyList(List<String> currencyCodes, Map<String, String> headers);
    CurrencyConversionResponse convertCurrency(String fromCurrency, String toCurrency, String amount, String date, Map<String, String> headers);
    List<CurrencyRatesResponse> historicalTrendForCurrency(String currencyCode, String fromDate, String toDate, Map<String, String> headers);
}
