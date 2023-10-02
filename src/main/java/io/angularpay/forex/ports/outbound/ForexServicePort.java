package io.angularpay.forex.ports.outbound;

import java.util.Optional;

public interface ForexServicePort<A,B,C,D,E,F> {
    Optional<B> latestRates(A request);
    Optional<D> convert(C request);
    Optional<F> historicalRates(E request);
}
