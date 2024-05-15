package com.Ledger.repository;

import com.Ledger.components.responses.AuthorizationResponse;
import com.Ledger.components.responses.LoadResponse;
import com.Ledger.components.schemas.Amount;
import com.Ledger.components.schemas.DebitCredit;
import com.Ledger.components.schemas.Event;
import com.Ledger.components.schemas.ResponseCode;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class LoadAuthorizeRepository {


    private List<Event> eventList = new ArrayList<>();
    private Map<String, Double> accountBalances = new HashMap<>();


    // add money to the user's account with a specified amount
    public LoadResponse saveLoad(String action, String messageId, String userId, String currency, String currencyAmount, DebitCredit debitCredit) {

        // if somehow loading a negative value, decline transaction
        if (Double.parseDouble(currencyAmount) < 0) {

            // create event that will be tracked
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(debitCredit);
            event.setTransactionAmount(Double.valueOf(currencyAmount));
            event.setResponseCode(ResponseCode.DECLINED);

            Double balance = accountBalances.getOrDefault(userId, 0d); // if userId has a balance in accountBalances, balance is unchanged, else stay at 0

            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into a LoadResponse
            LoadResponse loadResponse = new LoadResponse();
            loadResponse.setMessageId(messageId);
            loadResponse.setUserId(userId);

            Amount amt = new Amount();
            amt.setCurrency(currency);
            amt.setDebitCredit(debitCredit);
            amt.setAmount(String.valueOf(balance));

            loadResponse.setBalance(amt);

            return loadResponse;

        } else {

            // if user already has a balance, the load amount (currencyAmount) is added to the balance
            // otherwise, the balance is equal to the load amount
            Double balance;

            if (accountBalances.containsKey(userId)) {
                balance = accountBalances.get(userId) + Double.parseDouble(currencyAmount);
            } else {
                balance = Double.parseDouble(currencyAmount);
            }
            accountBalances.put(userId, balance);

            // track event
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(debitCredit);
            event.setTransactionAmount(Double.parseDouble(currencyAmount));
            event.setResponseCode(ResponseCode.APPROVED);
            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into a LoadResponse
            LoadResponse loadResponse = new LoadResponse();
            loadResponse.setMessageId(messageId);
            loadResponse.setUserId(userId);

            Amount amt = new Amount();
            amt.setCurrency(currency);
            amt.setDebitCredit(debitCredit);
            amt.setAmount(String.valueOf(balance));

            loadResponse.setBalance(amt);

            return loadResponse;

        }

    }

    // add money to an account when the amount is not specified
    public LoadResponse saveLoad(String action, String messageId, String userId) {

        // if there is no amount specified, add money to the previous event's account ONLY if it was a declined authorization with positive transaction amount
        if (!eventList.isEmpty() && eventList.get(eventList.size() - 1).getAction().equals("AUTHORIZATION")
                && eventList.get(eventList.size() - 1).getResponseCode().name().equals("DECLINED") && eventList.get(eventList.size() - 1).getTransactionAmount() >= 0) {
            Event prevEvent = eventList.get(eventList.size() - 1);
            String prevUserId = prevEvent.getUserId();

            // amount to add
            Double amount = prevEvent.getTransactionAmount();

            Double balance;

            // if the previous account already has a balance, add amount to that balance
            // otherwise, the balance is equal to amount
            if (accountBalances.containsKey(prevUserId)) {
                balance = accountBalances.get(prevUserId) + amount;
            } else {
                balance = amount;
            }

            accountBalances.put(prevUserId, balance);

            // track event
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(DebitCredit.CREDIT);
            // we do not do event.setTransactionAmount() because there was no actual load amount sent through the request
            event.setResponseCode(ResponseCode.APPROVED);
            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into a LoadResponse
            LoadResponse loadResponse = new LoadResponse();
            loadResponse.setMessageId(messageId);
            loadResponse.setUserId(userId);

            Amount amt = new Amount();
            amt.setDebitCredit(DebitCredit.CREDIT);
            amt.setAmount(String.valueOf(balance));

            loadResponse.setBalance(amt);

            return loadResponse;


        } else {

            // previous event was NOT a declined authorization - DECLINE load and recorded balance is balance of current user's account
            Double balance = 0d;

            if (accountBalances.containsKey(userId)) {
                balance = accountBalances.get(userId);
            }

            // track event
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(DebitCredit.CREDIT);
            event.setResponseCode(ResponseCode.DECLINED);
            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into a LoadResponse
            LoadResponse loadResponse = new LoadResponse();
            loadResponse.setMessageId(messageId);
            loadResponse.setUserId(userId);

            Amount amt = new Amount();
            amt.setDebitCredit(DebitCredit.CREDIT);
            amt.setAmount(String.valueOf(balance));

            loadResponse.setBalance(amt);

            return loadResponse;

        }

    }


    public AuthorizationResponse saveAuthorization(String action, String messageId, String userId, String currency, String currencyAmount, DebitCredit debitCredit) {

        // approve authorizations only if the account's balance has the funds and transaction amount is a non-negative number
        if (accountBalances.containsKey(userId) && accountBalances.get(userId) >= Double.parseDouble(currencyAmount) && Double.parseDouble(currencyAmount) >= 0) {
            Double balance = accountBalances.get(userId) - Double.parseDouble(currencyAmount);
            accountBalances.put(userId, balance);

            // track event
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(debitCredit);
            event.setTransactionAmount(Double.parseDouble(currencyAmount));
            event.setResponseCode(ResponseCode.APPROVED);
            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into an AuthorizationResponse
            AuthorizationResponse authorizationResponse = new AuthorizationResponse();
            authorizationResponse.setUserId(userId);
            authorizationResponse.setMessageId(messageId);
            authorizationResponse.setResponseCode(ResponseCode.APPROVED);

            Amount amt = new Amount();
            amt.setCurrency(currency);
            amt.setDebitCredit(debitCredit);
            amt.setAmount(String.valueOf(balance));

            authorizationResponse.setBalance(amt);

            return authorizationResponse;

        } else {

            Double balance = accountBalances.getOrDefault(userId, 0d);

            // track event
            Event event = new Event();
            event.setAction(action);
            event.setMessageId(messageId);
            event.setUserId(userId);
            event.setDebitOrCredit(debitCredit);
            event.setTransactionAmount(Double.parseDouble(currencyAmount));
            event.setResponseCode(ResponseCode.DECLINED);
            event.setBalance(balance);
            event.setLocalDateTime(LocalDateTime.now());

            eventList.add(event);

            // set values desired to return into an AuthorizationResponse
            AuthorizationResponse authorizationResponse = new AuthorizationResponse();
            authorizationResponse.setUserId(userId);
            authorizationResponse.setMessageId(messageId);
            authorizationResponse.setResponseCode(ResponseCode.DECLINED);

            Amount amt = new Amount();
            amt.setCurrency(currency);
            amt.setDebitCredit(debitCredit);
            amt.setAmount(String.valueOf(balance));

            authorizationResponse.setBalance(amt);

            return authorizationResponse;

        }

    }


    public List<Event> getEvents() {
        return eventList;
    }


    public Map<String, Double> getBalances() {
        return accountBalances;
    }
}
