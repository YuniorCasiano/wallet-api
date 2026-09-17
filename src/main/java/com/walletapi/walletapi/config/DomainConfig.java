package com.walletapi.walletapi.config;

import com.walletapi.walletapi.domain.TransferenciaDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public TransferenciaDomainService transferenciaDomainService() {
        return new TransferenciaDomainService();
    }
}