import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "ro.jf.playground"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

sourceSets {
    create("integration") {
        kotlin.srcDir("$projectDir/src/integration/kotlin")
        resources.srcDir("$projectDir/src/integration/resources")
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

configurations {
    named("integrationImplementation") {
        extendsFrom(configurations.getByName("testImplementation"))
    }
    named("integrationRuntimeOnly") {
        extendsFrom(configurations.getByName("testRuntimeOnly"))
    }

}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }
    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    register("bootRunLocal") {
        group = "application"
        description = "Runs the service with the local profile"
        doFirst {
            bootRun.configure {
                systemProperty("spring.profiles.active", "local")
            }
        }
        finalizedBy("bootRun")
    }
    withType<Test> {
        useJUnitPlatform()
    }
    val integrationTest by registering(Test::class) {
        testClassesDirs = sourceSets["integration"].output.classesDirs
        classpath = sourceSets["integration"].runtimeClasspath
        group = "verification"
    }
    val check by getting {
        dependsOn(integrationTest)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:1.11.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    testImplementation("com.github.tomakehurst:wiremock:3.0.0-beta-2")
}
