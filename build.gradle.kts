plugins {
	kotlin("jvm") version "2.2.20"
	id("com.gradleup.shadow") version "8.3.0"
	id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "rip.shuka"
version = providers.gradleProperty("version").getOrElse("1.0")
val paperMinecraftVersion = providers.gradleProperty("paperMinecraftVersion").getOrElse("1.21.11")

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/") {
		name = "papermc-repo"
	}
}

dependencies {
	compileOnly("io.papermc.paper:paper-api:${paperMinecraftVersion}-R0.1-SNAPSHOT")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
	runServer {
		// Configure the Minecraft version for our task.
		// This is the only required configuration besides applying the plugin.
		// Your plugin's jar (or shadowJar if present) will be used automatically.
		minecraftVersion(paperMinecraftVersion)
	}

	register("printPaperMinecraftVersion") {
		doLast {
			println(paperMinecraftVersion)
		}
	}
}

val targetJavaVersion = 21
kotlin {
	jvmToolchain(targetJavaVersion)
}

tasks.build {
	dependsOn("shadowJar")
}

tasks.processResources {
	val props = mapOf("version" to version)
	inputs.properties(props)
	filteringCharset = "UTF-8"
	filesMatching("plugin.yml") {
		expand(props)
	}
}
