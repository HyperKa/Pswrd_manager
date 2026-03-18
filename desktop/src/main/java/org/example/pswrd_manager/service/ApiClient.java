package org.example.pswrd_manager.service;

import org.example.pswrd_manager.repository.AuthApi;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private static Retrofit retrofit;

    public static AuthApi getAuthApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return retrofit.create(AuthApi.class);
    }
}
