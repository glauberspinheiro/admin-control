package com.revitalize.admincontrol.controllers;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class webhookJson {

    private final List<JSONObject> receivedPayloads = new ArrayList<>();

    private String latestResponse = "{}"; // Initialize with an empty JSON object

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payloadString) {
        // Parse the JSON payload
        JSONObject payload = new JSONObject(payloadString);

        // Extract required fields
        JSONObject contact = payload.optJSONObject("contact");
        String contactName = contact != null ? contact.optString("name") : "";
        String phoneNumber = contact != null ? contact.optString("phone_number") : "";

        JSONObject invoice = payload.optJSONObject("subscription");
        String invoiceStatus = invoice != null ? invoice.optString("last_status") : "";


        // Create a response JSON object
        JSONObject responseJson = new JSONObject();
        responseJson.put("contact_name", contactName);
        responseJson.put("phone_number", phoneNumber);
        responseJson.put("invoice_status", invoiceStatus);

        // Store the latest response
        latestResponse = responseJson.toString();

        // Respond with the extracted fields
        return new ResponseEntity<>(latestResponse, HttpStatus.OK);
    }

    @GetMapping("/webhook/list")
    public ResponseEntity<String> getLatestResponse() {
        return new ResponseEntity<>(latestResponse, HttpStatus.OK);
    }
}