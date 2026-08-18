import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import xyz.jpenilla.runpaper.task.RunServer

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

val java25Launcher = javaToolchains.launcherFor {
	languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
	runServer {
		// Configure the Minecraft version for our task.
		// This is the only required configuration besides applying the plugin.
		// Your plugin's jar (or shadowJar if present) will be used automatically.
		minecraftVersion(paperMinecraftVersion)
		runDirectory(layout.projectDirectory.dir("run/$paperMinecraftVersion").asFile)
	}

	fun registerPaperTest(name: String, minecraftVersion: String) {
		register<RunServer>(name) {
			group = "paper"
			description = "Builds and starts a test server with Paper $minecraftVersion."
			minecraftVersion(minecraftVersion)
			runDirectory(layout.projectDirectory.dir("run/$minecraftVersion").asFile)
			pluginJars(project.tasks.named("shadowJar"))
			dependsOn("shadowJar")
			if (minecraftVersion.startsWith("26.")) {
				javaLauncher.set(java25Launcher)
			}
		}
	}

	registerPaperTest("runPaper261", "26.1.2")
	registerPaperTest("runPaper262", "26.2")

	register("printPaperMinecraftVersion") {
		doLast {
			println(paperMinecraftVersion)
		}
	}
}

val targetJavaVersion = 21
val buildJavaVersion = 25
kotlin {
	jvmToolchain(buildJavaVersion)
	compilerOptions {
		jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(targetJavaVersion)
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
