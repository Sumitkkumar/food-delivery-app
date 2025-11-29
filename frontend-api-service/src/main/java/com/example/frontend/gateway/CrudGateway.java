package com.example.frontend.gateway;

import com.example.frontend.exception.DownstreamException;
import com.example.frontend.integrations.CollectionsClient;
import okhttp3.OkHttpClient;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.UUID;

@Component
public class CrudGateway {

    private final CollectionsClient client;

    public CrudGateway(@Value("${app.crudBaseUrl}") String baseUrl) {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    var original = chain.request();
                    var requestId = MDC.get("requestId");
                    var newReq = original.newBuilder()
                            .addHeader("X-Request-Id", requestId != null ? requestId : UUID.randomUUID().toString())
                            .build();

                    return chain.proceed(newReq);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okClient)
                .build();

        this.client = retrofit.create(CollectionsClient.class);
    }

    public CollectionsClient client() {
        return client;
    }

    public <T> T execute(Call<T> call) {
        long start = System.currentTimeMillis();
        try {
            Response<T> response = call.execute();

            LoggerFactory.getLogger("DOWNSTREAM_LOG")
                    .info("service=crud method={} url={} status={} durationMs={}",
                            call.request().method(),
                            call.request().url(),
                            response.code(),
                            System.currentTimeMillis() - start
                    );

            if (response.isSuccessful()) return response.body();

            // Extract real error body from CRUD
            String errorMessage = "Unknown downstream error";
            if (response.errorBody() != null) {
                errorMessage = response.errorBody().string();
            }

            throw new DownstreamException(errorMessage, response.code());

        } catch (IOException e) {
            throw new DownstreamException("Downstream network failure: " + e.getMessage(), 502);
        }
    }
}
