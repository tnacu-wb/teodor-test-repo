package uk.co.whitbread.codingrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameContaining;
import static com.tngtech.archunit.core.domain.properties.HasName.Predicates.nameEndingWith;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class NamingConventionRules {
    @ArchTest
    public static final ArchRule CONTROLLERS_SHOULD_BE_SUFFIXED = classes()
            .that().resideInAPackage("..controller")
            .or().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    public static final ArchRule CLASSES_NAMED_CONTROLLER_SHOULD_BE_IN_A_CONTROLLER_PACKAGE =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .should().resideInAPackage("..controller..");

    @ArchTest
    public static final ArchRule INFRA_MODEL_CLASSES_SHOULD_HAVE_DTO_SUFFIX = classes().that()
            .resideInAnyPackage("..infrastructure..model.in")
            .or().resideInAnyPackage("..infrastructure..model.out")
            .should().haveSimpleNameEndingWith("Dto")
            .orShould().haveSimpleNameEndingWith("DtoBuilder")
            .orShould().haveSimpleNameEndingWith("DtoBuilderImpl")
            .orShould().beEnums()
            .because("Model classes from infra package should have Dto suffix, except for enums and lombok generated builders");

    @ArchTest
    public static final ArchRule NO_DTO_CLASSES_OUTSIDE_INFRA_MODEL_PACKAGES = classes().that()
            .haveNameMatching(".*Dto")
            .should().resideInAPackage("..infrastructure..model..")
            .orShould().resideInAPackage("..generated.models..");

    @ArchTest
    public static final ArchRule DOMAIN_MODEL_CLASSES_SHOULD_NOT_HAVE_SUFFIXES = noClasses().that()
            .resideInAnyPackage("..domain.model..")
            .should().haveSimpleNameEndingWith("Dto")
            .orShould().haveSimpleNameEndingWith("Entity");

    @ArchTest
    public static final ArchRule PORT_INTERFACES_SUFFIX = classes().that()
            .resideInAnyPackage("..domain.ports..")
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Port");

    @ArchTest
    public static final ArchRule SECONDARY_PORT_IMPLEMENTATION_SUFFIX = classes().that()
            .resideInAnyPackage("..infrastructure..")
            .and().implement(nameEndingWith("Port"))
            .should().haveSimpleNameEndingWith("PortImpl");

    @ArchTest
    public static final ArchRule MAPPER_METHOD_PATTERN = methods().that()
            .areDeclaredInClassesThat(resideInAnyPackage("..infrastructure..mapper..").and(simpleNameContaining("Mapper")))
            .and().arePublic()
            .should()
            .haveNameMatching("to.*Dto")
            .orShould().haveNameMatching("to.*Model");
}
