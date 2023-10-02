package io.angularpay.forex.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FixerConversionRequest {
    private String accessKey;
    private String from ;
    private String to;
    private String amount;
    private String date; // optional
}
