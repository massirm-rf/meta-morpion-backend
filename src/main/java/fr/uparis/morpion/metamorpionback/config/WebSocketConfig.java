package fr.uparis.morpion.metamorpionback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/swns")
                .enableSimpleBroker(
                        "/notifications",
                        "/notifications-count",
                        "/role",
                        "/role/update-account",
                        "/import-data-progress",
                        "/sample-data-progress",
                        "/email-sync"
                );
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Bean
    public Map<String, ScheduledFuture<?>> getUserSchedulers() {
        return new HashMap<>();
    }
}