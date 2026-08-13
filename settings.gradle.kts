pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
        // 포트원 안드로이드 SDK는 JitPack으로 배포됨.
        // JitPack은 누구나 올릴 수 있어서 다른 라이브러리가 여기로 새지 않게 그룹을 묶어둠
        maven("https://jitpack.io") {
            content { includeGroup("com.github.portone-io") }
        }
    }
}

rootProject.name = "Li-routi-Frontend"
include(":app")

include(":core:common:kotlin")
include(":core:common:android")
include(":core:common:ui")
include(":core:domain")
include(":core:data")
include(":core:design-system")

include(":feature:login")
include(":feature:home")
include(":feature:mypage")
include(":feature:challenge")
include(":feature:grouproutine")
