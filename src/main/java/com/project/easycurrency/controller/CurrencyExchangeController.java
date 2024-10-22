package com.project.easycurrency.controller;

import com.project.easycurrency.dto.ConvertRequestDto;
import com.project.easycurrency.dto.ConvertResponseDto;
import com.project.easycurrency.dto.RateResponseDto;
import com.project.easycurrency.service.CurrencyExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @GetMapping("/convert")
    public ResponseEntity<ConvertResponseDto> convertAmountOfCurrency(@RequestBody ConvertRequestDto requestDto){
        ConvertResponseDto convertResponseDto = currencyExchangeService.convertAmount(requestDto);
        return ResponseEntity.ok(convertResponseDto);
    }

    @GetMapping
    public String getCurrencyRate(){
        String allCurrencyRates = currencyExchangeService.getCurrencyRatesForBase();
        return allCurrencyRates;
    }

    @GetMapping("/rate")
    public ResponseEntity<RateResponseDto> getCurrencyRate(@RequestParam("from") String from,
                                                           @RequestParam("to") String to){
        RateResponseDto rateResponseDto = currencyExchangeService.getCurrencyRate(from, to);
        return ResponseEntity.ok(rateResponseDto);
    }
}
