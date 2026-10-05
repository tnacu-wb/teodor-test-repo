package uk.co.whitbread.kiosk;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import uk.co.whitbread.codingrules.WhitbreadRules;

/**
 * This class enables Whitbread architectural and coding convention verification at build time
 */
@AnalyzeClasses(packages = "uk.co.whitbread.kiosk", importOptions = {ImportOption.DoNotIncludeJars.class, ImportOption.DoNotIncludeTests.class})
public class ArchUnitTests {
    @ArchTest
    static final ArchTests RULES = ArchTests.in(WhitbreadRules.class);
} 