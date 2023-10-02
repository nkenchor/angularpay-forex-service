package io.angularpay.forex.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_MESSAGE_ERROR("The message format read from the given topic is invalid"),
    VALIDATION_ERROR("The request has validation errors"),
    REQUEST_NOT_FOUND("The requested resource was NOT found"),
    CURRENCY_CONVERSION_ERROR("Currency conversion failed. See logs for details"),
    GENERIC_ERROR("Generic error occurred. See stacktrace for details"),
    INVALID_HISTORY_DATE_ERROR("The history date can be a date in the past or current date - NOT a future date"),
    INVALID_DATE_RANGE_ERROR("Provide a valid date range. Both dates should NOT be the same"),
    INVALID_FROM_DATE_ERROR("Provide a valid start date. Start date should NOT be after end date"),
    INVALID_TO_DATE_ERROR("Provide a valid end date. End date should NOT be after current date"),
    AUTHORIZATION_ERROR("You do NOT have adequate permission to access this resource"),
    NO_PRINCIPAL("Principal identifier NOT provided", 500);

    private final String defaultMessage;
    private final int defaultHttpStatus;

    ErrorCode(String defaultMessage) {
        this(defaultMessage, 400);
    }

    ErrorCode(String defaultMessage, int defaultHttpStatus) {
        this.defaultMessage = defaultMessage;
        this.defaultHttpStatus = defaultHttpStatus;
    }
}
