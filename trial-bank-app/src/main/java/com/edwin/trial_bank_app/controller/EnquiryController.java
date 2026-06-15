package com.edwin.trial_bank_app.controller;


import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.dto.request.EnquiryRequest;
import com.edwin.trial_bank_app.service.EnquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class EnquiryController {
    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @GetMapping("/balanceEnquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestBody EnquiryRequest request) {
        return ResponseEntity.ok().body(enquiryService.balanceEnquiry(request));
    }

    @GetMapping("/nameEnquiry")
    public ResponseEntity<String> nameEnquiry(@RequestBody EnquiryRequest request) {
        return ResponseEntity.ok().body(enquiryService.nameEnquiry(request));
    }

}
