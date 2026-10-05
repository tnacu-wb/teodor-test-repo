package uk.co.whitbread.codingrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

public class HexagonalPackagesStructureRules {
    @ArchTest
    public static final ArchRule DOMAIN_IS_NOT_DEPENDENT_ON_PORT_IMPLEMENTATIONS = noClasses().that()
            .resideInAnyPackage("..domain..")
            .should().accessClassesThat()
            .resideInAnyPackage("..infrastructure..");

    @ArchTest
    public static final ArchRule SECONDARY_PORT_IMPL_ARE_ONLY_DEPENDENT_ON_DOMAIN_INTERFACES_AND_MODEL = classes()
            .that(resideInAnyPackage("..infrastructure..").and(simpleNameEndingWith("PortImpl")))
            .should()
            .onlyAccessClassesThat(resideInAnyPackage("..domain.model..")
                    .or(resideInAnyPackage("..domain.exception.."))
                    .or(resideInAnyPackage("..domain.ports.secondary..").and(simpleNameEndingWith("Port")))
                    .or(resideOutsideOfPackage("..domain.."))
            );

    @ArchTest
    public static final ArchRule HEXAGONAL_LAYERS = layeredArchitecture()
            .consideringAllDependencies()
            .layer("domain-model").definedBy("..domain..model..")
            .layer("infra-model").definedBy("..infrastructure..model..")
            .layer("primary-ports").definedBy("..domain..ports.primary")
            .layer("primary-ports-impl").definedBy("..domain..logic..")
            .layer("secondary-ports").definedBy("..domain..ports.secondary")
            .layer("secondary-ports-impl").definedBy("..infrastructure..")
            .layer("infra-config").definedBy("..infrastructure..config")

            .whereLayer("secondary-ports-impl").mayOnlyBeAccessedByLayers("infra-config")
            .whereLayer("primary-ports-impl").mayOnlyBeAccessedByLayers("infra-config")
            .whereLayer("secondary-ports").mayOnlyBeAccessedByLayers("secondary-ports-impl", "primary-ports-impl")
            .whereLayer("primary-ports").mayOnlyBeAccessedByLayers("secondary-ports-impl", "primary-ports-impl", "infra-config")
            .whereLayer("domain-model").mayOnlyBeAccessedByLayers("secondary-ports", "secondary-ports-impl", "primary-ports", "primary-ports-impl", "infra-config")
            .whereLayer("infra-model").mayOnlyBeAccessedByLayers("secondary-ports-impl");
}
