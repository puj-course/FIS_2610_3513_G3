package com.example.entregaya.config;

import com.example.entregaya.interceptor.LatenciaInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LatenciaInterceptor latenciaInterceptor;

    public WebMvcConfig(LatenciaInterceptor latenciaInterceptor) {
        this.latenciaInterceptor = latenciaInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(latenciaInterceptor)
                // Aplicar a todas las rutas de la app
                .addPathPatterns("/**")
                // Excluir recursos estáticos para no ensuciar los logs
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }
}