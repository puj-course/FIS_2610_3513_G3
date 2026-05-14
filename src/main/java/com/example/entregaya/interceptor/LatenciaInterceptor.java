package com.example.entregaya.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LatenciaInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LatenciaInterceptor.class);

    /** Atributo usado para almacenar el timestamp de inicio en el request. */
    private static final String ATTR_INICIO = "latencia_inicio_ms";

    /** Umbral en milisegundos a partir del cual se emite una advertencia. */
    public static final long UMBRAL_MS = 1000L;

    /**
     * Se ejecuta ANTES de que el controlador procese la petición.
     * Guarda el timestamp de inicio como atributo del request.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        request.setAttribute(ATTR_INICIO, System.currentTimeMillis());
        return true;
    }

    /**
     * Se ejecuta DESPUÉS de que el controlador haya enviado la respuesta.
     * Calcula la latencia y la registra en el log.
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Object inicio = request.getAttribute(ATTR_INICIO);
        if (inicio == null) {
            return;
        }

        long latencia = System.currentTimeMillis() - (Long) inicio;
        String metodo = request.getMethod();
        String uri    = request.getRequestURI();
        int    status = response.getStatus();

        if (latencia > UMBRAL_MS) {
            log.warn("[LATENCIA] {} {} → {} ms (⚠ supera umbral de {} ms) [HTTP {}]",
                    metodo, uri, latencia, UMBRAL_MS, status);
        } else {
            log.info("[LATENCIA] {} {} → {} ms [HTTP {}]",
                    metodo, uri, latencia, status);
        }
    }
}