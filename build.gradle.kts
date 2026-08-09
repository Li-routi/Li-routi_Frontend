// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}

// Krossbow(채팅 STOMP 클라이언트)가 프로젝트 Kotlin 컴파일러(2.2.10)보다 새 kotlin-stdlib를 전이 의존성으로
// 끌어와 전체 모듈이 깨지는 것을 막기 위해, 프로젝트가 쓰는 Kotlin 버전으로 stdlib를 강제 고정한다.
subprojects {
    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}",
                "org.jetbrains.kotlin:kotlin-stdlib-common:${libs.versions.kotlin.get()}",
            )
        }
    }
}