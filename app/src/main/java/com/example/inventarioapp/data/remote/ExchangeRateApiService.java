package com.example.inventarioapp.data.remote;

import com.example.inventarioapp.data.remote.dto.ExchangeRateResponseDto;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateApiService {
    @GET("v6/latest/{base}")
    Call<ExchangeRateResponseDto> obtenerTasas(@Path("base") String base);
}
