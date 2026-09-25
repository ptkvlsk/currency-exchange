package com.boo4er.currencyexchange.filter;

import com.boo4er.currencyexchange.util.FormParserUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class PatchFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if ("PATCH".equals(httpRequest.getMethod())) {
            String contentType = httpRequest.getContentType();
            if (contentType != null && contentType.startsWith("application/x-www-form-urlencoded")) {
                httpRequest = FormParserUtil.parseFormBody(httpRequest);
            }
        }
        chain.doFilter(httpRequest, response);
    }
}
