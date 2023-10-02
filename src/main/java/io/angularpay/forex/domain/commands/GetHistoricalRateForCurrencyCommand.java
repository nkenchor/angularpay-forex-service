package io.angularpay.forex.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.forex.adapters.outbound.FixerForexServiceAdapter;
import io.angularpay.forex.adapters.outbound.MongoAdapter;
import io.angularpay.forex.configurations.AngularPayConfiguration;
import io.angularpay.forex.domain.Role;
import io.angularpay.forex.domain.SavedExchangeRates;
import io.angularpay.forex.exceptions.CommandException;
import io.angularpay.forex.exceptions.ErrorCode;
import io.angularpay.forex.exceptions.ErrorObject;
import io.angularpay.forex.models.CurrencyRatesResponse;
import io.angularpay.forex.models.FixerHistoricalRatesRequest;
import io.angularpay.forex.models.FixerHistoricalRatesResponse;
import io.angularpay.forex.models.GetHistoricalRateForCurrencyCommandRequest;
import io.angularpay.forex.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.angularpay.forex.common.Constants.ERROR_SOURCE;
import static io.angularpay.forex.exceptions.ErrorCode.REQUEST_NOT_FOUND;
import static io.angularpay.forex.exceptions.ErrorCode.VALIDATION_ERROR;

@Service
public class GetHistoricalRateForCurrencyCommand extends AbstractCommand<GetHistoricalRateForCurrencyCommandRequest, CurrencyRatesResponse>
    implements NoResponseLogCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final FixerForexServiceAdapter fixerForexServiceAdapter;
    private final AngularPayConfiguration configuration;

    public GetHistoricalRateForCurrencyCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            FixerForexServiceAdapter fixerForexServiceAdapter,
            AngularPayConfiguration configuration) {
        super("GetHistoricalRateForCurrencyCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.fixerForexServiceAdapter = fixerForexServiceAdapter;
        this.configuration = configuration;
    }

    @Override
    protected CurrencyRatesResponse handle(GetHistoricalRateForCurrencyCommandRequest request) {
        if (Instant.parse(request.getDate() + "T00:00:00Z").isAfter(Instant.now())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_HISTORY_DATE_ERROR)
                    .message(ErrorCode.INVALID_HISTORY_DATE_ERROR.getDefaultMessage())
                    .build();
        }

        String currencyCode = request.getCurrencyCode().toUpperCase();

        Optional<SavedExchangeRates> optionalSaveRate = this.mongoAdapter.findExchangeRateForCurrencyByDate(
                currencyCode, request.getDate());
        if (optionalSaveRate.isPresent()) {
            SavedExchangeRates response = optionalSaveRate.get();
            return CurrencyRatesResponse.builder()
                    .base(response.getBase())
                    .date(response.getDate())
                    .rates(response.getRates())
                    .build();
        }

        FixerHistoricalRatesRequest fixerHistoricalRatesRequest = FixerHistoricalRatesRequest.builder()
                .accessKey(configuration.getFixer().getApiKey())
                .base(currencyCode)
                .date(request.getDate())
                .build();
        Optional<FixerHistoricalRatesResponse> optionalFixerResponse = fixerForexServiceAdapter.historicalRates(fixerHistoricalRatesRequest);

        if (optionalFixerResponse.isPresent() && optionalFixerResponse.get().getSuccess()) {
            FixerHistoricalRatesResponse fixerLatestRatesResponse = optionalFixerResponse.get();
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

        throw CommandException.builder()
                .status(HttpStatus.NOT_FOUND)
                .errorCode(REQUEST_NOT_FOUND)
                .message(REQUEST_NOT_FOUND.getDefaultMessage())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(GetHistoricalRateForCurrencyCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();
        try {
            Instant.parse(request.getDate() + "T00:00:00Z");
        } catch (DateTimeParseException exception) {
            errors.add(ErrorObject.builder()
                    .code(VALIDATION_ERROR)
                    .message("date is INVALID - must be in the format 'YYYY-MM-DD'")
                    .source(ERROR_SOURCE)
                    .build());
        }
        errors.addAll(this.validator.validate(request));
        return errors;
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_UNVERIFIED_USER, Role.ROLE_VERIFIED_USER, Role.ROLE_PLATFORM_ADMIN);
    }
}
