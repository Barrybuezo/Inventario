package com.example.inventarioapp.domain.repository;

import com.example.inventarioapp.domain.model.TipoCambio;

public interface ExchangeRateRepository {
    interface TipoCambioCallback {
        void onExito(TipoCambio tipoCambio);
        void onError(String mensajeError);
    }

    void obtenerTipoCambio(TipoCambioCallback callback);
}