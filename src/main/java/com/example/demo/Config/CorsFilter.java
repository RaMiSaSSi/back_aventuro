package com.example.demo.Config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter{

    @Value("${app.client.url}")
    private String clientAppUrl="";

    public CorsFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String originHeader = request.getHeader("Origin");

        // if Origin absent, fallback to configured client app url
        String allowOrigin = (originHeader != null && !originHeader.isBlank()) ? originHeader : clientAppUrl;

        response.setHeader("Access-Control-Allow-Origin", allowOrigin);
        response.setHeader("Vary", "Origin");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers, Content-Disposition, enctype, multipart/form-data");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Autorise les cookies
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
