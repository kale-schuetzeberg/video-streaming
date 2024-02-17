plugins {
    java
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.baddog"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.2")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.withType<Test> {
    useJUnitPlatform()
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
