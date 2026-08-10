package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.mapper.toApiValue
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.safeDataApiCall
import com.li_routi.core.data.network.service.NotificationApiService
import com.li_routi.core.domain.notification.NotificationCategory
import com.li_routi.core.domain.notification.NotificationPage
import com.li_routi.core.domain.notification.NotificationReadAllResult
import com.li_routi.core.domain.notification.NotificationRepository

class NotificationRepositoryImpl(
    private val api: NotificationApiService,
) : NotificationRepository {

    override suspend fun getNotifications(
        category: NotificationCategory?,
        cursor: Long?,
        size: Int,
    ): ResultState<NotificationPage> = safeDataApiCall {
        apiCall {
            api.getNotifications(
                category = category?.toApiValue(),
                cursor = cursor,
                size = size,
            )
        }.toDomain()
    }

    override suspend fun markRead(notificationId: Long): ResultState<Unit> = safeDataApiCall {
        val response = api.markRead(notificationId)
        if (!response.isSuccess) throw ApiException(response.message)
    }

    override suspend fun markAllRead(): ResultState<NotificationReadAllResult> = safeDataApiCall {
        apiCall { api.markAllRead() }.toDomain()
    }
}
