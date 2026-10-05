package uk.co.whitbread.codingrules;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.slf4j.Logger;

import java.util.Optional;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

public class CodingConventionRules {

    @ArchTest
    public static final ArchRule NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
    @ArchTest
    public static final ArchRule NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;
    @ArchTest
    public static final ArchRule NO_CLASSES_SHOULD_USE_JODA_TIME = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;
    @ArchTest
    public static final ArchRule NO_CLASSES_SHOULD_USE_FIELD_INJECTION = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;
    @ArchTest
    public static final ArchRule NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
    @ArchTest
    public static final ArchRule DECLARED_LOGGERS_SHOULD_BE_PRIVATE_STATIC_FINAL = fields().that()
            .haveRawType(Logger.class)
            .should().bePrivate()
            .andShould().beStatic()
            .andShould().beFinal()
            .because("it is a common idiom");
    @ArchTest
    public static final ArchRule JAVA16_COLLECTORS_SHOULD_BE_USED = noClasses()
            .should().callMethod("java.util.stream.Collectors", "toList")
            .because("since Java 16 there is a better variant to produce a list directly from a stream: Stream.toList()");
    @ArchTest
    public static final ArchRule OPTIONAL_NOT_ALLOWED_IN_FIELDS = noFields()
            .should().haveRawType(Optional.class)
            .because("Optional is intended for use as a method return type where there is a clear need to represent \"no result\"");
    @ArchTest
    public static final ArchRule OPTIONAL_NOT_ALLOWED_IN_METHOD_PARAMETERS = noMethods()
            .should().haveRawParameterTypes(Optional.class)
            .because("Optional is intended for use as a method return type where there is a clear need to represent \"no result\"");

}