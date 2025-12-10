package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.Dto.FlouciPaymentResponse;
import com.example.demo.Dto.FlouciVerifyResponse;

@Service
public class FlouciPaymentService {

    @Value("${flouci.app-token}")
    private String appToken;

    @Value("${flouci.app-secret}")
    private String appSecret;

    @Value("${flouci.base-url}")
    private String baseUrl;

    @Value("${flouci.test.mode:false}")
    private boolean testMode;

    @Value("${app.client.url}")
    private String clientUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FlouciPaymentResponse createPayment(Double amount, String description, String successUrl, String failUrl) {
        try {
            String url = baseUrl + "/payment";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format(
                "{\"app_token\":\"%s\",\"app_secret\":\"%s\",\"amount\":%s,\"accept_url\":\"%s\",\"cancel_url\":\"%s\",\"decline_url\":\"%s\",\"session_timeout_secs\":1200,\"developer_tracking_id\":\"%s\"}",
                appToken, appSecret, amount, successUrl, failUrl, failUrl, System.currentTimeMillis()
            );

            System.out.println("Creating payment with URL: " + url);
            System.out.println("Request body: " + requestBody);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

            System.out.println("Payment response: " + response);

            JsonNode jsonNode = objectMapper.readTree(response);

            FlouciPaymentResponse paymentResponse = new FlouciPaymentResponse();
            paymentResponse.setPaymentId(jsonNode.get("result").get("payment_id").asText());

            // In test mode, redirect to test page
            if (testMode) {
                paymentResponse.setRedirectUrl(clientUrl + "/payment/test?payment_id=" + paymentResponse.getPaymentId());
            } else {
                paymentResponse.setRedirectUrl(jsonNode.get("result").get("link").asText());
            }

            paymentResponse.setSuccess(jsonNode.get("result").get("success").asBoolean());

            return paymentResponse;

        } catch (Exception e) {
            System.err.println("Failed to create Flouci payment: " + e.getMessage());
            throw new RuntimeException("Failed to create Flouci payment: " + e.getMessage());
        }
    }

    public FlouciVerifyResponse verifyPayment(String paymentId) {
        try {
            String url = baseUrl + "/verify/" + paymentId;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = String.format(
                "{\"app_token\":\"%s\",\"app_secret\":\"%s\"}",
                appToken, appSecret
            );

            System.out.println("Verifying payment with URL: " + url);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

            System.out.println("Verification response: " + response);

            JsonNode jsonNode = objectMapper.readTree(response);

            FlouciVerifyResponse verifyResponse = new FlouciVerifyResponse();
            verifyResponse.setSuccess(jsonNode.get("result").get("success").asBoolean());
            verifyResponse.setStatus(jsonNode.get("result").get("status").asText());
            verifyResponse.setAmount(jsonNode.get("result").get("amount").asDouble());

            return verifyResponse;

        } catch (Exception e) {
            System.err.println("Failed to verify Flouci payment: " + e.getMessage());
            throw new RuntimeException("Failed to verify Flouci payment: " + e.getMessage());
        }
    }
}