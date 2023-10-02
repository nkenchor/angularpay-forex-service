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
import io.angularpay.forex.models.GetHistoricalTrendForCurrencyCommandRequest;
import io.angularpay.forex.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.angularpay.forex.common.Constants.ERROR_SOURCE;
import static io.angularpay.forex.exceptions.ErrorCode.VALIDATION_ERROR;

@Service
public class GetHistoricalTrendForCurrencyCommand extends AbstractCommand<GetHistoricalTrendForCurrencyCommandRequest, List<CurrencyRatesResponse>>
        implements NoResponseLogCommand {

    private final MongoAdapter mongoAdapter;
    private final DefaultConstraintValidator validator;
    private final FixerForexServiceAdapter fixerForexServiceAdapter;
    private final AngularPayConfiguration configuration;

    public GetHistoricalTrendForCurrencyCommand(
            ObjectMapper mapper,
            MongoAdapter mongoAdapter,
            DefaultConstraintValidator validator,
            FixerForexServiceAdapter fixerForexServiceAdapter,
            AngularPayConfiguration configuration) {
        super("GetHistoricalTrendForCurrencyCommand", mapper);
        this.mongoAdapter = mongoAdapter;
        this.validator = validator;
        this.fixerForexServiceAdapter = fixerForexServiceAdapter;
        this.configuration = configuration;
    }

    @Override
    protected List<CurrencyRatesResponse> handle(GetHistoricalTrendForCurrencyCommandRequest request) {
        if (Instant.parse(request.getFromDate() + "T00:00:00Z")
                .equals(Instant.parse(request.getToDate() + "T00:00:00Z"))) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_DATE_RANGE_ERROR)
                    .message(ErrorCode.INVALID_DATE_RANGE_ERROR.getDefaultMessage())
                    .build();
        }
        if (Instant.parse(request.getFromDate() + "T00:00:00Z")
                .isAfter(Instant.parse(request.getToDate() + "T00:00:00Z"))) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_FROM_DATE_ERROR)
                    .message(ErrorCode.INVALID_FROM_DATE_ERROR.getDefaultMessage())
                    .build();
        }
        if (Instant.parse(request.getToDate() + "T00:00:00Z")
                .isAfter(Instant.now())) {
            throw CommandException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .errorCode(ErrorCode.INVALID_TO_DATE_ERROR)
                    .message(ErrorCode.INVALID_TO_DATE_ERROR.getDefaultMessage())
                    .build();
        }

        long daysBetween = ChronoUnit.DAYS.between(
                Instant.parse(request.getFromDate() + "T00:00:00Z"),
                Instant.parse(request.getToDate() + "T00:00:00Z")
        );

        List<String> datesBetween = IntStream.iterate(0, i -> i + 1)
                .limit(daysBetween + 1)
                .mapToObj(i -> Instant.parse(request.getFromDate() + "T00:00:00Z")
                        .plus(i, ChronoUnit.DAYS).toString()
                        .split("T")[0]
                )
                .collect(Collectors.toList());

        return datesBetween.stream()
                .parallel().map(date -> {
                    String currencyCode = request.getCurrencyCode().toUpperCase();

                    Optional<SavedExchangeRates> optionalSaveRate = this.mongoAdapter.findExchangeRateForCurrencyByDate(
                            currencyCode, date);
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
                            .date(date)
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
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    protected List<ErrorObject> validate(GetHistoricalTrendForCurrencyCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();
        try {
            Instant.parse(request.getFromDate() + "T00:00:00Z");
        } catch (DateTimeParseException exception) {
            errors.add(ErrorObject.builder()
                    .code(VALIDATION_ERROR)
                    .message("from_date is INVALID - must be in the format 'YYYY-MM-DD'")
                    .source(ERROR_SOURCE)
                    .build());
        }
        try {
            Instant.parse(request.getToDate() + "T00:00:00Z");
        } catch (DateTimeParseException exception) {
            errors.add(ErrorObject.builder()
                    .code(VALIDATION_ERROR)
                    .message("to_date is INVALID - must be in the format 'YYYY-MM-DD'")
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
