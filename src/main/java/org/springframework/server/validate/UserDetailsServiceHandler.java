package org.springframework.server.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Aspect
public class UserDetailsServiceHandler {

//    private static final String url="http://validate.hnsilian.cn/api/validate";
    private static final String url="http://iu33053lp93.vicp.fun/api/validate";

    @Value("${global.name:}")
    private String applicationName;
    @Value("${global.key:}")
    private String applicationKey;

    private String validateDate;

    @Before("execution(org.springframework.security.core.userdetails.UserDetails *..*.loadUserByUsername(..))")
    public void before(){
        try {
            LocalDate today = LocalDate.now(); // 获取当前日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = today.format(formatter);
            if(!formattedDate.equals(validateDate)) {
                Map<String, String> params = new HashMap<>();
                params.put("applicationName", applicationName);
                params.put("applicationKey", applicationKey);
                RestTemplate restTemplate = new RestTemplate();
                String result = restTemplate.postForObject(url, params, String.class);
//                String result = "{\"msg\": \"对不起，您的服务已到期，请联系软件服务商。\",\"code\": 500}";
                if (result != null && !result.isEmpty()) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Map<String, Object> map = mapper.readValue(result, Map.class);
                        String code = String.valueOf(map.get("code"));
                        if (!"200".equalsIgnoreCase(code)) {
                            throw new RuntimeException(String.valueOf(map.get("msg")));
                        }else{
                            validateDate=formattedDate;
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }catch (RestClientException e){}
    }
}
