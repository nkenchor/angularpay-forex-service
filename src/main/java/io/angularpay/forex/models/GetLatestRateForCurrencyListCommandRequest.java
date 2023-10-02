package io.angularpay.forex.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetLatestRateForCurrencyListCommandRequest extends AccessControl {

    @NotEmpty
    private List<String> currencyCodes;

    GetLatestRateForCurrencyListCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
