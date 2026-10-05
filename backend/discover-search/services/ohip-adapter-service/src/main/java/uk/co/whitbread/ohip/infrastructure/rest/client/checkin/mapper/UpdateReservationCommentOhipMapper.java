package uk.co.whitbread.ohip.infrastructure.rest.client.checkin.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_NOTIFICATION_AREA;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentReservationIdList;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentText;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskChangeReservation;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskComment;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskComments;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskReservation;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class UpdateReservationCommentOhipMapper {

  @Mapping(expression = "java(injectReservationComments(reservationId, hotelId, commentDetails))",
      target = "reservations")
  public abstract KioskChangeReservation toModel(String reservationId, String hotelId,
      CommentDetails commentDetails);

  protected List<KioskReservation> injectReservationComments(String reservationId,
      String hotelId, CommentDetails commentDetails) {
    final var hotelReservation = new KioskReservation();
    final var commentsInfoType = new KioskComments();
    final var commentInfoType = new KioskComment();
    final var commentText = new CommentText();
    final var reservationIdList = new CommentReservationIdList();

    reservationIdList.setId(reservationId);
    reservationIdList.setType("Reservation");

    commentInfoType.setCommentTitle(commentDetails.getCommentTitle());
    commentInfoType.setNotificationLocation(RESERVATION_NOTIFICATION_AREA);
    commentInfoType.setType(commentDetails.getType());
    commentInfoType.setInternal(false);
    commentText.setValue(commentDetails.getTextValue());
    commentInfoType.setText(commentText);

    commentsInfoType.setComment(commentInfoType);

    hotelReservation.setReservationIdList(Collections.singletonList(reservationIdList));
    hotelReservation.setComments(Collections.singletonList(commentsInfoType));
    hotelReservation.setHotelId(hotelId);
    return List.of(hotelReservation);
  }


}
