plugins {
    `java-library`
    groovy
    id("io.freefair.lombok")
    `maven-publish`
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    internal(enforcedPlatform(project(":platform")))
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    api(project(":users-api"))
    api("org.springframework:spring-webflux")
    api("com.auth0:java-jwt")
    api("org.springframework.security:spring-security-web")
    api("org.springframework.security:spring-security-config")
    api("com.google.guava:guava")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.codehaus.groovy:groovy-all")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.hamcrest:hamcrest-core")
    testImplementation("org.springframework:spring-context")
    testImplementation("org.springframework:spring-test")
    testRuntimeOnly("net.bytebuddy:byte-buddy")
    testRuntimeOnly("org.objenesis:objenesis")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "bintray"
            val bintrayUsername = "twinklehawk"
            val bintrayRepoName = "maven"
            val bintrayPackageName = "net.plshark.users"
            url = uri("https://api.bintray.com/maven/$bintrayUsername/$bintrayRepoName/$bintrayPackageName/;publish=1")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_API_KEY")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionResult()
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
