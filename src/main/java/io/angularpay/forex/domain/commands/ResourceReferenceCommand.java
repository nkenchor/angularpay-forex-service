package io.angularpay.forex.domain.commands;

public interface ResourceReferenceCommand<T, R> {

    R map(T referenceResponse);
}
