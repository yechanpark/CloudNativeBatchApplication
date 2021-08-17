import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("maven-publish")
	id("java")
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	mavenLocal()
}

apply(plugin="io.spring.dependency-management")
apply(plugin="maven-publish")

extra["springCloudVersion"] = "2020.0.3"

dependencies {

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")

	// https://mvnrepository.com/artifact/mysql/mysql-connector-java
	implementation("mysql:mysql-connector-java")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-aws
	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")

	// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-aop
	implementation("org.springframework.boot:spring-boot-starter-aop:2.5.3")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-config
	implementation("org.springframework.cloud:spring-cloud-starter-config")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-bootstrap
	implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-eureka
	//implementation("org.springframework.cloud:spring-cloud-starter-eureka:1.4.7.RELEASE")

	// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-netflix-eureka-client
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
publishing {
	publications {
		create<MavenPublication>("bootJava") {
			artifact(tasks.getByName("bootJar"))
		}
	}
}
/*

publishing {
	publications {
		create<MavenPublication>("java") {
			// Applies the component for the release build variant.
			from(components["java"])
			// You can then customize attributes of the publication as shown below.
			groupId = "com.example"
			artifactId = "CloudNativeBatchApplication"
			version = "0.0.1-SNAPSHOT"
		}
	}
}*/
