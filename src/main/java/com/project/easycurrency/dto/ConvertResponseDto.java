package com.project.easycurrency.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ConvertResponseDto {

    private Double convertedAmount;
    private Double targetAmount;
    private String from;
    private String to;

}
