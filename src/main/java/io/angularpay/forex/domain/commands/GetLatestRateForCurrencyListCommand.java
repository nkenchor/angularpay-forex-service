package io.angularpay.forex.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.forex.adapters.outbound.FixerForexServiceAdapter;
import io.angularpay.forex.adapters.outbound.MongoAdapter;
import io.angularpay.forex.configurations.AngularPayConfiguration;
import io.angularpay.forex.domain.Role;
import io.angularpay.forex.domain.SavedExchangeRates;
import io.angularpay.forex.exceptions.ErrorObject;
import io.angularpay.forex.models.CurrencyRatesResponse;
import io.angularpay.forex.models.FixerLatestRatesRequest;
import io.angularpay.forex.models.FixerLatestRatesResponse;
import io.angularpay.forex.models.GetLatestRateForCurrencyListCommandRequest;
import io.angularpay.forex.validation.DefaultConstraintValidator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GetLatestRateForCurrencyListCommand extends AbstractCommand<GetLatestRateForCurrencyListCommandRequest, List<CurrencyRatesResponse>>
        implements NoResponseLogCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final FixerForexServiceAdapter fixerForexServiceAdapter;
    private final AngularPayConfiguration configuration;

    public GetLatestRateForCurrencyListCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            FixerForexServiceAdapter fixerForexServiceAdapter,
            AngularPayConfiguration configuration) {
        super("GetLatestRateForCurrencyListCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.fixerForexServiceAdapter = fixerForexServiceAdapter;
        this.configuration = configuration;
    }

    @Override
    protected List<CurrencyRatesResponse> handle(GetLatestRateForCurrencyListCommandRequest request) {
        return request.getCurrencyCodes().stream()
                .parallel().map(x -> {
                    String currencyCode = x.toUpperCase();

                    Optional<SavedExchangeRates> optionalSaveRate = this.mongoAdapter.findLatestExchangeRateForCurrency(currencyCode);
                    if (optionalSaveRate.isPresent()) {
                        SavedExchangeRates response = optionalSaveRate.get();
                        return CurrencyRatesResponse.builder()
                                .base(response.getBase())
                                .date(response.getDate())
                                .rates(response.getRates())
                                .build();
                    }

                    FixerLatestRatesRequest fixerLatestRatesRequest = FixerLatestRatesRequest.builder()
                            .accessKey(configuration.getFixer().getApiKey())
                            .base(currencyCode)
                            .build();
                    Optional<FixerLatestRatesResponse> optionalFixerResponse = fixerForexServiceAdapter.latestRates(fixerLatestRatesRequest);

                    if (optionalFixerResponse.isPresent() && optionalFixerResponse.get().getSuccess()) {
                        FixerLatestRatesResponse fixerLatestRatesResponse = optionalFixerResponse.get();
                        SavedExchangeRates savedExchangeRates = SavedExchangeRates.builder()
                                .base(fixerLatestRatesResponse.getBase())
                                .date(fixerLatestRatesResponse.getDate())
                                .rates(fixerLatestRatesResponse.getRates())
                                .build();

                        SavedExchangeRates response = this.mongoAdapter.addExchangeRateRecord(savedExchangeRates);

                        return CurrencyRatesResponse.builder()
                                .base(response.getBase())
                                .date(response.getDate())
                                .rates(response.getRates())
                                .build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    protected List<ErrorObject> validate(GetLatestRateForCurrencyListCommandRequest request) {
        return this.validator.validate(request);
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_UNVERIFIED_USER, Role.ROLE_VERIFIED_USER, Role.ROLE_PLATFORM_ADMIN);
    }
}
