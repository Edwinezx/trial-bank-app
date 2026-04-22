package com.edwin.trial_bank_app.controller;


import com.edwin.trial_bank_app.dto.*;
import com.edwin.trial_bank_app.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")

public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody CreateAccountRequest request) {
        return  userService.createAccount(request.getUserRequest(),  request.getAccountRequest());
    }

    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
        }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.DebitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponse transferAccount(@RequestBody TransferRequest request){
        return userService.TransferMoney(request);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginRequest loginRequest){
        return userService.appLogin(loginRequest);
    }
}
