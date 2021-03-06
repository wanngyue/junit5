description = "${rootProject.description} (Bill of Materials)"

apply(from = "$rootDir/gradle/publishing.gradle.kts")

dependencies {
	constraints {
		val mavenizedProjects: List<Project> by rootProject.extra
		mavenizedProjects.sorted()
				.filter { it.name != "junit-platform-console-standalone" }
				.forEach { api("${it.group}:${it.name}:${it.version}") }
	}
}

the<PublishingExtension>().publications.named<MavenPublication>("maven") {
	from(components["javaLibraryPlatform"])
	pom {
		description.set("This Bill of Materials POM can be used to ease dependency management " +
				"when referencing multiple JUnit artifacts using Gradle or Maven.")
	}
}

tasks.withType<GenerateMavenPom> {
	doLast {
		val xml = destination.readText()
		require(xml.indexOf("<dependencies>") == xml.lastIndexOf("<dependencies>")) {
			"BOM must contain exactly one <dependencies> element but contained multiple:\n$destination"
		}
		require(xml.contains("<dependencyManagement>")) {
			"BOM must contain a <dependencyManagement> element:\n$destination"
		}
	}
}
