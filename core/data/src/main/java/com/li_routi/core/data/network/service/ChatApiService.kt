package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.UpdateChatReadRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.ChatMessageListResponse
import com.li_routi.core.data.network.dto.response.EmoticonListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {

    @GET("api/groups/{groupId}/chat/messages")
    suspend fun getChatMessages(
        @Path("groupId") groupId: Long,
        @Query("cursor") cursor: Long?,
        @Query("size") size: Int?,
    ): ApiResponse<ChatMessageListResponse>

    // 응답 result가 항상 비어 있어(성공해도 페이로드 없음) Unit?으로 받는다.
    @PATCH("api/groups/{groupId}/chat/read")
    suspend fun updateReadPosition(
        @Path("groupId") groupId: Long,
        @Body request: UpdateChatReadRequest,
    ): ApiResponse<Unit?>

    @GET("api/chat/emoticons")
    suspend fun getEmoticons(): ApiResponse<EmoticonListResponse>
}
