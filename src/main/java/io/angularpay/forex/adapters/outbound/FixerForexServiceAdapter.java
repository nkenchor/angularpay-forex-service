package io.angularpay.forex.adapters.outbound;

import io.angularpay.forex.configurations.AngularPayConfiguration;
import io.angularpay.forex.models.*;
import io.angularpay.forex.ports.outbound.ForexServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FixerForexServiceAdapter implements
        ForexServicePort<FixerLatestRatesRequest, FixerLatestRatesResponse,
                FixerConversionRequest, FixerConversionResponse,
                FixerHistoricalRatesRequest, FixerHistoricalRatesResponse> {

    private final WebClient webClient;
    private final AngularPayConfiguration configuration;

    @Override
    public Optional<FixerLatestRatesResponse> latestRates(FixerLatestRatesRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(configuration.getFixer().getBaseUrl())
                .path("/latest")
                .queryParam("access_key", request.getAccessKey())
                .queryParam("base", request.getBase());

        URI uri;
        if (StringUtils.hasText(request.getSymbols())) {
            uri = builder.queryParam("symbols", request.getSymbols()).build().toUri();
        } else {
            uri = builder.build().toUri();
        }

        FixerLatestRatesResponse fixerLatestRatesResponse = webClient
                .get()
                .uri(uri.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(FixerLatestRatesResponse.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .block();
        return Objects.nonNull(fixerLatestRatesResponse) ? Optional.of(fixerLatestRatesResponse) : Optional.empty();
    }

    @Override
    public Optional<FixerConversionResponse> convert(FixerConversionRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(configuration.getFixer().getBaseUrl())
                .path("/convert")
                .queryParam("access_key", request.getAccessKey())
                .queryParam("from", request.getFrom())
                .queryParam("to", request.getTo())
                .queryParam("amount", request.getAmount());

        URI uri;
        if (StringUtils.hasText(request.getDate())) {
            uri = builder.queryParam("date", request.getDate()).build().toUri();
        } else {
            uri = builder.build().toUri();
        }

        FixerConversionResponse fixerConversionResponse = webClient
                .get()
                .uri(uri.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(FixerConversionResponse.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .block();
        return Objects.nonNull(fixerConversionResponse) ? Optional.of(fixerConversionResponse) : Optional.empty();
    }

    @Override
    public Optional<FixerHistoricalRatesResponse> historicalRates(FixerHistoricalRatesRequest request) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(configuration.getFixer().getBaseUrl())
                .path("/")
                .path(request.getDate())
                .queryParam("access_key", request.getAccessKey())
                .queryParam("base", request.getBase());

        URI uri;
        if (StringUtils.hasText(request.getSymbols())) {
            uri = builder.queryParam("symbols", request.getSymbols()).build().toUri();
        } else {
            uri = builder.build().toUri();
        }

        FixerHistoricalRatesResponse fixerHistoricalRatesResponse = webClient
                .get()
                .uri(uri.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(FixerHistoricalRatesResponse.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .block();
        return Objects.nonNull(fixerHistoricalRatesResponse) ? Optional.of(fixerHistoricalRatesResponse) : Optional.empty();
    }

}
