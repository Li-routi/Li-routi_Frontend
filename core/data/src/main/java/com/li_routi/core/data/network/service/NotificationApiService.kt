package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.FcmDeviceTokenRequest
import com.li_routi.core.data.network.dto.request.UpdateNotificationSettingsRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.FcmDeviceActiveResponse
import com.li_routi.core.data.network.dto.response.NotificationListResponse
import com.li_routi.core.data.network.dto.response.NotificationReadAllResponse
import com.li_routi.core.data.network.dto.response.NotificationSettingsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @POST("api/notifications/devices")
    suspend fun registerDevice(
        @Body body: FcmDeviceTokenRequest,
    ): ApiResponse<FcmDeviceActiveResponse>

    @HTTP(method = "DELETE", path = "api/notifications/devices", hasBody = true)
    suspend fun unregisterDevice(
        @Body body: FcmDeviceTokenRequest,
    ): ApiResponse<FcmDeviceActiveResponse>

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("category") category: String?,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<NotificationListResponse>

    @PATCH("api/notifications/{notificationId}/read")
    suspend fun markRead(
        @Path("notificationId") notificationId: Long,
    ): ApiResponse<String?>

    @PATCH("api/notifications/read-all")
    suspend fun markAllRead(): ApiResponse<NotificationReadAllResponse>

    @GET("api/notifications/settings")
    suspend fun getSettings(): ApiResponse<NotificationSettingsResponse>

    @PATCH("api/notifications/settings")
    suspend fun updateSettings(
        @Body body: UpdateNotificationSettingsRequest,
    ): ApiResponse<NotificationSettingsResponse>
}
