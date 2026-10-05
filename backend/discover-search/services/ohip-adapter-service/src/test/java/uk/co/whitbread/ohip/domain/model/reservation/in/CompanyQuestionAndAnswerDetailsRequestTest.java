/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.ohip.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;

class CompanyQuestionAndAnswerDetailsRequestTest extends BaseValidation {

  @Test
  void companyQuestionAndAnswerDetailsRequestDto_EmptyRequest_Validate() {
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequest = CompanyQuestionAndAnswerDetailsRequestDto.builder()
        .build();
    Set<ConstraintViolation<CompanyQuestionAndAnswerDetailsRequestDto>> violations = ValidatorFactory.getValidator()
        .validate(companyQuestionAndAnswerDetailsRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  void companyQuestionAndAnswerDetailsRequestDto_Request_EmptyReservationIdsValidate() {
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequest = CompanyQuestionAndAnswerDetailsRequestDto.builder()
        .reservationIds(new HashSet<>()).build();
    Set<ConstraintViolation<CompanyQuestionAndAnswerDetailsRequestDto>> violations = ValidatorFactory.getValidator()
        .validate(companyQuestionAndAnswerDetailsRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  void companyQuestionAndAnswerDetailsRequestDto_Request_NoHotelIdValidate() {
    Set<String> reservationIds = Set.of("RES-123");
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequest = CompanyQuestionAndAnswerDetailsRequestDto.builder()
        .reservationIds(reservationIds).build();
    Set<ConstraintViolation<CompanyQuestionAndAnswerDetailsRequestDto>> violations = ValidatorFactory.getValidator()
        .validate(companyQuestionAndAnswerDetailsRequest);
    assertFalse(violations.isEmpty());
  }

  @Test
  void companyQuestionAndAnswerDetailsRequestDto_Request_Validate() {
    Set<String> reservationIds = Set.of("RES-123");
    String hotelId = "Hotel_Id";
    CompanyQuestionAndAnswerDetailsDto companyQuestionAndAnswerDetailsDto = new CompanyQuestionAndAnswerDetailsDto();
    CompanyQuestionAndAnswerDetailsRequestDto companyQuestionAndAnswerDetailsRequest =
        CompanyQuestionAndAnswerDetailsRequestDto.builder().reservationIds(reservationIds)
            .hotelId(hotelId).companyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsDto)
            .build();
    Set<ConstraintViolation<CompanyQuestionAndAnswerDetailsRequestDto>> violations = ValidatorFactory.getValidator()
        .validate(companyQuestionAndAnswerDetailsRequest);
    assertTrue(violations.isEmpty());
  }

  @Test
  void constructor_empty_shouldSelfValidateAndThrow() {
    final String[] possibleExpectedErrors = new String[]{OhipConstants.RESERVATION_ID_ERROR,
        OhipConstants.HOTEL_ID_ERROR, OhipConstants.COMPANY_QNA_DETAILS_ERROR};
    checkErrorThrownContainsMessage(() -> CompanyQuestionAndAnswerDetailsRequest.builder().build(),
        possibleExpectedErrors);
  }

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    final String hotelIdError = OhipConstants.HOTEL_ID_ERROR;
    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();
    Set<String> reservationIds = Set.of("RES-123");
    checkErrorThrownContainsMessage(
        () -> CompanyQuestionAndAnswerDetailsRequest.builder().reservationIds(reservationIds)
            .companyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails)
            .build(), hotelIdError);
  }

  @Test
  void constructor_emptyReservationIds_shouldSelfValidateAndThrow() {
    final String reservationIdError = OhipConstants.RESERVATION_ID_ERROR;
    CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails = new CompanyQuestionAndAnswerDetails();
    checkErrorThrownContainsMessage(
        () -> CompanyQuestionAndAnswerDetailsRequest.builder().hotelId("TEST")
            .companyQuestionAndAnswerDetails(companyQuestionAndAnswerDetails).build(),
        reservationIdError);
  }


}
