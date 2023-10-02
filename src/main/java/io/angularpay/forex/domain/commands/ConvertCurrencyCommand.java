package io.angularpay.forex.domain.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.angularpay.forex.adapters.outbound.FixerForexServiceAdapter;
import io.angularpay.forex.configurations.AngularPayConfiguration;
import io.angularpay.forex.domain.Role;
import io.angularpay.forex.exceptions.CommandException;
import io.angularpay.forex.exceptions.ErrorObject;
import io.angularpay.forex.models.ConvertCurrencyCommandRequest;
import io.angularpay.forex.models.CurrencyConversionResponse;
import io.angularpay.forex.models.FixerConversionRequest;
import io.angularpay.forex.models.FixerConversionResponse;
import io.angularpay.forex.validation.DefaultConstraintValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static io.angularpay.forex.common.Constants.ERROR_SOURCE;
import static io.angularpay.forex.exceptions.ErrorCode.*;

@Service
public class ConvertCurrencyCommand extends AbstractCommand<ConvertCurrencyCommandRequest, CurrencyConversionResponse> {

    private final DefaultConstraintValidator validator;
    private final FixerForexServiceAdapter fixerForexServiceAdapter;
    private final AngularPayConfiguration configuration;

    public ConvertCurrencyCommand(
            ObjectMapper mapper,
            DefaultConstraintValidator validator,
            FixerForexServiceAdapter fixerForexServiceAdapter,
            AngularPayConfiguration configuration) {
        super("ConvertCurrencyCommand", mapper);
        this.validator = validator;
        this.fixerForexServiceAdapter = fixerForexServiceAdapter;
        this.configuration = configuration;
    }

    @Override
    protected CurrencyConversionResponse handle(ConvertCurrencyCommandRequest request) {
        FixerConversionRequest fixerConversionRequest = FixerConversionRequest.builder()
                .accessKey(configuration.getFixer().getApiKey())
                .from(request.getFromCurrency().toUpperCase())
                .to(request.getToCurrency().toUpperCase())
                .amount(request.getAmount())
                .date(request.getDate())
                .build();
        Optional<FixerConversionResponse> optionalFixerResponse = fixerForexServiceAdapter.convert(fixerConversionRequest);

        if (optionalFixerResponse.isPresent() && optionalFixerResponse.get().getSuccess()) {
            FixerConversionResponse fixerConversionResponse = optionalFixerResponse.get();
            return CurrencyConversionResponse.builder()
                    .result(fixerConversionResponse.getResult())
                    .rate(fixerConversionResponse.getInfo().getRate())
                    .build();
        }

        throw CommandException.builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .errorCode(CURRENCY_CONVERSION_ERROR)
                .message(CURRENCY_CONVERSION_ERROR.getDefaultMessage())
                .build();
    }

    @Override
    protected List<ErrorObject> validate(ConvertCurrencyCommandRequest request) {
        List<ErrorObject> errors = new ArrayList<>();
        if (StringUtils.hasText(request.getDate())) {
            try {
                Instant.parse(request.getDate() + "T00:00:00Z");
            } catch (DateTimeParseException exception) {
                errors.add(ErrorObject.builder()
                        .code(VALIDATION_ERROR)
                        .message("date is INVALID - must be in the format 'YYYY-MM-DD'")
                        .source(ERROR_SOURCE)
                        .build());
            }
        }
        errors.addAll(this.validator.validate(request));
        return errors;
    }

    @Override
    protected List<Role> permittedRoles() {
        return Arrays.asList(Role.ROLE_UNVERIFIED_USER, Role.ROLE_VERIFIED_USER, Role.ROLE_PLATFORM_ADMIN);
    }
}
