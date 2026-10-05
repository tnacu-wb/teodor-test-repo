package uk.co.whitbread.basket.infrastructure.rest.controller.basket.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidCompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidCompanyQuestionAndAnswerDetailsValidator;

@ExtendWith(MockitoExtension.class)
class ValidCompanyQuestionAndAnswerDetailsValidatorTest {

    @Mock
    private ValidCompanyQuestionAndAnswerDetails mockValidCompanyQuestionAndAnswerDetails;

    private ValidCompanyQuestionAndAnswerDetailsValidator validator;

    @Mock
    ConstraintValidatorContext context;

    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void setUp() throws Exception {
        validator = new ValidCompanyQuestionAndAnswerDetailsValidator();
        validator.initialize(mockValidCompanyQuestionAndAnswerDetails);
    }

    @Test
    void shouldCompanyQuestionAndAnswerDetailsDtoInCorrectFormat() {
        //Arrange
        CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto =
            CompanyQuestionAndAnswerDetailsDto.builder()
            .purchaseOrderQuestionAndAnswer(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                "This is PO. - 0123456789").build())
            .customerReferenceQuestionAndAnswer(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                "This is reference. - 012").build())
            .userDefinedQuestionAndAnswers(
                List.of(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                    "This is UD / À-Ö Ø ø ÿ 0123456789 .,;:&()_?!~#/").build())
            ).build();


        //Act
        boolean valid = validator.isValid(companyQuestionAndAnswerDetailsDto, context);

        //Assert
        assertThat(valid, is(true));
    }

    @Test
    void shouldCompanyQuestionAndAnswerDetailsDtoInNotCorrectFormat() throws Exception {
        //Arrange
        when(context.buildConstraintViolationWithTemplate(anyString()))
            .thenReturn(builder);

        CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto =
            CompanyQuestionAndAnswerDetailsDto.builder()
                .purchaseOrderQuestionAndAnswer(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                    "This is PO. - 0123456789 ****").build())
                .customerReferenceQuestionAndAnswer(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                    "This is reference. - 012 ^^^^").build())
                .userDefinedQuestionAndAnswers(
                    List.of(CompanyQuestionAndAnswerDto.builder().question("What is this").answer(
                        "This is UD À-Ö Ø ø ÿ 0123456789 .,;:&()_?!~# {{{}}} ^^^^").build())
                ).build();


        //Act
        boolean valid = validator.isValid(companyQuestionAndAnswerDetailsDto, context);

        //Assert
        assertThat(valid, is(false));
    }

}