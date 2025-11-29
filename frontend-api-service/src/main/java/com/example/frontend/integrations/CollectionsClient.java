package com.example.frontend.integrations;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface CollectionsClient {

    @POST("collections/{collection}")
    Call<Map<String, Object>> create(@Path("collection") String collection, @Body Map<String, Object> body);

    @GET("collections/{collection}/{id}")
    Call<Map<String, Object>> getById(@Path("collection") String collection, @Path("id") String id);

    @PUT("collections/{collection}/{id}")
    Call<Map<String, Object>> update(@Path("collection") String collection, @Path("id") String id,
                                     @Body Map<String, Object> body);

    @DELETE("collections/{collection}/{id}")
    Call<Void> delete(@Path("collection") String collection, @Path("id") String id);

    @POST("collections/{collection}/query")
    Call<Map<String, Object>> query(
            @Path("collection") String collection,
            @Body Map<String, Object> filterPayload
    );
}
