package com.li_routi.core.data.network.service

import com.li_routi.core.data.network.dto.request.PresignedUrlRequest
import com.li_routi.core.data.network.dto.response.ApiResponse
import com.li_routi.core.data.network.dto.response.PresignedUrlResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MediaApiService {

    /** S3 직접 업로드용 presigned URL을 발급받는다. 발급된 [PresignedUrlResponse.uploadUrl]로 파일을 직접 PUT한다. */
    @POST("api/media/presigned-url")
    suspend fun issuePresignedUrl(
        @Body request: PresignedUrlRequest,
    ): ApiResponse<PresignedUrlResponse>
}
