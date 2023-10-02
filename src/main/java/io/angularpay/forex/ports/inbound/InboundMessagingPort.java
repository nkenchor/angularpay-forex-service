package io.angularpay.forex.ports.inbound;

import io.angularpay.forex.models.platform.PlatformConfigurationIdentifier;

public interface InboundMessagingPort {
    void onMessage(String message, PlatformConfigurationIdentifier identifier);
}
