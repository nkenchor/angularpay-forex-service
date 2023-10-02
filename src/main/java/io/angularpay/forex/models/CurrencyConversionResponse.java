
package io.angularpay.forex.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyConversionResponse {
    private Double result;
    private Double rate;
}
