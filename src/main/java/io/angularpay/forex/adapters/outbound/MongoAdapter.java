package io.angularpay.forex.adapters.outbound;

import io.angularpay.forex.domain.SavedExchangeRates;
import io.angularpay.forex.ports.outbound.PersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MongoAdapter implements PersistencePort {

    private final SavedExchangeRatesRepository savedExchangeRatesRepository;

    @Override
    public SavedExchangeRates addExchangeRateRecord(SavedExchangeRates request) {
        request.setCreatedOn(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return savedExchangeRatesRepository.save(request);
    }

    @Override
    public SavedExchangeRates updateExchangeRateRecord(SavedExchangeRates request) {
        request.setLastModified(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString());
        return savedExchangeRatesRepository.save(request);
    }

    @Override
    public Optional<SavedExchangeRates> findLatestExchangeRateForCurrency(String currency) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.from(ZoneOffset.UTC));
        String date = formatter.format(Instant.now());
        return savedExchangeRatesRepository.findByBaseAndDate(currency, date).stream().findFirst();
    }

    @Override
    public Optional<SavedExchangeRates> findExchangeRateForCurrencyByDate(String currency, String date) {
        return savedExchangeRatesRepository.findByBaseAndDate(currency, date).stream().findFirst();
    }
}
