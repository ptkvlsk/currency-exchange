package com.boo4er.currencyexchange.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FormParserUtil {

    public static HttpServletRequest parseFormBody(HttpServletRequest req) throws IOException {
        if (!"application/x-www-form-urlencoded".equals(req.getContentType())) {
            return req;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }

        String body = sb.toString();

        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return new HttpServletRequestWrapper(req) {
            @Override
            public String getParameter(String name) {
                if (params.containsKey(name)) {
                    return params.get(name);
                }
                return super.getParameter(name);
            }
        };
    }

    private FormParserUtil() {
    }
}
