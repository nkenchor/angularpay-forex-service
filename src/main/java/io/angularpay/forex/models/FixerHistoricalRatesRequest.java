package io.angularpay.forex.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixerHistoricalRatesRequest {
    private String accessKey;
    private String date;
    private String base;
    private String symbols; // optional
}
