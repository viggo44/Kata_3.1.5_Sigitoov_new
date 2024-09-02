package com.example.demo.controller;

import com.example.demo.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    private static final String URL = "http://94.198.50.185:7081/api/users";
    private static final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/sendRequests")
    public String sendRequests() {
        String sessionId = getSessionId();
        HttpHeaders headers = createHeadersWithCookies(sessionId);

        User userPost = new User(3L, "James", "Brown", (byte) 30);
        String responsePost = sendRequestWithBody(HttpMethod.POST, URL, headers, userPost);

        User userPut = new User(3L, "Thomas", "Shelby", (byte) 30);
        String responsePut = sendRequestWithBody(HttpMethod.PUT, URL, headers, userPut);

        String deleteUrl = URL + "/3";
        String responseDelete = sendRequestWithBody(HttpMethod.DELETE, deleteUrl, headers, null);

        return responsePost + responsePut + responseDelete;
    }

    private String getSessionId() {
        ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.GET, HttpEntity.EMPTY, String.class);
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID")) {
                    return cookie.split(";")[0];
                }
            }
        }
        throw new IllegalStateException("No session ID found in response");
    }

    private HttpHeaders createHeadersWithCookies(String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionId);
        return headers;
    }

    private String sendRequestWithBody(HttpMethod method, String url, HttpHeaders headers, User user) {
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }
}
