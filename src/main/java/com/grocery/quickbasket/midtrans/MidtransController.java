package com.grocery.quickbasket.midtrans;

import com.grocery.quickbasket.midtrans.service.MidtransService;
import com.midtrans.httpclient.error.MidtransError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/midtrans")
@Slf4j
public class MidtransController {
    private final MidtransService midtransService;

    public MidtransController(MidtransService midtransService) {
        this.midtransService = midtransService;
    }

    @PostMapping("/notification")
    public ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> payload) {
        log.info("Received notification: {}", payload);
        try {
            midtransService.handleNotification(payload);
            return ResponseEntity.ok("Notification processed successfully");
        } catch (MidtransError e) {
            log.error("Error processing Midtrans notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing notification");
        }
    }

    @PostMapping("/test-notification")
    public ResponseEntity<String> testNotification(@RequestBody Map<String, Object> payload) {
        log.info("Received test notification: {}", payload);
        return ResponseEntity.ok("Test notification received");
    }
}
