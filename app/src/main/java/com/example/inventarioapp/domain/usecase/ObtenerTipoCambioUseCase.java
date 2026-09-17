package com.example.inventarioapp.domain.usecase;

import com.example.inventarioapp.domain.repository.ExchangeRateRepository;

public class ObtenerTipoCambioUseCase {

    private final ExchangeRateRepository exchangeRateRepository;

    public ObtenerTipoCambioUseCase(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public void ejecutar(ExchangeRateRepository.TipoCambioCallback callback) {
        exchangeRateRepository.obtenerTipoCambio(callback);
    }
}