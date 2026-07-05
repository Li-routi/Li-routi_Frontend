# Li-routi Frontend

> 집안일 루틴과 소모품을 관리하고, 필요한 상품까지 연결해주는 Android 애플리케이션

---

# 프로젝트 소개

Li-routi는 사용자의 집안일 루틴과 소모품 사용 주기를 관리하고,
필요한 시점에 쇼핑 기능과 연계하여 효율적인 생활을 돕는 Android 애플리케이션입니다.

---

### 💙 팀원 소개

|                                  김지아 (팀장)                                  |                                  강수아                                  |                                  이동진                                  |                                  정규은                                  |                                  최종희                                  |                                  임은지                                  |
|:---------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|:-------------------------------------------------------------------------:|
| <img src="https://github.com/Lemon0610.png" width="150" height="150">       | <img src="https://github.com/sua710.png" width="150" height="150">        | <img src="https://github.com/East0219.png" width="150" height="150">      | <img src="https://github.com/jeongkyueun.png" width="150" height="150">   | <img src="https://github.com/jongheecode.png" width="150" height="150">   |                                     -                                      |
|                  [@Lemon0610](https://github.com/Lemon0610)                  |                 [@sua710](https://github.com/sua710)                  |                 [@East0219](https://github.com/East0219)                |             [@jeongkyueun](https://github.com/jeongkyueun)              |             [@jongheecode](https://github.com/jongheecode)              |                                     -                                      |
|                     스플래시, 로그인, 회원가입, 휴대폰 인증                     |                       메인 화면, 루틴 관리, 소모품 관리                       |                                   캘린더                                   |                     마이페이지, 주문내역, 정기구독, 결제수단                     |                              알림, 코치마크                              |                       쇼핑, 상품 검색, 장바구니, 결제                       |

---

## 🛠 기술 스택 및 환경

- **Language**
  - Kotlin

- **UI**
  - Jetpack Compose
  - Material3

- **Architecture**
  - MVVM
  - Multi-Module (core / feature)

- **주요 라이브러리**
  - Navigation Compose
  - ViewModel
  - StateFlow
  - Coroutines
  - Hilt
  - Retrofit2
  - Coil

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
    ├── login/                    # 로그인, 회원가입, 휴대폰 인증
    ├── onboarding/                # 온보딩
    ├── home/                      # 메인 화면, 루틴/소모품 관리
    ├── shopping/                  # 쇼핑, 상품 검색, 장바구니, 결제
    └── mypage/                    # 마이페이지, 주문내역, 정기구독
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

| 화면 이름 | 스크린 ID | 진입 경로 | 담당자 |
|-----------|-----------|-----------|--------|
| 스플래시 화면 | SplashScreen | 앱 실행 | 김지아 |
| 로그인 화면 | LoginScreen | 앱 최초 진입 | 김지아 |
| 회원가입 화면 | SignUpScreen | 로그인 → 회원가입 | 김지아 |
| 휴대폰 인증 화면 | PhoneVerificationScreen | 회원가입 | 김지아 |
| 코치마크 화면 | CoachMarkScreen | 최초 회원가입 완료 후 | 최종희 |
| 메인 화면 | MainScreen | 로그인/코치마크 완료 | 강수아 |
| 루틴 추가 화면 | RoutineCreateScreen | 메인, 캘린더 | 강수아 |
| 루틴 수정 화면 | RoutineEditScreen | 캘린더 | 강수아 |
| 소모품 추가 화면 | ItemCreateScreen | 메인, 캘린더 | 강수아 |
| 소모품 수정 화면 | ItemEditScreen | 캘린더 | 강수아 |
| 캘린더 화면 | CalendarScreen | 하단 네비게이션 | 이동진 |
| 날짜 상세 BottomSheet | CalendarBottomSheet | 캘린더 날짜 선택 | 이동진 |
| 쇼핑 메인 화면 | ShoppingScreen | 하단 네비게이션 | 임은지 |
| 상품 검색 화면 | SearchScreen | 쇼핑 → 검색 | 임은지 |
| 카테고리 상품 목록 | CategoryScreen | 쇼핑 → 카테고리 | 임은지 |
| 상품 상세 화면 | ProductDetailScreen | 상품 선택 | 임은지 |
| 장바구니 화면 | CartScreen | 쇼핑 | 임은지 |
| 결제 화면 | PaymentScreen | 장바구니 / 바로구매 | 임은지 |
| 결제 완료 화면 | PaymentCompleteScreen | 결제 완료 | 임은지 |
| 마이페이지 | MyPageScreen | 하단 네비게이션 | 정규은 |
| 주문내역 화면 | OrderHistoryScreen | 마이페이지 | 정규은 |
| 정기구독 관리 | SubscriptionScreen | 마이페이지 | 정규은 |
| 찜 목록 | WishlistScreen | 마이페이지 | 정규은 |
| 결제수단 관리 | PaymentMethodScreen | 마이페이지 | 정규은 |
| 알림함 | NotificationScreen | 상단 알림 버튼 | 최종희 |
| 알림 설정 | NotificationSettingScreen | 알림함 | 최종희 |

---

# 화면 이동 플로우

```text
앱 실행
│
▼
SplashScreen
│
▼
저장된 로그인 여부 확인
├───────────────┐
│               │
로그인 O        로그인 X
│               │
▼               ▼
코치마크 완료?   LoginScreen
   │               │
┌──┴──────┐        │
│         │        │
YES       NO       │
│         │        │
▼         ▼        │
Main   CoachMark   │
│         │        │
└─────┬───┘        │
      ▼            │
MainScreen   ◀─────┘


==============================
Main Navigation
==============================
```

```text
MainScreen
├── CalendarScreen
├── ShoppingScreen
└── MyPageScreen
```

==============================
로그인
==============================

```text
LoginScreen
├── 일반 로그인
├── 카카오 로그인
├── 구글 로그인
└── 회원가입
│
▼
SignUpScreen
│
▼
PhoneVerificationScreen
│
▼
CoachMarkScreen
│
▼
MainScreen
```

==============================
메인
==============================

```text
MainScreen
├── 루틴 추가
├── 소모품 추가
├── NotificationScreen
└── CalendarScreen
```

==============================
캘린더
==============================

```text
CalendarScreen
├── 날짜 선택
│      │
│      ▼
│ CalendarBottomSheet
│      ├── 루틴 수정
│      └── 소모품 수정
│
├── 루틴 추가
└── 소모품 추가
```

==============================
쇼핑
==============================

```text
ShoppingScreen
├── SearchScreen
├── CategoryScreen
├── ProductDetailScreen
│          │
│          ├── CartScreen
│          │      │
│          │      ▼
│          │ PaymentScreen
│          │      │
│          │      ▼
│          │ PaymentCompleteScreen
│          │
│          └── 바로구매
│                 │
│                 ▼
│            PaymentScreen
│
└── 장바구니
```

==============================
마이페이지
==============================

```text
MyPageScreen
├── OrderHistoryScreen
├── SubscriptionScreen
├── WishlistScreen
└── PaymentMethodScreen
```

==============================
알림
==============================

```text
NotificationScreen
└── NotificationSettingScreen
```

---

## Copyright

© 2026 LiRouti Team. All rights reserved.

This project and its source code are proprietary and confidential.
Unauthorized copying, modification, distribution, or use of this software is strictly prohibited.
