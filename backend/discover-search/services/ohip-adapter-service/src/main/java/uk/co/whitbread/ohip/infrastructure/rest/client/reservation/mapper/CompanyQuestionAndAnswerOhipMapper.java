package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_PURCHASE_ORDER_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.StringUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.QuestionAndAnswerTypeEnum;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CompanyQuestionAndAnswerOhipMapper {

  public static final String RESERVATION_VALUE = "Reservation";
  public static final String PIPE_DELIM = "|";

  @Mapping(source = "companyQuestionAndAnswerDetailsRequest.companyQuestionAndAnswerDetails"
      + ".customerReferenceQuestionAndAnswer.answer", target = "customReference")
  @Mapping(expression = "java(injectReservationIds(reservationId))",
      target = "reservationIdList")
  @Mapping(expression = "java(injectCompanyQuestionAndAnswer(hotelId, companyQuestionAndAnswerDetailsRequest))",
      target = "comments")
  @Mapping(expression = "java(injectUserDefinedFields(companyQuestionAndAnswerDetailsRequest"
      + ".getCompanyQuestionAndAnswerDetails().getPurchaseOrderQuestionAndAnswer()))", target = "userDefinedFields")
  abstract HotelReservationInstructionType fromDto(
      String hotelId,
      String reservationId,
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest);


  @Named("injectReservationIds")
  protected List<UniqueIDType> injectReservationIds(
      String reservationId) {
    return Collections.singletonList(buildUniqueIdType(reservationId));
  }

  private UniqueIDType buildUniqueIdType(String reservationId) {
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setType(RESERVATION_VALUE);
    uniqueIdType.setId(reservationId);
    return uniqueIdType;
  }

  @Named("injectCompanyQuestionAndAnswer")
  protected List<CommentInfoType> injectCompanyQuestionAndAnswer(String hotelId, CompanyQuestionAndAnswerDetailsRequest
      companyQuestionAndAnswerDetailsRequest) {
    List<CommentInfoType> commentInfoTypes = new ArrayList<>();

    if (companyQuestionAndAnswerDetailsRequest != null
        && companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails() != null) {

      final CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails =
          companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails();

      populateCommentInfoType(hotelId, companyQuestionAndAnswerDetails.getCustomerReferenceQuestionAndAnswer(),
          QuestionAndAnswerTypeEnum.CUST_REF_QNA.name(), commentInfoTypes);

      populateCommentInfoType(hotelId, companyQuestionAndAnswerDetails.getPurchaseOrderQuestionAndAnswer(),
          QuestionAndAnswerTypeEnum.PUR_ORD_QNA.name(), commentInfoTypes);

      final List<CompanyQuestionAndAnswer> userDefinedQuestionAndAnswers =
          companyQuestionAndAnswerDetails.getUserDefinedQuestionAndAnswers();

      if (userDefinedQuestionAndAnswers != null) {
        for (CompanyQuestionAndAnswer userDefinedQuestionAndAnswer : userDefinedQuestionAndAnswers) {
          populateCommentInfoType(hotelId, userDefinedQuestionAndAnswer,
              QuestionAndAnswerTypeEnum.USR_DEF_QNA.name(), commentInfoTypes);
        }
      }
    }

    return commentInfoTypes;
  }

  private void populateCommentInfoType(String hotelId, final CompanyQuestionAndAnswer companyQuestionAndAnswer,
      final String commentType, final List<CommentInfoType> commentInfoTypes) {
    if (companyQuestionAndAnswer != null && commentType != null) {
      CommentType comment = new CommentType();

      // Better null handling than List.of()
      var questionAndHeader = new ArrayList<>();
      questionAndHeader.add(companyQuestionAndAnswer.getQuestion());
      questionAndHeader.add(companyQuestionAndAnswer.getQuestionHeader());
      comment.setCommentTitle(StringUtils.collectionToDelimitedString(questionAndHeader, PIPE_DELIM));

      FormattedTextTextType formattedTextTextType = new FormattedTextTextType();
      formattedTextTextType.setValue(companyQuestionAndAnswer.getAnswer());
      comment.setText(formattedTextTextType);
      comment.setType(commentType);
      comment.setHotelId(hotelId);

      CommentInfoType commentInfoType = new CommentInfoType();
      commentInfoType.setComment(comment);

      commentInfoTypes.add(commentInfoType);
    }
  }

  @Named("injectUserDefinedFields")
  protected UserDefinedFieldsType injectUserDefinedFields(
      CompanyQuestionAndAnswer purchaseOrderQuestionAndAnswer) {
    UserDefinedFieldsType userDefinedFieldsType = new UserDefinedFieldsType();

    if (purchaseOrderQuestionAndAnswer != null
        && purchaseOrderQuestionAndAnswer.getAnswer() != null) {
      CharacterUDFType purchaseOrderType = new CharacterUDFType();
      purchaseOrderType.name(UDFC_PURCHASE_ORDER_NAME);
      purchaseOrderType.value(purchaseOrderQuestionAndAnswer.getAnswer());

      userDefinedFieldsType.addCharacterUDFsItem(purchaseOrderType);

      return userDefinedFieldsType;
    } else {
      return null;
    }
  }

}
