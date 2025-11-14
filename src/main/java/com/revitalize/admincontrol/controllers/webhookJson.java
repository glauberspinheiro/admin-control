package com.revitalize.admincontrol.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class webhookJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final List<JsonNode> receivedPayloads = new ArrayList<>();

    private String latestResponse = "{}"; // Initialize with an empty JSON object

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payloadString) {
        try {
            // Parse the JSON payload using Jackson
            JsonNode payload = objectMapper.readTree(payloadString);

            // Optionally store received payloads
            receivedPayloads.add(payload);

            // Extract required fields
            JsonNode contact = payload.path("contact");
            String contactName = contact.path("name").asText("");
            String phoneNumber = contact.path("phone_number").asText("");

            JsonNode invoice = payload.path("subscription");
            String invoiceStatus = invoice.path("last_status").asText("");

            // Create a response JSON object using Jackson
            ObjectNode responseJson = objectMapper.createObjectNode();
            responseJson.put("contact_name", contactName);
            responseJson.put("phone_number", phoneNumber);
            responseJson.put("invoice_status", invoiceStatus);

            // Store the latest response
            latestResponse = responseJson.toString();

            // Respond with the extracted fields
            return new ResponseEntity<>(latestResponse, HttpStatus.OK);
        } catch (Exception e) {
            // Return Bad Request if parsing fails
            return new ResponseEntity<>("{\"error\":\"invalid json\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/webhook/list")
    public ResponseEntity<String> getLatestResponse() {
        return new ResponseEntity<>(latestResponse, HttpStatus.OK);
    }
}