plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    project(":ajabaja-core")
    compileOnly("org.bukkit", "bukkit", "1.8-R0.1-SNAPSHOT")
}
