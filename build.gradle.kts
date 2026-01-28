plugins {
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
}

group = "com.realworld"
version = "0.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        optIn.add("kotlin.RequiresOptIn")
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.6")
    }
}
    
dependencies {
    // spring boot starter
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    // embedded mongodb
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring3x:4.21.0")
    implementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.21.0")

    // h2
    implementation("io.r2dbc:r2dbc-h2:1.1.0.RELEASE")
    implementation("com.h2database:h2:2.4.240")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:1.14.6")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
//    testImplementation(kotlin("test"))
//    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.3.0-Beta2") // Or a compatible stable version
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.3.0-Beta2") // Or a compatible stable version
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kover {
    reports {
        filters {
            excludes {
                classes("com.realworld.spring.webflux.SpringWebfluxKtApplicationKt",
                    "*\$suspendImpl\$\$inlined\$awaitBody\$1",
                    "*\$suspendImpl\$\$inlined\$map\$*"
                )
            }
        }
    }
}

