plugins {
    kotlin("jvm") version "1.6.20"
    application
}

group = "me.thunderbiscuit"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // hocon
    implementation("com.typesafe:config:1.4.2")

    // lightningdevkit
    implementation("org.lightningdevkit:ldk-java:0.0.106.0")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("me.thunderbiscuit.kldk.MainKt")
}
