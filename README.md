# LibChestGUI

Lightweight chest GUI library for Spigot.

**LibChestGUI is a library for developers. It is not a standalone plugin. It does not have a configuration file, nor can you open create and open chest menus with commands. If you have these needs, LibChestGUI might not be what you're looking for.**

# Use

LibChestGUI is not a plugin - it is a library. You need to include it in your project as a dependency. You can do this by adding the following to your `pom.xml` file.


```xml
<!--Add this to your repositories-->
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<!--Add this to your dependencies-->
<dependency>
    <groupId>com.github.kuromesama6</groupId>
    <artifactId>LibChestGUI</artifactId>
    <version>1.0.0</version>
</dependency>
```

Or `build.gradle.kts` if you are using Gradle:

```kotlin
// Add this to your repositories
maven("https://jitpack.io")

// Add this to your dependencies
dependencies {
    implementation("com.github.kuromesama6:LibChestGUI:1.0.0")
}
```

## Compatibility

LibChestGUI is compatible with Java 8 and above, and Spigot 1.8.8 and above.