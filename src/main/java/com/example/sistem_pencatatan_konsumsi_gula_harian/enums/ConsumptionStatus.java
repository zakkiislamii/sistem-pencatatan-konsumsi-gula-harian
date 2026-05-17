package com.example.sistem_pencatatan_konsumsi_gula_harian.enums;

public enum ConsumptionStatus {
    NORMAL("Normal"),
    MELEBIHI_BATAS("Melebihi Batas Konsumsi");

    private final String label;

    ConsumptionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isNormal() {
        return this == NORMAL;
    }
}
