package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class SpecialRequestsRemoveCommentsMapper {

  public static final String RESERVATION_VALUE = "Reservation";

  @Mapping(expression = "java(injectReservationIds(reservationId))",
      target = "reservationIdList")
  abstract HotelReservationInstructionType removeCommentfromDto(String hotelId,
      String reservationId,
      List<String> commentId);

  @AfterMapping
  void updateSpecialComments(List<String> commentId,
      @MappingTarget HotelReservationInstructionType hotelReservationInstructionType) {

    if (!CollectionUtils.isEmpty(commentId)) {
      hotelReservationInstructionType.setComments(removeSpecialComments(commentId));
    }

  }

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

  protected List<CommentInfoType> removeSpecialComments(List<String> commentId) {

    List<CommentInfoType> comments = new ArrayList<>();

    commentId.forEach(note -> {
      if (StringUtils.isNotBlank(note)) {
        final var commentInfoType = new CommentInfoType();

        commentInfoType.setId(note);
        commentInfoType.setType("Comment");

        comments.add(commentInfoType);
      }
    });

    return comments;
  }
}
