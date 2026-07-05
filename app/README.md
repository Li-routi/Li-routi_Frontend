# Li-routi Frontend

> 집안일 루틴과 소모품을 관리하고, 필요한 상품까지 연결해주는 Android 애플리케이션

---

# 프로젝트 소개

Li-routi는 사용자의 집안일 루틴과 소모품 사용 주기를 관리하고,
필요한 시점에 쇼핑 기능과 연계하여 효율적인 생활을 돕는 Android 애플리케이션입니다.

---

# 팀원 소개 및 역할

| 이름      | 담당 |
|---------|------|
| 레몬/김지아  | 스플래시, 로그인, 회원가입, 휴대폰 인증 |
| 우가/강수아  | 메인 화면, 루틴 관리, 소모품 관리 |
| 동진/이동진  | 캘린더 |
| 묵은지/임은지 | 쇼핑, 상품 검색, 장바구니, 결제 |
| 제로/정규은  | 마이페이지, 주문내역, 정기구독, 결제수단 |
| 종이/최종희  | 알림, 코치마크 |

---

# 기술 스택

### Language

- Kotlin

### UI

- Jetpack Compose
- Material3

### Architecture

- MVVM

### Libraries

- Navigation Compose
- ViewModel
- StateFlow
- Coroutines
- Hilt
- Retrofit2
- Coil

---

# 📂 프로젝트 구조

```
app
 ├── data
 │    ├── datasource
 │    ├── model
 │    ├── repository
 │
 ├── domain
 │    ├── model
 │    ├── repository
 │    └── usecase
 │
 ├── presentation
 │    ├── login
 │    ├── home
 │    ├── calendar
 │    ├── shopping
 │    ├── mypage
 │    ├── notification
 │    └── common
 │
 ├── navigation
 │
 ├── di
 │
 └── util
```

---

# 컨벤션

## 브랜치 네이밍

| 종류 | 예시 |
|------|------|
| Feature | feature/login |
| Fix | fix/login-error |
| Refactor | refactor/home-ui |
| Design | design/main-screen |
| Docs | docs/readme |

---

## 커밋 메시지

형식

```
타입: 내용
```

예시

```
feat: 로그인 화면 구현
fix: 로그인 오류 수정
docs: README 수정
refactor: HomeViewModel 리팩토링
design: 버튼 UI 수정
```

### Commit Type

| 타입 | 설명 |
|------|------|
| feat | 새로운 기능 추가 |
| fix | 버그 수정 |
| hotfix | 긴급 버그 수정 |
| refactor | 리팩토링 |
| perf | 성능 개선 |
| style | 코드 스타일 수정 |
| design | UI 수정 |
| docs | 문서 수정 |
| comment | 주석 수정 |
| test | 테스트 코드 |
| build | 빌드 설정 |
| ci | CI 설정 |
| chore | 기타 작업 |
| rename | 파일명 변경 |
| remove | 파일 삭제 |
| revert | 이전 커밋 되돌리기 |

---

## Pull Request 규칙

- PR 제목은 작업 내용을 간단히 작성한다.
- 최소 1명 이상의 리뷰를 받은 후 Merge한다.
- Merge 전 Conflict를 해결한다.
- Build 오류가 없는 상태에서 Merge한다.
- 하나의 PR은 하나의 기능 구현을 원칙으로 한다.

---

## 코드 네이밍 규칙

### Class

- PascalCase

```
LoginViewModel
HomeScreen
ShoppingRepository
```

### Function

- camelCase

```
login()

loadRoutine()
```

### Variable

- camelCase

```
userName

routineList
```

### Constant

- UPPER_SNAKE_CASE

```
MAX_COUNT

DEFAULT_DELAY
```

### Package

- 소문자 사용

```
presentation.login

presentation.home
```

---

## 패키지 구조 규칙

```
presentation
 ├── login
 ├── home
 ├── calendar
 ├── shopping
 ├── mypage
 ├── notification
 └── common
```

