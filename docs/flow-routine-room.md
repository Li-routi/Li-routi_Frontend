# 담당 화면 플로우 - 루틴 / 방 만들기 (담당: 최종희)

> 스크린샷 원본 위치: 프로젝트 루트 상위 폴더 (`리루티/*.png`)
> README.md의 담당자 표/화면 목록은 업데이트가 안 된 옛날 버전 (참고 X)

## 진입점

홈 화면(home.png) 상단 "+" 버튼 → 바텀시트(+Button.png) 노출
- 내 루틴 관리
- 방 만들기
- 초대코드로 참여

하단 네비게이션은 홈 / 그룹 루틴 / 챌린지 / 마이 4개 탭으로 분리되어 있고,
홈 화면 자체에도 "내 루틴" 카드가 별도로 존재함 (그룹 루틴과는 별개 영역).

## 모듈 배치

- 방 만들기 / 초대코드 플로우 → `feature/grouproutine` (확인 완료)
- 내 루틴(개인) 플로우 → `feature/home` 쪽으로 추정 (홈 화면에 "내 루틴" 카드가 있고, 하단 탭의 "그룹 루틴"과 구분되는 영역이라서). 최종 확인 필요.
- `RoutineAdd`/`RoutineRepeat`/`RoutineBasic`/`RoutineDelete`/`Category` 화면은 "내 루틴 관리"(home)와 "방 만들기"(grouproutine)에서 공통으로 쓰임 → **`core/common/ui`에 공용 Composable로 구현, home/grouproutine이 둘 다 의존** (결정 완료)

## 1. 내 루틴 (개인)

```
홈 화면
└─ "내 루틴" 클릭
   └─ MyRoutine.png
      └─ (입력 케이스) RoutineCase.png
      └─ "루틴 관리" 클릭
         └─ RoutinePlus.png
```

## 2. 내 루틴 관리 (홈 + 버튼 진입)

```
홈 화면
└─ "+" 버튼 → "내 루틴 관리"
   └─ RoutineManage.png
      └─ 루틴 많아지면 스크롤 → RoutineScroll.png
      └─ "루틴 추가" 클릭
         └─ RoutineAdd.png
            ├─ 이름 작성
            ├─ 반복 탭 → 날짜 클릭 → RoutineRepeat.png (선택 날짜 채워짐)
            ├─ 기본 제공 루틴 클릭 → RoutineBasic.png
            └─ 카테고리 영역 "+" 버튼 (위치: RoutineCategory.png)
               └─ Category.png (이름 입력 → 카테고리 추가)
      └─ 우측 상단 삭제 → RoutineDelete.png
```

## 3. 방 만들기 (홈 + 버튼 진입)

```
홈 화면
└─ "+" 버튼 → "방 만들기"
   └─ MakeRoom.png (방 이름 입력)
      └─ "다음" 클릭
         └─ RoomRoutineAdd.png
            └─ 루틴 많아지면 스크롤 → RoomRoutineAddScroll.png
            └─ 아래 항목은 "내 루틴 관리"와 동일 화면 재사용
               - 루틴 추가: RoutineAdd.png
               - 반복 일자 선택: RoutineRepeat.png
               - 기본 제공 루틴: RoutineBasic.png
               - 루틴 삭제: RoutineDelete.png
               - 카테고리 추가: Category.png
               - 카테고리 삭제: (RoutineCategory.png 화면 내 동작)
```

## 4. 초대코드 입력 (홈 + 버튼 진입)

```
홈 화면
└─ "+" 버튼 → "초대코드 입력"
   └─ InviteCode.png
      └─ 입력 시 키보드 노출 → InviteCodeKeyBoard.png
      └─ 에러 → InviteCodeError.png
      └─ 성공(확인) → InviteCodeSuccess.png
```

## 공통 재사용 화면

`RoutineAdd` / `RoutineRepeat` / `RoutineBasic` / `RoutineDelete` / `RoutineCategory` / `Category` 화면은
"내 루틴 관리" 플로우와 "방 만들기 → 루틴 추가" 플로우에서 동일하게 재사용됨.
→ 공용 컴포넌트/화면으로 설계하는 게 맞아 보임 (구현 시 재확인).
