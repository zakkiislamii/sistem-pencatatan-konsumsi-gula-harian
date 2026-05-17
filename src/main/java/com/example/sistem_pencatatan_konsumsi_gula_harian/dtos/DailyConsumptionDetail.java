package com.example.sistem_pencatatan_konsumsi_gula_harian.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.example.sistem_pencatatan_konsumsi_gula_harian.entities.SugarConsumption;
import com.example.sistem_pencatatan_konsumsi_gula_harian.enums.ConsumptionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyConsumptionDetail {

    private List<SugarConsumption> consumptions;
    private BigDecimal dailyTotal;
    private ConsumptionStatus dailyStatus;
    private boolean empty;
}