각 화면(Screen)별로 패키지를 분리하며,
ViewModel, UI, Component 등을 동일한 패키지 내에서 관리한다.

---

# ▶ 빌드 및 실행 방법

1. Android Studio에서 프로젝트를 Open한다.
2. Gradle Sync를 진행한다.
3. 실행 기기(에뮬레이터 또는 실제 기기)를 선택한다.
4. Run 버튼을 눌러 실행한다.

---

# 화면 목록

| 화면 이름 | 스크린 ID | 진입 경로 | 담당자 |
|-----------|-----------|-----------|--------|
| 스플래시 화면 | SplashScreen | 앱 실행 | 레몬/김지아 |
| 로그인 화면 | LoginScreen | 앱 최초 진입 | 레몬/김지아 |
| 회원가입 화면 | SignUpScreen | 로그인 → 회원가입 | 레몬/김지아 |
| 휴대폰 인증 화면 | PhoneVerificationScreen | 회원가입 | 레몬/김지아 |
| 코치마크 화면 | CoachMarkScreen | 최초 회원가입 완료 후 | 종이/최종희 |
| 메인 화면 | MainScreen | 로그인/코치마크 완료 | 우가/강수아 |
| 루틴 추가 화면 | RoutineCreateScreen | 메인, 캘린더 | 우가/강수아 |
| 루틴 수정 화면 | RoutineEditScreen | 캘린더 | 우가/강수아 |
| 소모품 추가 화면 | ItemCreateScreen | 메인, 캘린더 | 우가/강수아 |
| 소모품 수정 화면 | ItemEditScreen | 캘린더 | 우가/강수아 |
| 캘린더 화면 | CalendarScreen | 하단 네비게이션 | 동진/이동진 |
| 날짜 상세 BottomSheet | CalendarBottomSheet | 캘린더 날짜 선택 | 동진/이동진 |
| 쇼핑 메인 화면 | ShoppingScreen | 하단 네비게이션 | 묵은지/임은지 |
| 상품 검색 화면 | SearchScreen | 쇼핑 → 검색 | 묵은지/임은지 |
| 카테고리 상품 목록 | CategoryScreen | 쇼핑 → 카테고리 | 묵은지/임은지 |
| 상품 상세 화면 | ProductDetailScreen | 상품 선택 | 묵은지/임은지 |
| 장바구니 화면 | CartScreen | 쇼핑 | 묵은지/임은지 |
| 결제 화면 | PaymentScreen | 장바구니 / 바로구매 | 묵은지/임은지 |
| 결제 완료 화면 | PaymentCompleteScreen | 결제 완료 | 묵은지/임은지 |
| 마이페이지 | MyPageScreen | 하단 네비게이션 | 제로/정규은 |
| 주문내역 화면 | OrderHistoryScreen | 마이페이지 | 제로/정규은 |
| 정기구독 관리 | SubscriptionScreen | 마이페이지 | 제로/정규은 |
| 찜 목록 | WishlistScreen | 마이페이지 | 제로/정규은 |
| 결제수단 관리 | PaymentMethodScreen | 마이페이지 | 제로/정규은 |
| 알림함 | NotificationScreen | 상단 알림 버튼 | 종이/최종희 |
| 알림 설정 | NotificationSettingScreen | 알림함 | 종이/최종희 |

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
```text
MainScreen
├── CalendarScreen
├── ShoppingScreen
└── MyPageScreen


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


==============================
메인
==============================
```text
MainScreen
├── 루틴 추가
├── 소모품 추가
├── NotificationScreen
└── CalendarScreen


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


==============================
마이페이지
==============================
```text
MyPageScreen
├── OrderHistoryScreen
├── SubscriptionScreen
├── WishlistScreen
└── PaymentMethodScreen


==============================
알림
==============================
```text
NotificationScreen
└── NotificationSettingScreen

```