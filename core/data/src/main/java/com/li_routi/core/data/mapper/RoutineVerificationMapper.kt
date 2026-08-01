package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GroupRoutineVerificationResponse
import com.li_routi.core.data.network.dto.response.MemberRoutineVerificationResponse
import com.li_routi.core.domain.routine.GroupRoutineVerification
import com.li_routi.core.domain.routine.MemberRoutineVerification

fun MemberRoutineVerificationResponse.toDomain(): MemberRoutineVerification =
    MemberRoutineVerification(
        verificationId = verificationId,
        routineId = routineId,
        imageKey = imageKey,
        content = content,
        verifiedDate = verifiedDate,
        verifiedAt = verifiedAt,
    )

fun GroupRoutineVerificationResponse.toDomain(): GroupRoutineVerification =
    GroupRoutineVerification(
        verificationId = verificationId,
        assignmentId = assignmentId,
        imageKey = imageKey,
        content = content,
        verifiedAt = verifiedAt,
    )
