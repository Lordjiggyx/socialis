package com.example.socialis.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:aplication/json",
                    "Authorization:key=AAAAze9TUVM:APA91bEMCTeUImBrSi1ZKejXka8fHEpelRtu1sGvJw1O2rkSStkqZIsbL2fwGwE_lJ1hIw0jR9TdsuJxutC-bC41RwRemXLLGcF8HWwdHA8miePp9nCUFIyJ9EajVa_qIKDmtorEiFRl"
            }
    )

    @POST("fcm/Send")
    Call<Response> sendNotification(@Body Sender body );
}
