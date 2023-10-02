
package io.angularpay.forex.models;

import lombok.Data;

@Data
public class FixerConversionResponse {
    private Boolean success;
    private FixerQuery query;
    private FixerInfo info;
    private String historical; // value is true if historical date was specified in the request
    private String date;
    private Double result;
}
