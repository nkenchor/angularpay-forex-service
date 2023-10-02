
package io.angularpay.forex.models;

import lombok.Data;

import java.util.Map;

@Data
public class FixerLatestRatesResponse {
    private Boolean success;
    private Long timestamp;
    private String base;
    private String date;
    private Map<String, String> rates;
}
