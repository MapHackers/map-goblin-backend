package com.mapgoblin.config;

import com.mapgoblin.domain.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    /**
     * Automatic mapping of createdBy, modifiedBy data
     *
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider() {

        return new AuditorAware<String>() {
            @Override
            public Optional<String> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (null == authentication || !authentication.isAuthenticated()) {
                    return null;
                }

                Member member = (Member) authentication.getPrincipal();

                return Optional.of(member.getUserId());
            }
        };
    }
}
