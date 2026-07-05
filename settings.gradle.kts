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
include(":feature:onboarding")
include(":feature:home")
include(":feature:shopping")
include(":feature:mypage")
