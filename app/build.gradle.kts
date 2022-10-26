plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.thunderbiscuit"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")
    
    // hocon
    implementation("com.typesafe:config:1.4.2")

    // clikt
    implementation("com.github.ajalt.clikt:clikt:3.4.0")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta6")

    // ktor
    implementation("io.ktor:ktor-client-core:2.0.1")
    implementation("io.ktor:ktor-client-cio:2.0.1")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // lightningdevkit
    implementation("org.lightningdevkit:ldk-java:0.0.110.2")

    // bitcoindevkit
    implementation("org.bitcoindevkit:bdk-jvm:0.10.0")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("me.thunderbiscuit.kldk.MainKt")
    applicationName = "kldk"
}

distributions {
    main {
        distributionBaseName.set("kldk")
    }
}
