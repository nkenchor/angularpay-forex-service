package io.angularpay.forex.ports.outbound;

import io.angularpay.forex.domain.SavedExchangeRates;

import java.util.Optional;

public interface PersistencePort {
    SavedExchangeRates addExchangeRateRecord(SavedExchangeRates request);
    SavedExchangeRates updateExchangeRateRecord(SavedExchangeRates request);
    Optional<SavedExchangeRates> findLatestExchangeRateForCurrency(String currency);
    Optional<SavedExchangeRates> findExchangeRateForCurrencyByDate(String currency, String date);
}
