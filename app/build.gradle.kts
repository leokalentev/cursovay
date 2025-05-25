plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.freefair.lombok") version "8.6"
	id("org.sonarqube") version "6.0.1.5171"
	id("io.sentry.jvm.gradle") version "5.6.0"
	checkstyle
	application
}

application {
	mainClass.set("hexlet.code.AppApplication")
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	implementation("org.projectlombok:lombok:1.18.32")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	annotationProcessor("org.projectlombok:lombok:1.18.32")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("net.datafaker:datafaker:2.0.1")
	implementation("org.instancio:instancio-junit:3.3.0")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")
	implementation ("org.springframework.boot:spring-boot-starter-logging")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:8.12.0")
	implementation("io.sentry:sentry-logback:8.12.0")
	implementation("org.json:json:20250107")
	implementation("io.github.cdimascio:dotenv-java:3.0.0")

}

tasks.withType<Test> {
	useJUnitPlatform()
	maxParallelForks = 1
	forkEvery = 0
}

sonar {
	properties {
		property("sonar.projectKey", "leokalentev_java-project-99")
		property("sonar.organization", "leokalentev")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}




