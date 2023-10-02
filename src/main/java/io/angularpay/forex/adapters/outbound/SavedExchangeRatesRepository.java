package io.angularpay.forex.adapters.outbound;

import io.angularpay.forex.domain.SavedExchangeRates;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SavedExchangeRatesRepository extends MongoRepository<SavedExchangeRates, String> {

    List<SavedExchangeRates> findByBaseAndDate(String base, String date);
}
