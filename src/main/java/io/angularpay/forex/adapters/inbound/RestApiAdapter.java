package io.angularpay.forex.adapters.inbound;

import io.angularpay.forex.domain.commands.*;
import io.angularpay.forex.models.*;
import io.angularpay.forex.ports.inbound.RestApiPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.angularpay.forex.helpers.Helper.fromHeaders;

@RestController
@RequestMapping("/forex/query")
@RequiredArgsConstructor
public class RestApiAdapter implements RestApiPort {

    private final GetLatestRateForCurrencyCommand getLatestRateForCurrencyCommand;
    private final GetHistoricalRateForCurrencyCommand getHistoricalRateForCurrencyCommand;
    private final GetLatestRateForCurrencyListCommand getLatestRateForCurrencyListCommand;
    private final ConvertCurrencyCommand convertCurrencyCommand;
    private final GetHistoricalTrendForCurrencyCommand getHistoricalTrendForCurrencyCommand;

    @GetMapping("/currencies/{currencyCode}")
    @ResponseBody
    @Override
    public CurrencyRatesResponse latestRateForCurrency(
            @PathVariable String currencyCode,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetLatestRateForCurrencyCommandRequest getLatestRateForCurrencyCommandRequest = GetLatestRateForCurrencyCommandRequest.builder()
                .currencyCode(currencyCode)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getLatestRateForCurrencyCommand.execute(getLatestRateForCurrencyCommandRequest);
    }

    @GetMapping("/currencies/{currencyCode}/date/{date}")
    @ResponseBody
    @Override
    public CurrencyRatesResponse historicalRateForCurrency(
            @PathVariable String currencyCode,
            @PathVariable String date,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetHistoricalRateForCurrencyCommandRequest getHistoricalRateForCurrencyCommandRequest = GetHistoricalRateForCurrencyCommandRequest.builder()
                .currencyCode(currencyCode)
                .date(date)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getHistoricalRateForCurrencyCommand.execute(getHistoricalRateForCurrencyCommandRequest);
    }

    @GetMapping("/list/{currencyCodes}")
    @ResponseBody
    @Override
    public List<CurrencyRatesResponse> latestRateForCurrencyList(
            @PathVariable List<String> currencyCodes,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetLatestRateForCurrencyListCommandRequest getLatestRateForCurrencyListCommandRequest = GetLatestRateForCurrencyListCommandRequest.builder()
                .currencyCodes(currencyCodes)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getLatestRateForCurrencyListCommand.execute(getLatestRateForCurrencyListCommandRequest);
    }

    @GetMapping("/convert/from/{fromCurrency}/to/{toCurrency}/amount/{amount}")
    @ResponseBody
    @Override
    public CurrencyConversionResponse convertCurrency(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency,
            @PathVariable String amount,
            @RequestParam(required = false) String date,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        ConvertCurrencyCommandRequest convertCurrencyCommandRequest = ConvertCurrencyCommandRequest.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .amount(amount)
                .date(date)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.convertCurrencyCommand.execute(convertCurrencyCommandRequest);
    }

    @GetMapping("/trends/currencies/{currencyCode}/from-date/{fromDate}/to-date/{toDate}")
    @ResponseBody
    @Override
    public List<CurrencyRatesResponse> historicalTrendForCurrency(
            @PathVariable String currencyCode,
            @PathVariable String fromDate,
            @PathVariable String toDate,
            @RequestHeader Map<String, String> headers) {
        AuthenticatedUser authenticatedUser = fromHeaders(headers);
        GetHistoricalTrendForCurrencyCommandRequest getHistoricalTrendForCurrencyCommandRequest = GetHistoricalTrendForCurrencyCommandRequest.builder()
                .currencyCode(currencyCode)
                .fromDate(fromDate)
                .toDate(toDate)
                .authenticatedUser(authenticatedUser)
                .build();
        return this.getHistoricalTrendForCurrencyCommand.execute(getHistoricalTrendForCurrencyCommandRequest);
    }

}
