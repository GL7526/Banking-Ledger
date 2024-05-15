package com.Ledger.controller;

import com.Ledger.components.requestBodies.AuthorizationRequest;
import com.Ledger.components.requestBodies.LoadRequest;
import com.Ledger.components.responses.AuthorizationResponse;
import com.Ledger.components.responses.LoadResponse;
import com.Ledger.components.responses.ServerError;
import com.Ledger.components.schemas.Event;
import com.Ledger.components.schemas.Ping;
import com.Ledger.service.LoadAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class LoadAuthorizeController {

    @Autowired
    private LoadAuthorizeService loadAuthorizeService;


    // Loads: Add money to a user (credit)
    @RequestMapping(method = RequestMethod.PUT, value = "/load", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@RequestBody LoadRequest loadRequest) {

        try {
            LoadResponse loadResponse = loadAuthorizeService.load(loadRequest.getUserId(), loadRequest.getMessageId(), loadRequest.getAmount());
            return new ResponseEntity<>(loadResponse, HttpStatus.CREATED); // 201
        } catch (Exception ex) {
            return new ResponseEntity<>(new ServerError(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }

    }


    // Authorizations: Conditionally remove money from a user (debit)
    @RequestMapping(method = RequestMethod.PUT, value = "/authorization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authorization(@RequestBody AuthorizationRequest authorizationRequest) {

        try {
            AuthorizationResponse authorizationResponse = loadAuthorizeService.authorization(authorizationRequest.getUserId(), authorizationRequest.getMessageId(), authorizationRequest.getTransactionAmount());
            return new ResponseEntity<>(authorizationResponse, HttpStatus.OK); // 200
        } catch (Exception ex) {
            return new ResponseEntity<>(new ServerError(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }

    }


    @RequestMapping(method = RequestMethod.GET, value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ping() {

        try {
            Ping ping = loadAuthorizeService.ping();
            return new ResponseEntity<>(ping, HttpStatus.OK); // 200
        } catch (Exception ex) {
            return new ResponseEntity<>(new ServerError(), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }

    }


    @RequestMapping(method = RequestMethod.GET, value = "/getEvents", produces =  MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Event>> getEvents() {
        List<Event> eventList = loadAuthorizeService.getEvents();
        return new ResponseEntity<>(eventList, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.GET, value = "/getBalances", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Double>> getBalances() {
        return new ResponseEntity<>(loadAuthorizeService.getBalances(), HttpStatus.OK);
    }


}
