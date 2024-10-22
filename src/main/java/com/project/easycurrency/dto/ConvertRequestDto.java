package com.project.easycurrency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertRequestDto {

    private Double amount;
    private String from;
    private String to;

}
