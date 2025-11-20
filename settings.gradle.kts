pluginManagement {
    repositories {
        gradlePluginPortal() // This should be recognized here
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "spring-webflux-mongodb-r2dbc"
