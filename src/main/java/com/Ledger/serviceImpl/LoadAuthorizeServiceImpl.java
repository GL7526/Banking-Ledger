package com.Ledger.serviceImpl;

import com.Ledger.components.responses.AuthorizationResponse;
import com.Ledger.components.responses.LoadResponse;
import com.Ledger.components.schemas.Amount;
import com.Ledger.components.schemas.DebitCredit;
import com.Ledger.components.schemas.Event;
import com.Ledger.components.schemas.Ping;
import com.Ledger.repository.LoadAuthorizeRepository;
import com.Ledger.service.LoadAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class LoadAuthorizeServiceImpl implements LoadAuthorizeService {

    @Autowired
    private LoadAuthorizeRepository loadAuthorizeRepository;

    @Override
    public LoadResponse load(String userId, String messageId, Amount amount) {

        String action = "LOAD";

        LoadResponse loadResponse;

        // load amount is specified
        if (amount != null) {

            String currency = amount.getCurrency();
            String currencyAmount = amount.getAmount();
            DebitCredit debitCredit = amount.getDebitCredit();

            // assuming all loads are accepted/approved into own user's accounts if the amount is specified
            loadResponse = loadAuthorizeRepository.saveLoad(action, messageId, userId, currency, currencyAmount, debitCredit);

        } else {
            // load amount is not specified
            // loads will be accepted/approved into another user's account only if the previous authorization was declined/denied
            loadResponse = loadAuthorizeRepository.saveLoad(action, messageId, userId);
        }

        return loadResponse;

    }


    @Override
    public AuthorizationResponse authorization(String userId, String messageId, Amount amount) {

        String action = "AUTHORIZATION";

        AuthorizationResponse authorizationResponse;

        // authorization amount is specified
        if (amount != null) {

            String currency = amount.getCurrency();
            String currencyAmount = amount.getAmount();
            DebitCredit debitCredit = amount.getDebitCredit();

            authorizationResponse = loadAuthorizeRepository.saveAuthorization(action, messageId, userId, currency, currencyAmount, debitCredit);

        } else {

            authorizationResponse = loadAuthorizeRepository.saveAuthorization(action, messageId, userId, null, String.valueOf(0), DebitCredit.DEBIT);

        }

        return authorizationResponse;

    }


    @Override
    public Ping ping() {

        Ping ping = new Ping();

        ping.setServerTime(String.valueOf(LocalDateTime.now()));

        return ping;
    }


    @Override
    public List<Event> getEvents() {
        return loadAuthorizeRepository.getEvents();
    }


    @Override
    public Map<String, Double> getBalances() {
        return loadAuthorizeRepository.getBalances();
    }

}
