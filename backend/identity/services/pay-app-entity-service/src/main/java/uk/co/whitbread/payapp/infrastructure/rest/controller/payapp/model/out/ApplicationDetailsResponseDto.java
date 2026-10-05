package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import java.util.List;
import lombok.Builder;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;
import uk.co.whitbread.payapp.domain.model.out.ApplicationParticipant;
import uk.co.whitbread.payapp.domain.model.out.CardDetails;
import uk.co.whitbread.payapp.domain.model.out.CardHolder;
import uk.co.whitbread.payapp.domain.model.out.CompanyDetailsData;
import uk.co.whitbread.payapp.domain.model.out.ContactDetails;

@Builder
public record ApplicationDetailsResponseDto(
    String applicationId,
    String applicationNumber,
    String applicationGuid,
    Integer companyId,
    String startedDate,
    String scheme,
    String updateDate,
    String accountName,
    String status,
    String resumeUrl,
    String submittedDate,
    String activatedDate,
    String campaignCode,
    int incentiveId,
    String incentiveCode,
    String termsAndConditionsAccepted,
    String registrationQuestion,
    String registrationAnswer,
    List<ApplicationParticipant> participants,
    List<CardHolder> cardHolders,
    String created,
    String modified,
    ContactDetails contactDetails,
    CompanyDetailsData companyDetails,
    List<CardDetails> cardDetails,
    String hostedPageGuid,
    DirectDebitOption directDebitOption) {

}