package com.chatapp.notification_manager.send_notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAOOcaLVw:APA91bF3lBYY6lW_Zk7tD3xYWXVJYmWnyrRKdHhbhJcDO5vHWU6p_PqWBh65NR0gWJBq21dIuTOphX47SuVIshRLoGxvBnEIKQPnPVeeDs3erbFAGgt1TdG6lRDiMpli1sJH4BK7hGcU"
                    // Your server key from firebase project settings, cloud messaging
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}

