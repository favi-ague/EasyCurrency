package com.project.easycurrency.service;

import com.project.easycurrency.dto.ConvertRequestDto;
import com.project.easycurrency.dto.ConvertResponseDto;
import com.project.easycurrency.dto.RateResponseDto;

import java.util.Map;

public interface CurrencyExchangeService {

    ConvertResponseDto convertAmount(ConvertRequestDto requestDto);

    String getCurrencyRatesForBase();

    RateResponseDto getCurrencyRate(String from, String to);
}
