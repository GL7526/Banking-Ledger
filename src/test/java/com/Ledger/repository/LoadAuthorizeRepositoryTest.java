package com.Ledger.repository;

import com.Ledger.components.schemas.DebitCredit;
import com.Ledger.components.schemas.Event;
import com.Ledger.components.schemas.ResponseCode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class LoadAuthorizeRepositoryTest {


    @Test
    public void LoadPositiveAmtToOwnAcct() {

        // load positive amount to a specified account

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        String action = "LOAD";
        String messageId = "message1";
        String userId = "user1";
        String currency = "USD";
        String currencyAmount = "3.21";
        DebitCredit debitCredit = DebitCredit.CREDIT;

        loadAuthorizeRepository.saveLoad(action, messageId, userId, currency, currencyAmount, debitCredit);
        List<Event> eventList = loadAuthorizeRepository.getEvents();

        // make sure transaction was approved
        assertEquals(ResponseCode.APPROVED, eventList.get(eventList.size() - 1).getResponseCode());
        // make sure the amount added is the amount specified
        assertEquals((Double) 3.21, loadAuthorizeRepository.getBalances().get(userId));

    }

    @Test
    public void LoadNegativeAmtToOwnAcct() {

        // load negative amount to a specified account

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        String action = "LOAD";
        String messageId = "message1";
        String userId = "user1";
        String currency = "USD";
        String currencyAmount = "-3.21";
        DebitCredit debitCredit = DebitCredit.CREDIT;

        Double originalBalance = loadAuthorizeRepository.getBalances().get(userId);

        loadAuthorizeRepository.saveLoad(action, messageId, userId, currency, currencyAmount, debitCredit);
        List<Event> eventList = loadAuthorizeRepository.getEvents();

        // make sure transaction was declined
        assertEquals(ResponseCode.DECLINED, eventList.get(eventList.size() - 1).getResponseCode());
        // make sure the balance is unchanged for the user
        assertEquals(originalBalance, loadAuthorizeRepository.getBalances().get(userId));

    }

    @Test
    public void AuthorizationPositiveAmtGoodBalance() {

        // perform authorization on an account that has sufficient funds

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        // add money to account
        String action1 = "LOAD";
        String messageId1 = "message1";
        String userId = "user1";
        String currency1 = "USD";
        String currencyAmount1 = "103.21";
        DebitCredit debitCredit1 = DebitCredit.CREDIT;

        loadAuthorizeRepository.saveLoad(action1, messageId1, userId, currency1, currencyAmount1, debitCredit1);

        // perform authorization to debit money from an account that has money
        String action2 = "AUTHORIZATION";
        String messageId2 = "message2";
        String currency2 = "USD";
        String currencyAmount2 = "3.21";
        DebitCredit debitCredit2 = DebitCredit.DEBIT;
        loadAuthorizeRepository.saveAuthorization(action2, messageId2, userId, currency2, currencyAmount2, debitCredit2);

        // make sure authorization transaction was approved
        List<Event> eventList = loadAuthorizeRepository.getEvents();
        assertEquals(ResponseCode.APPROVED, eventList.get(eventList.size() - 1).getResponseCode());

        // check that the authorization subtracted the correct amount
        Double expectedBalance = Double.parseDouble(currencyAmount1) - Double.parseDouble(currencyAmount2);
        assertEquals(expectedBalance, loadAuthorizeRepository.getBalances().get(userId));

    }

    @Test
    public void AuthorizationPositiveAmtBadBalance() {

        // perform authorization on an account that does not have sufficient funds

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        // add money to account
        String action1 = "LOAD";
        String messageId1 = "message1";
        String userId = "user1";
        String currency1 = "USD";
        String currencyAmount1 = "103.21";
        DebitCredit debitCredit1 = DebitCredit.CREDIT;

        loadAuthorizeRepository.saveLoad(action1, messageId1, userId, currency1, currencyAmount1, debitCredit1);

        Double preAuthorizationBalance = loadAuthorizeRepository.getBalances().get(userId);

        // perform authorization to try to take out more than in balance
        String action2 = "AUTHORIZATION";
        String messageId2 = "message2";
        String currency2 = "USD";
        String currencyAmount2 = "1000";
        DebitCredit debitCredit2 = DebitCredit.DEBIT;
        loadAuthorizeRepository.saveAuthorization(action2, messageId2, userId, currency2, currencyAmount2, debitCredit2);

        // make sure authorization transaction was declined
        List<Event> eventList = loadAuthorizeRepository.getEvents();
        assertEquals(ResponseCode.DECLINED, eventList.get(eventList.size() - 1).getResponseCode());

        // check that the balance is unchanged
        Double postAuthorizationBalance = loadAuthorizeRepository.getBalances().get(userId);
        assertEquals(preAuthorizationBalance, postAuthorizationBalance);

    }

    @Test
    public void AuthorizationNegativeAmt() {

        // perform authorization with a negative amount value

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        // add money to account
        String action1 = "LOAD";
        String messageId1 = "message1";
        String userId = "user1";
        String currency1 = "USD";
        String currencyAmount1 = "103.21";
        DebitCredit debitCredit1 = DebitCredit.CREDIT;

        loadAuthorizeRepository.saveLoad(action1, messageId1, userId, currency1, currencyAmount1, debitCredit1);

        Double preAuthorizationBalance = loadAuthorizeRepository.getBalances().get(userId);

        // perform authorization
        String action2 = "AUTHORIZATION";
        String messageId2 = "message2";
        String currency2 = "USD";
        String currencyAmount2 = "-50";
        DebitCredit debitCredit2 = DebitCredit.DEBIT;
        loadAuthorizeRepository.saveAuthorization(action2, messageId2, userId, currency2, currencyAmount2, debitCredit2);

        // make sure authorization transaction was declined
        List<Event> eventList = loadAuthorizeRepository.getEvents();
        assertEquals(ResponseCode.DECLINED, eventList.get(eventList.size() - 1).getResponseCode());

        // check that the balance is unchanged
        Double postAuthorizationBalance = loadAuthorizeRepository.getBalances().get(userId);
        assertEquals(preAuthorizationBalance, postAuthorizationBalance);

    }

    @Test
    public void LoadNoAmtAfterFailedAuthorizationGoodFormat() {

        // perform a load without a specified amount, after a declined authorization

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        // perform failed authorization
        String action1 = "AUTHORIZATION";
        String messageId1 = "message1";
        String userId1 = "user1";
        String currency1 = "USD";
        String currencyAmount1 = "100";
        DebitCredit debitCredit1 = DebitCredit.DEBIT;

        Double originalBalance = loadAuthorizeRepository.getBalances().containsKey(userId1) ? loadAuthorizeRepository.getBalances().get(userId1) : 0;

        loadAuthorizeRepository.saveAuthorization(action1, messageId1, userId1, currency1, currencyAmount1, debitCredit1);

        // make sure authorization transaction was declined
        List<Event> eventList = loadAuthorizeRepository.getEvents();
        assertEquals(ResponseCode.DECLINED, eventList.get(eventList.size() - 1).getResponseCode());

        // perform a load without an amount specified
        String action2 = "LOAD";
        String messageId2 =  "message2";
        String userId2 = "user2";
        loadAuthorizeRepository.saveLoad(action2, messageId2, userId2);

        Double postLoadBalance = loadAuthorizeRepository.getBalances().containsKey(userId1) ? loadAuthorizeRepository.getBalances().get(userId1) : 0;

        // check that the amount specified in the failed authorization is added to the balance of the failed authorization's user's account
        assertEquals(Double.parseDouble(currencyAmount1), postLoadBalance - originalBalance, 0.001);

    }

    @Test
    public void LoadNoAmtAfterBadTransaction() {

        // perform a load without a specified amount, after a transaction that is not a well formatted, declined authorization

        LoadAuthorizeRepository loadAuthorizeRepository = new LoadAuthorizeRepository();

        // perform failed authorization
        String action1 = "LOAD";
        String messageId1 = "message1";
        String userId1 = "user1";
        String currency1 = "USD";
        String currencyAmount1 = "100";
        DebitCredit debitCredit1 = DebitCredit.CREDIT;

        loadAuthorizeRepository.saveLoad(action1, messageId1, userId1, currency1, currencyAmount1, debitCredit1);

        // perform a load without an amount specified - since previous transaction was not a declined authorization,
        // no balances should change and load should be declined
        String action2 = "LOAD";
        String messageId2 =  "message2";
        String userId2 = "user2";

        Double originalBalanceUser1 = loadAuthorizeRepository.getBalances().containsKey(userId1) ? loadAuthorizeRepository.getBalances().get(userId1) : 0;
        Double originalBalanceUser2 = loadAuthorizeRepository.getBalances().containsKey(userId2) ? loadAuthorizeRepository.getBalances().get(userId2) : 0;

        loadAuthorizeRepository.saveLoad(action2, messageId2, userId2);

        Double postLoadBalanceUser1 = loadAuthorizeRepository.getBalances().containsKey(userId1) ? loadAuthorizeRepository.getBalances().get(userId1) : 0;
        Double postLoadBalanceUser2 = loadAuthorizeRepository.getBalances().containsKey(userId2) ? loadAuthorizeRepository.getBalances().get(userId2) : 0;

        // check that the 2nd load was declined
        List<Event> eventList = loadAuthorizeRepository.getEvents();
        assertEquals(ResponseCode.DECLINED, eventList.get(eventList.size() - 1).getResponseCode());

        // check that the balance is not changed for either accounts
        assertEquals(originalBalanceUser1, postLoadBalanceUser1, 0.001);
        assertEquals(originalBalanceUser2, postLoadBalanceUser2, 0.001);

    }


}
