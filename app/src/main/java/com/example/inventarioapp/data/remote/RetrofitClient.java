package com.example.inventarioapp.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://open.er-api.com/";
    private static ExchangeRateApiService servicio;

    // Patrón singleton simple: crea el servicio una sola vez y lo reutiliza,
    // en vez de construir un cliente Retrofit nuevo cada vez que se necesita.
    public static ExchangeRateApiService getApiService() {
        if (servicio == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            servicio = retrofit.create(ExchangeRateApiService.class);
        }
        return servicio;
    }
}
