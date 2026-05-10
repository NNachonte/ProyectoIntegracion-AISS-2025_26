package aiss_L3.VideoMiner.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${videominer.api.key}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        if (path.contains("/swagger") ||  path.contains("/h2-console") || path.contains("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String clientKey = req.getHeader("X-API-KEY");

        if (secretKey.equals(clientKey)) {
            chain.doFilter(request, response);
        } else {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Devuelve un error 401
            res.setContentType("application/json");
            res.getWriter().write("{\"error\": \"Acceso denegado. Falta la cabecera X-API-KEY o es incorrecta.\"}");
        }
    }
}