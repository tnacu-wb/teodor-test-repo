package uk.co.whitbread.codingrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;

public class WhitbreadRules {
    @ArchTest
    static final ArchTests CODING_RULES = ArchTests.in(CodingConventionRules.class);
    @ArchTest
    static final ArchTests ARCHITECTURE_RULES = ArchTests.in(HexagonalPackagesStructureRules.class);
    @ArchTest
    static final ArchTests NAMING_CONVENTION_RULES = ArchTests.in(NamingConventionRules.class);
}
