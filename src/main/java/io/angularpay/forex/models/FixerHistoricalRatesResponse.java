
package io.angularpay.forex.models;

import lombok.Data;

import java.util.Map;

@Data
public class FixerHistoricalRatesResponse {
    private Boolean success;
    private Boolean historical;
    private String date;
    private Long timestamp;
    private String base;
    private Map<String, String> rates;
}
