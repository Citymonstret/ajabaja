import com.hierynomus.gradle.license.LicenseBasePlugin
import com.hierynomus.gradle.license.tasks.LicenseCheck
import net.kyori.indra.IndraExtension
import net.ltgt.gradle.errorprone.ErrorPronePlugin
import net.ltgt.gradle.errorprone.errorprone
import nl.javadude.gradle.plugins.license.LicenseExtension
import org.gradle.api.plugins.JavaPlugin.*

plugins {
    val indraVersion = "1.2.1"
    id("net.kyori.indra") version indraVersion apply false
    id("net.kyori.indra.checkstyle") version indraVersion apply false
    id("net.kyori.indra.publishing.sonatype") version indraVersion apply false
    id("com.github.hierynomus.license") version "0.15.0" apply false
    id("com.github.johnrengelman.shadow") version "6.1.0" apply false
    id("net.ltgt.errorprone") version "1.3.0" apply false
    id("com.github.ben-manes.versions") version "0.36.0"
}

group = "org.incendo"
version = "1.0-SNAPSHOT"
description = "Content filtering system for Minecraft"

repositories {
    mavenCentral()
}

subprojects {
    plugins.apply("net.kyori.indra")
    plugins.apply("net.kyori.indra.checkstyle")
    plugins.apply("net.kyori.indra.publishing.sonatype")
    apply<ErrorPronePlugin>()
    apply<LicenseBasePlugin>()

    extensions.configure(LicenseExtension::class) {
        header = rootProject.file("HEADER")
        mapping("java", "DOUBLESLASH_STYLE")
        mapping("kt", "DOUBLESLASH_STYLE")
        includes(listOf("**/*.java", "**/*.kt"))
    }

    extensions.configure(IndraExtension::class) {
        github("Incendo", "ajabaja") {
            ci = true
        }
        gpl3OnlyLicense()

        javaVersions {
            testWith(8, 11, 15)
        }
        checkstyle.set("8.39")

        configurePublications {
            pom {
                developers {
                    developer {
                        id.set("Sauilitired")
                        name.set("Alexander Söderberg")
                        url.set("https://alexander-soderberg.com")
                        email.set("contact@alexander-soderberg.com")
                    }
                }
            }
        }
    }

    /* Disable checkstyle on tests */
    project.gradle.startParameter.excludedTaskNames.add("checkstyleTest")

    tasks {
        withType(JavaCompile::class) {
            options.errorprone {
                /* These are just annoying */
                disable(
                    "JdkObsolete",
                    "FutureReturnValueIgnored",
                    "ImmutableEnumChecker",
                    "StringSplitter",
                    "EqualsGetClass",
                    "CatchAndPrintStackTrace"
                )
            }
            options.compilerArgs.addAll(listOf("-Xlint:-processing", "-Werror"))
        }

        named("check") {
            dependsOn(withType(LicenseCheck::class))
        }
    }

    dependencies {
        COMPILE_ONLY_API_CONFIGURATION_NAME("org.checkerframework", "checker-qual", "3.9.1")
        TEST_IMPLEMENTATION_CONFIGURATION_NAME("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")
        "errorprone"("com.google.errorprone", "error_prone_core", "2.5.1")
        COMPILE_ONLY_API_CONFIGURATION_NAME("com.google.errorprone", "error_prone_annotations", "2.5.1")
    }
}
