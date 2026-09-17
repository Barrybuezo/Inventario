package com.example.inventarioapp.data.repository;

import com.example.inventarioapp.data.remote.ExchangeRateApiService;
import com.example.inventarioapp.data.remote.RetrofitClient;
import com.example.inventarioapp.data.remote.dto.ExchangeRateResponseDto;
import com.example.inventarioapp.domain.model.TipoCambio;
import com.example.inventarioapp.domain.repository.ExchangeRateRepository;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExchangeRateRepositoryImpl implements ExchangeRateRepository {

    private static final String MONEDA_BASE = "GTQ";

    @Override
    public void obtenerTipoCambio(TipoCambioCallback callback) {
        ExchangeRateApiService api = RetrofitClient.getApiService();
        Call<ExchangeRateResponseDto> llamada = api.obtenerTasas(MONEDA_BASE);

        // enqueue() hace la petición en segundo plano sin congelar la pantalla,
        // y Retrofit entrega la respuesta ya de vuelta en el hilo principal,
        // es seguro actualizar la UI directo dentro de estos métodos
        llamada.enqueue(new Callback<ExchangeRateResponseDto>() {
            @Override
            public void onResponse(Call<ExchangeRateResponseDto> call, Response<ExchangeRateResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Double> tasas = response.body().rates;

                    if (tasas != null && tasas.containsKey("USD") && tasas.containsKey("EUR")) {
                        double tasaUsd = tasas.get("USD");
                        double tasaEur = tasas.get("EUR");
                        callback.onExito(new TipoCambio(tasaUsd, tasaEur));
                    } else {
                        callback.onError("La API no incluyó USD/EUR en la respuesta");
                    }
                } else {
                    callback.onError("No se pudo obtener el tipo de cambio");
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponseDto> call, Throwable t) {
                // Esto se dispara si no hay internet, o si el servidor no responde.
                callback.onError("Sin conexión a internet");
            }
        });
    }
}