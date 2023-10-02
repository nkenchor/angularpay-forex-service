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
public class GetHistoricalTrendForCurrencyCommandRequest extends AccessControl {

    @NotEmpty
    private String currencyCode;

    @NotEmpty
    private String fromDate;

    @NotEmpty
    private String toDate;

    GetHistoricalTrendForCurrencyCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
