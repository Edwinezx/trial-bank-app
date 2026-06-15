package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.service.EnquiryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    @GetMapping("/balanceEnquiry")
    public ResponseEntity<BankResponse> balanceEnquiry(@RequestParam String accountNumber) {
        return ResponseEntity.ok().body(enquiryService.balanceEnquiryByAccountNumber(accountNumber));
    }

    @GetMapping("/nameEnquiry")
    public ResponseEntity<String> nameEnquiry(@RequestParam String accountNumber) {
        return ResponseEntity.ok().body(enquiryService.nameEnquiryByAccountNumber(accountNumber));
    }
}
