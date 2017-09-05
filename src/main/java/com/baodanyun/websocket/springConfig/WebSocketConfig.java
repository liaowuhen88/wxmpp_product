package com.baodanyun.websocket.springConfig;

import com.baodanyun.websocket.core.handle.CustomerWebSocketHandler;
import com.baodanyun.websocket.core.handle.MobileCustomerWebSocketHandler;
import com.baodanyun.websocket.core.handle.NewVisitorWebSocketHandler;
import com.baodanyun.websocket.core.handle.VisitorWebSocketHandler;
import com.baodanyun.websocket.core.interceptors.CustomerWebHandshakeInterceptor;
import com.baodanyun.websocket.core.interceptors.VisitorWebHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * websocket页面跳转
 */
@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new NewVisitorWebSocketHandler(), "/sockjs/newVisitor").withSockJS().setHeartbeatTime(15000);
		registry.addHandler(new VisitorWebSocketHandler(), "/sockjs/visitor").addInterceptors(new VisitorWebHandshakeInterceptor()).withSockJS().setHeartbeatTime(15000);
		registry.addHandler(new CustomerWebSocketHandler(),"/sockjs/customer/chat").addInterceptors(new CustomerWebHandshakeInterceptor()).withSockJS().setHeartbeatTime(15000);
		registry.addHandler(new MobileCustomerWebSocketHandler(), "/sockjs/mobileCustomer/chat").addInterceptors(new CustomerWebHandshakeInterceptor()).withSockJS().setHeartbeatTime(15000);

	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
