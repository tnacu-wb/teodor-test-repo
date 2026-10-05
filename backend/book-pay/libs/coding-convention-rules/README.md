# Whitbread architecture and coding tests

This project contains [ArchUnit](https://www.archunit.org/) rules that can be used to ensure that common coding
conventions and a consistent project structure is maintained across all Whitbread microservices.

## Requirements

- **Java 25** or later
- **ArchUnit 1.3.0**

## How to use

To use the set of rules, defined in this project, in another project it must be added as a test dependency:

```xml
<dependency>
    <groupId>uk.co.whitbread</groupId>
    <artifactId>coding-convention-rules</artifactId>
    <version>${latest.version}</version>
    <scope>test</scope>
</dependency>
```

and a test class must be created to enable the running of the coding/architectural conventions rules during the build.
Example (all classes in package 'uk.co.whitbread'):

```java
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import uk.co.whitbread.codingrules.WhitbreadRules;

/**
 * This class enables Whitbread architectural and coding convention verification at build time
 */
@AnalyzeClasses(packages = "uk.co.whitbread", importOptions = {ImportOption.DoNotIncludeJars.class, ImportOption.DoNotIncludeTests.class})
public class ArchUnitTests {
    @ArchTest
    static final ArchTests RULES = ArchTests.in(WhitbreadRules.class);
}
```

## Migration guide (1.x → 2.0.0)

Version 2.0.0 contains **breaking changes**:

- **Java 25** is now the minimum required version (previously Java 17).
- **ArchUnit** has been upgraded from 0.23.1 to **1.3.0**. If your project depends on ArchUnit directly, ensure you align to the same major version to avoid conflicts.
