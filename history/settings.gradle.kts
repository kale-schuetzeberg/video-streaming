rootProject.name = "history"

plugins {
    id("com.gradle.enterprise") version("3.16.2")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}