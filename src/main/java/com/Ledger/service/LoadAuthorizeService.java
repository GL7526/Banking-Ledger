package com.Ledger.service;

import com.Ledger.components.responses.AuthorizationResponse;
import com.Ledger.components.responses.LoadResponse;
import com.Ledger.components.schemas.Amount;
import com.Ledger.components.schemas.Event;
import com.Ledger.components.schemas.Ping;

import java.util.List;
import java.util.Map;

public interface LoadAuthorizeService {

    LoadResponse load(String userId, String messageId, Amount amount);

    AuthorizationResponse authorization(String userId, String messageId, Amount amount);

    Ping ping();

    List<Event> getEvents();

    Map<String, Double> getBalances();

}
