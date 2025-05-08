plugins {
    java
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.baddog"
version = "0.0.1-SNAPSHOT"
description = "AWS Simple Storage Service"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.5")

    // AWS Simple Storage Service
    implementation(platform("software.amazon.awssdk:bom:2.20.56"))
    implementation("software.amazon.awssdk:s3")
    implementation("software.amazon.awssdk:sso")
    implementation("software.amazon.awssdk:ssooidc")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.777")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    // Dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("getDependencies") {
    from(sourceSets.main.get().runtimeClasspath)
    into("runtime/")

    doFirst {
        val runtimeDir = File("runtime")
        runtimeDir.deleteRecursively()
        runtimeDir.mkdir()
    }

    doLast {
        File("runtime").deleteRecursively()
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        indentWithSpaces()
        importOrder()
        googleJavaFormat()
        removeUnusedImports()
    }
    kotlin {
        ktlint()
    }
    kotlinGradle {
        ktlint()
    }
}
