import com.github.breadmoirai.GithubReleaseExtension
import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options
import org.gradle.jvm.tasks.Jar
import org.apache.tools.ant.filters.ReplaceTokens
import org.ajoberstar.grgit.Grgit

plugins {
    `java`
    kotlin("jvm") version "1.3.30"
    `maven-publish`
    id("fabric-loom") version "0.2.3-SNAPSHOT"
    id("com.matthewprenger.cursegradle") version "1.1.2"
    id("com.github.breadmoirai.github-release") version "2.2.4"
    id("org.ajoberstar.grgit") version "3.0.0"
}

val BUILD_NO: String? by project
val buildNo = BUILD_NO ?: "SNAPSHOT"

val project_group: String by project
val project_version: String by project
val project_name: String by project
val version_mc: String by project
val version_mc_jar: String by project
val version_mc_mappings: String by project
val version_fabric_lib: String by project
val version_fabric_events_lifecycle: String by project
val version_fabric_loader: String by project
val version_fabric_kotlin: String by project
val version_kotlin: String by project
val version_h3nt_commonutils: String by project
val curse_api_key: String? by project
val project_curseforge_id: String by project

val changelog: String? by project
val github_release_token: String? by project
val maven_url: String? by project

group = "$project_group"
version = "$project_version-$buildNo"

base {
	archivesBaseName = "$project_name"
}

tasks.compileJava   {
  sourceCompatibility = "1.8"
  targetCompatibility = "1.8"
}

repositories {
    maven(url="http://mribecky.com.ar/maven") {
        name = "Hea3veN"
    }
    maven(url = "https://kotlin.bintray.com/kotlinx") {
        name = "Kotlin X"
    }
}

val shadow by configurations.creating {
	isTransitive = false
}

dependencies {
	minecraft("com.mojang:minecraft:$version_mc_jar")
	mappings("net.fabricmc:yarn:$version_mc_mappings")
	modCompile("net.fabricmc:fabric-loader:$version_fabric_loader")

	//modCompile("net.fabricmc:fabric:$version_fabric")
	modCompile("net.fabricmc.fabric-api:fabric-lib:$version_fabric_lib")
	modCompile("net.fabricmc.fabric-api:fabric-events-lifecycle:$version_fabric_events_lifecycle")

    modCompile("net.fabricmc:fabric-language-kotlin:$version_fabric_kotlin")
    compileOnly("net.fabricmc:fabric-language-kotlin:$version_fabric_kotlin")
    /* compileOnly("org.jetbrains.kotlin:kotlin-stdlib:$version_kotlin") */

    modCompile("com.hea3ven.tools.commonutils:h3nt-commonutils:$version_h3nt_commonutils")
    // shadow("com.hea3ven.tools.commonutils:h3nt-commonutils:$version_h3nt_commonutils")

    api("org.hamcrest:hamcrest:2.1")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    testCompile("junit:junit:4.12")
    testCompile("org.mockito:mockito-core:2.8.9")
}

tasks.processResources {
    filesMatching("**/*.json") {
        filter<ReplaceTokens>("tokens" to mapOf(
                "version" to project.version.toString(),
                "version_fabric_lib" to "$version_fabric_lib".toString(),
                "version_fabric_events_lifecycle" to "$version_fabric_events_lifecycle".toString(),
                "version_kotlin" to "$version_kotlin".toString(),
                "version_fabric_kotlin" to "$version_fabric_kotlin".toString()
        ))
    }
}

//tasks.withType<Jar> {
//    from(configurations["shadow"].asFileTree.files.filter { !it.name.contains("fabric.mod.json") }.map { zipTree(it) })
//}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("apiJar") {
    from(sourceSets["main"].output) {
        include("com/hea3ven/unstainer/api/**")
    }
    archiveClassifier.set("api")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "unstainer"
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["apiJar"])
            pom {
                name.set("Unstainer")
                withXml {
                    val pom = asNode()
                    val deps = pom.get("dependencies") as groovy.util.NodeList
                    deps.forEach { pom.remove(it as groovy.util.Node) }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri(maven_url ?: "file:///tmp/mvn")
        }
    }
}

val versionRegex = Regex("v[0-9].*")
val versionExcludeRegex = Regex("v1.(9(.4)?|10)-.*")
var repo = Grgit.open()
val lastVersionTag = repo.tag.list()
        .map { it.fullName.substring(10) }
        .filterNot(versionExcludeRegex::matches)
        .filter(versionRegex::matches)
        .sortedWith({a, b -> -(a.split('.').zip(b.split('.')).map {  it.first.compareTo(it.second) }.filter { it != 0 }.first() ?: 0) })
        .first()
val changes = repo.log {
    range(lastVersionTag, "HEAD")
}.filter {
    it.parentIds.size > 1
}.map {
    it.fullMessage.removeRange(0, it.fullMessage.indexOf('\n')).trim()
}.joinToString("\r\n")

curseforge {
    apiKey = curse_api_key ?: ""

    options(closureOf<Options> {
        forgeGradleIntegration = false
    })

    project(closureOf<CurseProject> {
        id = project_curseforge_id
        releaseType = "release"
        changelog = changelog ?: ""
        addGameVersion(version_mc)
		relations(closureOf<CurseRelation> {
			requiredDependency("fabric")
			requiredDependency("fabric-language-kotlin")
		})
    })
}

configure<GithubReleaseExtension> {
    token(github_release_token ?: "")
    owner.set("Hea3veN")
    repo.set("Unstainer")
    targetCommitish.set("master")
    body.set(changes)
    draft.set(false)
    prerelease.set(false)
    releaseAssets.setFrom(tasks.jar.get().outputs.files)
}

