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
public class ConvertCurrencyCommandRequest extends AccessControl {

    @NotEmpty
    private String fromCurrency;

    @NotEmpty
    private String toCurrency;

    @NotEmpty
    private String amount;

    private String date;

    ConvertCurrencyCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
