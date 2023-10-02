package io.angularpay.forex.adapters.inbound;

import io.angularpay.forex.domain.commands.PlatformConfigurationsConverterCommand;
import io.angularpay.forex.models.platform.PlatformConfigurationIdentifier;
import io.angularpay.forex.ports.inbound.InboundMessagingPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static io.angularpay.forex.models.platform.PlatformConfigurationSource.TOPIC;

@Service
@RequiredArgsConstructor
public class RedisMessageAdapter implements InboundMessagingPort {

    private final PlatformConfigurationsConverterCommand converterCommand;

    @Override
    public void onMessage(String message, PlatformConfigurationIdentifier identifier) {
        this.converterCommand.execute(message, identifier, TOPIC);
    }
}
