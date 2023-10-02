package io.angularpay.forex.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetHistoricalRateForCurrencyCommandRequest extends AccessControl {

    @NotEmpty
    private String currencyCode;

    @NotEmpty
    private String date;

    GetHistoricalRateForCurrencyCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
