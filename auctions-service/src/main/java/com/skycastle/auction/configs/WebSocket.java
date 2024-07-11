package com.skycastle.auction.configs;

import com.skycastle.auction.clients.AuthClientStore;
import com.skycastle.auction.components.JWSAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.*;



@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSocket implements WebSocketMessageBrokerConfigurer {

    private final AuthenticationManager authenticationManager;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:3000").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/auction").enableSimpleBroker("/topic");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {


                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                List<String> headers =  accessor.getNativeHeader("Authorization");

                if(StompCommand.CONNECT.equals(accessor.getCommand())){ // let's authenticate connect
                    if (headers != null && headers.size() > 0){
                        String bearerToken =   headers.get(0).replace("Bearer ","");
                        log.debug("Received bearer token {}", bearerToken);
                        JWSAuthenticationToken token = (JWSAuthenticationToken) authenticationManager
                                .authenticate(new JWSAuthenticationToken(bearerToken));
                        accessor.setUser(token);
                    }else{
                        throw new BadCredentialsException("No Authorization header in request");
                    }
                }
                return message;
            }
        });
    }
}
