package com.example.demo.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID> {

    private static final UUID DEFAULT_TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        UUID tenant = TenantContext.getCurrentTenant();
        UUID resolved = (tenant != null) ? tenant : DEFAULT_TENANT;
        System.out.println("RESOLVED TENANT ID: " + resolved);
        return resolved;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
