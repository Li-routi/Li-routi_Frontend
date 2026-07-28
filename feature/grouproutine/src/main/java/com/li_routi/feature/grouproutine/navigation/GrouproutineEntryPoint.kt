package com.li_routi.feature.grouproutine.navigation

/** [GrouproutineRootNavHost]가 어느 화면부터 시작할지 고르는 진입점. */
enum class GrouproutineEntryPoint {
    /** 홈 `+` 메뉴의 "방 만들기". */
    CreateRoom,

    /** 홈 `+` 메뉴의 "초대코드로 참여". */
    JoinWithInviteCode,
}
