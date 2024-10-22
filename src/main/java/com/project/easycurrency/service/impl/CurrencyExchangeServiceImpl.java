package com.project.easycurrency.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.easycurrency.dto.ConvertRequestDto;
import com.project.easycurrency.dto.ConvertResponseDto;
import com.project.easycurrency.dto.RateResponseDto;
import com.project.easycurrency.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String currencyExchangerUrl = "http://api.exchangeratesapi.io/v1";

    @Value("${exchangerate.api.key}")
    private String accessKey;

    @Override
    public ConvertResponseDto convertAmount(ConvertRequestDto requestDto) {
        SymbolCurrencyResponseDto symbolCurrencyResponseDto = makeApiCallForExchangeRate(requestDto);

        Double amount = requestDto.getAmount();
        String currencyFrom = requestDto.getFrom();
        String currencyTo = requestDto.getTo();

        if(symbolCurrencyResponseDto != null){
            Double convertedAmount = (amount / symbolCurrencyResponseDto.from()) * symbolCurrencyResponseDto.to();
            return ConvertResponseDto.builder()
                    .convertedAmount(Math.round(convertedAmount * 1000.0) / 1000.0)
                    .targetAmount(amount)
                    .from(currencyFrom)
                    .to(currencyTo)
                    .build();
        } else{
            throw new RuntimeException("Error: Cannot convert currency");
        }
    }

    @Override
    public String getCurrencyRatesForBase() {
        String fetchCurrentExchangeRateUrl = currencyExchangerUrl +
                "/latest?access_key="+ accessKey;

        ResponseEntity<String> symbolCurrencyJsonResponse = restTemplate.getForEntity(fetchCurrentExchangeRateUrl, String.class);

        String allRates = null;

        try {
            JsonNode responseRootNode = objectMapper.readTree(symbolCurrencyJsonResponse.getBody());
            JsonNode ratesNode = responseRootNode.path("rates");
            allRates = objectMapper.writeValueAsString(ratesNode);
        } catch (JsonProcessingException ex){
            System.err.println("Error in converting JSON");
        }
        return allRates;
    }

    @Override
    public RateResponseDto getCurrencyRate(String from, String to) {
        SymbolCurrencyResponseDto symbolCurrencyResponseDto = makeApiCallForExchangeRate(new ConvertRequestDto(1.0, from, to));

        if(symbolCurrencyResponseDto != null){
            Double finalRate = symbolCurrencyResponseDto.to() / symbolCurrencyResponseDto.from();
            return RateResponseDto.builder()
                    .rate(Math.round(finalRate * 100.0) / 100.0)
                    .from(from)
                    .to(to)
                    .build();
        } else{
            throw new RuntimeException("Error: Cannot get currency rate");
        }
    }

    private SymbolCurrencyResponseDto makeApiCallForExchangeRate(ConvertRequestDto requestDto){
        String currencyFrom = requestDto.getFrom();
        String currencyTo = requestDto.getTo();

        String fetchCurrentExchangeRateUrl = currencyExchangerUrl +
                "/latest?access_key={access_key}&symbols=EUR,{from},{to}";

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("access_key", accessKey);
        paramsMap.put("from", currencyFrom);
        paramsMap.put("to", currencyTo);

        ResponseEntity<String> symbolCurrencyJsonResponse = restTemplate.getForEntity(fetchCurrentExchangeRateUrl, String.class, paramsMap);

        SymbolCurrencyResponseDto symbolCurrencyResponseDto = null;

        try {
            JsonNode responseRootNode = objectMapper.readTree(symbolCurrencyJsonResponse.getBody());
            JsonNode ratesNode = responseRootNode.path("rates");
            Map<String, Double> mapSymbolCurrency = objectMapper.treeToValue(ratesNode, Map.class);
            symbolCurrencyResponseDto = new SymbolCurrencyResponseDto("EUR", mapSymbolCurrency.get(currencyFrom), mapSymbolCurrency.get(currencyTo));
        } catch (JsonProcessingException ex){
            System.err.println("Error in converting JSON");
        }

        return symbolCurrencyResponseDto;
    }

    record SymbolCurrencyResponseDto(String base, Double from, Double to){};
}
