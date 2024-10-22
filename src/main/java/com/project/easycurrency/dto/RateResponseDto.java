package com.project.easycurrency.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class RateResponseDto {

    private String from;
    private String to;
    private Double rate;

}
