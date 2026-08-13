#평가용 브랜치는 final 브랜치입니다!!

# Li-routi Frontend

> 루틴 인증·챌린지·그룹 루틴(모임)으로 습관 형성을 돕는 Android 애플리케이션

---

# 프로젝트 소개

Li-routi(리루티)는 사용자가 개인 루틴을 만들고 사진으로 인증하며, 챌린지에 참여하거나
친구와 그룹 루틴 방(모임)을 꾸려 함께 습관을 이어갈 수 있도록 돕는 Android 애플리케이션입니다.
Figma: https://www.figma.com/design/VzJt8ozfEDxZJVf7GDtebx/%EB%A6%AC%EB%A3%A8%ED%8B%B0-%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84?node-id=2150-24226&p=f&m=dev

---

### 💙 팀원 소개

|                               김지아 (팀장)                                |                                강수아                                 |                                 이동진                                  |                                   정규은                                   |                                   최종희                                   |                                   임은지                                   |
|:---------------------------------------------------------------------:|:------------------------------------------------------------------:|:--------------------------------------------------------------------:|:-----------------------------------------------------------------------:|:-----------------------------------------------------------------------:|:-----------------------------------------------------------------------:|
| <img src="https://github.com/Lemon0610.png" width="150" height="150"> | <img src="https://github.com/sua710.png" width="150" height="150"> | <img src="https://github.com/East0219.png" width="150" height="150"> | <img src="https://github.com/jeongkyueun.png" width="150" height="150"> | <img src="https://github.com/jongheecode.png" width="150" height="150"> | <img src="https://github.com/mookeunji05.png" width="150" height="150"> |
|              [@Lemon0610](https://github.com/Lemon0610)               |                [@sua710](https://github.com/sua710)                |               [@East0219](https://github.com/East0219)               |             [@jeongkyueun](https://github.com/jeongkyueun)              |             [@jongheecode](https://github.com/jongheecode)              |             [@mookeunji05](https://github.com/mookeunji05)              |
|    챌린지 화면 구현 및 api 연동, 앱 내비게이션, 채팅 이모티콘 기능 구현, 상점 api 연동, 전체 코드 정비    |                      그룹 루틴(모임) 화면 구현 및 api 연동                      |                           홈 화면 구현 및 api 연동                           |                    공용 컴포넌트 구현, 마이페이지 화면 구현 및 api 연동                     |                    홈 화면 구현, 컬러·타이포그래피 구현, 그룹 api 연동                     |                공용 컴포넌트 구현, 로그인 화면 구현 및 api 연동, 채팅 api 연동                |

---

## 🛠 기술 스택 및 환경

- **Language**
  - Kotlin 2.2.10

- **UI**
  - Jetpack Compose (Compose BOM 2024.09.00)
  - Material3

- **Architecture**
  - MVVM
  - Multi-Module (core / feature)

- **주요 라이브러리**
  - Navigation Compose 2.9.3
  - Lifecycle(ViewModel) 2.9.2
  - Coroutines 1.10.2
  - Retrofit 2.11.0 + OkHttp 4.12.0 + Gson
  - Krossbow (STOMP over WebSocket) 9.3.0 — 채팅 실시간 통신

- **빌드 환경**
  - AGP 9.1.1
  - compileSdk / targetSdk 36, minSdk 24

- **의존성 관리**
  - `core:data`의 `NetworkModule`/`XxxContainer` 기반 수동 DI

---

## 📂 프로젝트 구조

멀티 모듈(Multi-Module) 구조를 따릅니다.

```
├── app/                          # 앱 진입점, 전역 DI/초기화
│
├── core/
│   ├── common/
│   │   ├── kotlin/               # 순수 Kotlin 유틸/확장 함수 (Android 의존 없음)
│   │   ├── android/              # Android 기반 공용 유틸, Base 클래스
│   │   └── ui/                   # feature 간 공용 Compose UI 컴포넌트
│   ├── domain/                   # Domain Model, Repository 인터페이스, UseCase
│   ├── data/                     # Repository 구현체, DataSource, DTO, Network
│   └── design-system/            # Theme, Color, Typography, 공용 컴포넌트
│
└── feature/
    ├── login/                    # 로그인 — 구현 예정
    ├── onboarding/                # 온보딩 — 구현 예정
    ├── home/                      # 홈, 내 루틴, 루틴 인증(촬영/업로드)
    ├── grouproutine/               # 그룹 루틴(모임) 목록/상세, 방 만들기, 초대코드 참여
    ├── challenge/                  # 챌린지 메인/찾아보기/상세 (MVP)
    ├── shopping/                  # 상점, 재화 구매 — 앱 내비게이션 연결 예정
    └── mypage/                    # 마이페이지 — 구현 예정
```

각 `feature` 모듈은 `component / navigation / screen / vm` 패키지로 구성합니다.

---

## 📌 Branch 전략

### Branch 종류 및 역할

| 브랜치 | 설명 |
| --- | --- |
| `main` | 실제 배포용 브랜치 |
| `develop` | 개발 통합 브랜치 |
| `feat/#이슈번호-기능요약` | 새로운 기능 개발 시 |
| `hotfix/#이슈번호-기능요약` | 긴급 버그 수정 시 |
| `refactor/#이슈번호-리팩토링요약` | 리팩토링 시 |

> 💡 브랜치명 형식: `타입/#이슈번호-기능요약`

✅ 예시
- `feat/#12-kakao-login`

---

## ✅ Commit 규칙

### 커밋 메시지 형식

```
타입: 주제

본문(선택)
```

### 타입 종류

| 타입 | 설명 | 예시 |
| --- | --- | --- |
| `feat` | 새로운 기능 추가 | `feat: add social login` |
| `fix` | 버그 수정 | `fix: resolve token expiry bug` |
| `hotfix` | 운영 중 긴급 버그 수정 | `hotfix: patch payment crash` |
| `refactor` | 기능 변경 없는 코드 구조 개선 | `refactor: extract auth service` |
| `perf` | 성능 개선 | `perf: optimize query indexing` |
| `style` | 코드 포맷팅, 세미콜론 등 (동작 변화 없음) | `style: apply prettier` |
| `design` | UI/CSS 등 디자인 변경 | `design: update button styles` |
| `docs` | 문서 수정 (README, 주석 등) | `docs: update API guide` |
| `comment` | 주석 추가/변경 | `comment: add function docs` |
| `test` | 테스트 코드 추가/수정 | `test: add login unit tests` |
| `build` | 빌드 시스템, 의존성 변경 | `build: bump compose bom` |
| `ci` | CI 설정 변경 (GitHub Actions 등) | `ci: fix OOM in build step` |
| `chore` | 기타 잡일 (설정, 패키지 등) | `chore: update gitignore` |
| `rename` | 파일/폴더명 변경 | `rename: move utils to lib` |
| `remove` | 파일 삭제 | `remove: delete legacy api` |
| `revert` | 이전 커밋 되돌리기 | `revert: feat add social login` |

✅ 예시
- `feat: 카카오 소셜 로그인 관련 화면 구현 완료`

---

## 🔀 PR 규칙

- `main` 브랜치에 직접 push 금지
- merge 전 빌드/테스트 진행해보기
- PR 템플릿에 타이트하게 맞추지 않고 유동적으로 작성하되, 의미가 명확하게 전달되도록 작성
- 최소 1명 이상의 리뷰를 받은 후 Merge한다 (Auto Assign으로 리뷰어 자동 배정)
- Merge 전 Conflict를 해결한다

---

## 💬 코드 리뷰 코멘트 컨벤션

리뷰 코멘트 작성 시 우선순위 태그를 붙여서 작성합니다.

| 태그 | 의미 | 설명 |
| --- | --- | --- |
| `[P1]` | 필수 수정 | merge 전 반드시 반영해야 하는 사항 (버그, 로직 오류, 보안 이슈 등) |
| `[P2]` | 권장 수정 | 반영하면 좋지만 필수는 아닌 사항 (가독성, 컨벤션, 구조 개선 등) |
| `[P3]` | 제안/의견 | 사소한 의견, nit, 선택 사항 |

✅ 예시
- `[P1] 여기서 null 체크가 빠져 있어서 NPE 발생 가능성이 있습니다.`
- `[P2] 이 로직은 ViewModel 레이어로 옮기는 게 더 적절해 보여요.`
- `[P3] 변수명을 조금 더 명확하게 하면 어떨까요? (nit)`

---

## 📦 공통 응답 처리

백엔드(Li-routi Backend)의 응답 규격과 동일하게, 모든 API 응답은
[`ApiResponse<T>`](core/data/src/main/java/com/li_routi/core/data/network/dto/response/ApiResponse.kt)로 감싸서 파싱합니다.

```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "string",
  "result": {}
}
```

- `isSuccess`: 성공 여부
- `code`: 도메인별 비즈니스 코드 (예: `COMMON400_1`, `AUTH403_1`)
- `message`: 사용자/디버깅 참고 메시지
- `result`: 실제 응답 데이터. 실패 시에는 보통 `null`

---

## ⚠️ 공통 예외 처리

- API 호출은 [`safeApiCall`](core/common/kotlin/src/main/kotlin/com/li_routi/core/common/kotlin/util/SafeApiCall.kt)로 감싸서 실행하고,
  결과는 [`ResultState`](core/common/kotlin/src/main/kotlin/com/li_routi/core/common/kotlin/util/ResultState.kt)(`Success` / `Error` / `Loading`)로 통일해 처리한다.
- 서버 에러 메시지는 [`ApiException`](core/common/kotlin/src/main/kotlin/com/li_routi/core/common/kotlin/util/ApiException.kt)으로 래핑한다.

---

## ▶ 빌드 및 실행 방법

1. Android Studio에서 프로젝트를 Open한다.
2. Gradle Sync를 진행한다.
3. 실행 기기(에뮬레이터 또는 실제 기기)를 선택한다.
4. Run 버튼을 눌러 실행한다.

---

# 화면 목록

> 앱 시작 탭은 홈으로 고정되어 있습니다. 하단 GNB(홈 / 그룹 루틴 / 챌린지 / 마이)는
> `app`의 `AppNavHost`가 직접 스위칭하며, 각 feature는 자기 화면만 노출합니다.

| 화면 이름 | 스크린 ID | 진입 경로 | 담당자      |
|-----------|-----------|-----------|----------|
| 챌린지 메인 | ChallengeScreen | 챌린지 탭 (하단 GNB) | 김지아      |
| 챌린지 찾아보기 | FindChallengeScreen | 챌린지 메인 → "새 챌린지 찾아보기" (백엔드 `GET /api/challenges` 연동) | 김지아      |
| 챌린지 상세 | ChallengeDetailScreen | 챌린지 찾아보기 → 카드 탭 | 김지아      |
| 홈 메인 | HomeScreen | 홈 탭 (하단 GNB) | 이동진, 최종희 |
| 내 루틴 | MyRoutineScreen | 홈 → "내 루틴" 카드 | 이동진, 최종희 |
| 루틴 인증 촬영 | RoutineAuthCameraScreen | 홈 스와이프 / 체크리스트 카메라 아이콘 | 이동진, 최종희 |
| 루틴 인증 작성 | RoutineAuthUploadScreen | 루틴 인증 촬영 → 촬영 완료 | 이동진, 최종희 |
| 모임 메인 | RoomListScreen | 그룹 루틴 탭 (하단 GNB) | 강수아      |
| 모임방 상세 | RoomDetailScreen | 모임 메인 → 방 카드 탭 (실시간 채팅 지원, 인증·관리는 다음 단계에서 확장 예정) | 강수아      |
| 방 만들기 | MakeRoomScreen | 홈/모임 메인 → `+` 메뉴 → "방 만들기" | 강수아      |
| 초대코드 입력 | InviteCodeScreen | 홈/모임 메인 → `+` 메뉴 → "초대코드로 참여" | 강수아      |
| 방 참여 확인 | JoinRoomConfirmDialog | 초대코드 입력 → 확인 | 강수아      |
| 루틴 체크리스트(내 루틴 관리 / 방 루틴 추가 공용) | RoutineChecklistScreen 외 | 홈 `+` 메뉴 → "내 루틴 관리" / 방 만들기 → 다음 | 정규은, 임은지 |
| 공용 하단 GNB | AppBottomNavBar | 홈/그룹 루틴/챌린지 루트 화면 공통 | 정규은, 임은지 |
| 상점 / 재화 구매 | ShopScreen / CurrencyShopScreen | 앱 내비게이션 연결 예정 | 이동진, 최종희 |
| 마이 | MyPageScreen 외 | 마이 탭 (하단 GNB) | 정규은      |
| 로그인 / 온보딩 | ProfileScreen 외 | 비로그인 시 MainActivity → LoginActivity 리다이렉트 (소셜 로그인 + 최초 로그인 시 온보딩 프로필 설정) | 임은지      |

---

# 화면 이동 플로우

```text
앱 실행
│
▼
MainActivity → AppNavHost
│   ※ 시작 탭: 홈
▼
하단 GNB (홈 / 그룹 루틴 / 챌린지 / 마이)
├── 홈 탭        → HomeNavHost (시작 화면: HomeScreen)
├── 그룹 루틴 탭  → GrouproutineRootNavHost (시작 화면: RoomListScreen)
├── 챌린지 탭    → ChallengeNavHost (시작 화면: ChallengeScreen)
└── 마이 탭      → placeholder
```

==============================
홈
==============================

```text
HomeScreen
├── "내 루틴" 카드 → MyRoutineScreen
├── "+" 메뉴 (AddMenuBottomSheet)
│      ├── 내 루틴 관리 → RoutineChecklistScreen
│      ├── 방 만들기 ─────────────┐ (그룹 루틴 탭으로 전환)
│      └── 초대코드로 참여 ───────┤ (그룹 루틴 탭으로 전환)
└── 스와이프 / 체크리스트 카메라 아이콘
       │
       ▼
   RoutineAuthCameraScreen
       │
       ▼
   RoutineAuthUploadScreen
```

==============================
챌린지
==============================

```text
ChallengeScreen (챌린지 메인)
└── "새 챌린지 찾아보기"
       │
       ▼
   FindChallengeScreen
       │
       ▼ (카드 탭)
   ChallengeDetailScreen
```

==============================
그룹 루틴 (모임)
==============================

```text
RoomListScreen (모임 메인)
├── 참여 중인 방 카드 탭
│      │
│      ▼
│  RoomDetailScreen
│      └── 실시간 채팅(STOMP 웹소켓) — 텍스트/이모티콘 전송, 메시지 이력 조회, 읽음 위치 동기화
│          (인증·관리는 다음 단계에서 확장 예정)
│
└── "+" 메뉴
       ├── 방 만들기
       │      │
       │      ▼
       │  MakeRoomScreen → 방 루틴 추가(RoutineChecklistScreen)
       │
       └── 초대코드로 참여
              │
              ▼
          InviteCodeScreen → JoinRoomConfirmDialog
```

> 모임 메인은 참여 중인 방을 카드로 보여주고, 방 만들기/초대코드 참여를 마치면 모임 메인으로 돌아옵니다.
> 방 목록 조회 API 연동은 다음 단계로 진행할 예정입니다.

---

## Copyright

© 2026 LiRouti Team. All rights reserved.

This project and its source code are proprietary and confidential.
Unauthorized copying, modification, distribution, or use of this software is strictly prohibited.
