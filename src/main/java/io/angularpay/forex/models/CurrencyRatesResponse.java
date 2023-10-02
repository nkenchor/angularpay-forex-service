
package io.angularpay.forex.models;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CurrencyRatesResponse {
    private String base;
    private String date;
    private Map<String, String> rates;
}
