package io.angularpay.forex.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixerLatestRatesRequest {
    private String accessKey;
    private String base;
    private String symbols; // optional
}
