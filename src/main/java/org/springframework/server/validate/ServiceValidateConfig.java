package org.springframework.server.validate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceValidateConfig {

    @Bean
    public UserDetailsServiceHandler getUserDetailsServiceHandler(){
        return new UserDetailsServiceHandler();
    }

}
