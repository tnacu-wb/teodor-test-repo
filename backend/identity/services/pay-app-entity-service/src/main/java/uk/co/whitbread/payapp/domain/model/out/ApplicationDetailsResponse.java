package uk.co.whitbread.payapp.domain.model.out;

import java.util.List;
import lombok.Builder;
import uk.co.whitbread.payapp.domain.model.in.DirectDebitOption;

@Builder
public record ApplicationDetailsResponse(
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
