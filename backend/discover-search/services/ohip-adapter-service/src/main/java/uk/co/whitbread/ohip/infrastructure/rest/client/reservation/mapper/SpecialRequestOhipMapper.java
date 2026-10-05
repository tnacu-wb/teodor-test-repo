package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SPECIALS_DESCRIPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SPECIALS_TYPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.AnnotateWith;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@AnnotateWith(
    value = Primary.class
)
public abstract class SpecialRequestOhipMapper {

  public static final String RESERVATION_VALUE = "Reservation";

  @Mapping(target = "preferenceCollection", ignore = true)
  @Mapping(expression = "java(injectReservationIds(reservationId))",
      target = "reservationIdList")
  @Mapping(target = "comments", ignore = true)
  abstract HotelReservationInstructionType fromDto(String hotelId,
      String reservationId,
      List<String> specialRequests,
      List<String> bookingNotes);

  @AfterMapping
  void updatePreferenceCollection(List<String> specialRequests,
      @MappingTarget HotelReservationInstructionType hotelReservationInstructionType) {

    if (!CollectionUtils.isEmpty(specialRequests)) {
      hotelReservationInstructionType.setPreferenceCollection(createPreferenceTypeCollection(specialRequests));
    }

  }

  @AfterMapping
  void updateSpecialComments(List<String> bookingNotes,
      @MappingTarget HotelReservationInstructionType hotelReservationInstructionType) {

    if (!CollectionUtils.isEmpty(bookingNotes)) {
      hotelReservationInstructionType.setComments(createSpecialComments(bookingNotes));
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

  private List<PreferenceTypeType> createPreferenceTypeCollection(List<String> specialRequests) {

    final var preferenceTypeType = new PreferenceTypeType();
    preferenceTypeType.setPreference(createPreferences(specialRequests));
    preferenceTypeType.setPreferenceType(SPECIALS_TYPE);
    preferenceTypeType.setPreferenceTypeDescription(SPECIALS_DESCRIPTION);
    return Collections.singletonList(preferenceTypeType);
  }

  private List<PreferenceType> createPreferences(List<String> specialRequest) {

    return specialRequest.stream()
        .filter(Objects::nonNull)
        .map(this::createPreferenceType)
        .toList();
  }

  private PreferenceType createPreferenceType(String specialRequest) {

    final var preference = new PreferenceType();
    preference.setPreferenceValue(specialRequest);
    return preference;
  }

  protected List<CommentInfoType> createSpecialComments(List<String> bookingNotes) {

    List<CommentInfoType> comments = new ArrayList<>();

    bookingNotes.forEach(note -> {
      if (StringUtils.isNotBlank(note)) {
        final var commentInfoType = new CommentInfoType();

        final var commentType = new CommentType();
        commentType.setCommentTitle(OhipConstants.SPECIAL_NOTES_COMMENT_TITLE);
        commentType.setType(OhipConstants.RESERVATION_COMMENT_TYPE);
        commentType.setNotificationLocation(OhipConstants.GENERAL_LOCATION_AREA);

        final var comment = new FormattedTextTextType();
        comment.setValue(note);

        commentType.setText(comment);
        commentInfoType.setComment(commentType);

        comments.add(commentInfoType);
      }
    });

    return comments;
  }
}
