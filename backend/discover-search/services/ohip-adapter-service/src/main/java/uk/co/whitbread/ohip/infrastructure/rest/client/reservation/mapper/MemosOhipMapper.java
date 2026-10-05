package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_FORMAT_DATE_RESERVATION_EXCEPTION;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.Memo;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemoId;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.DateUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.MemoUtils;


@Mapper(componentModel = "spring")
@Slf4j
public abstract class MemosOhipMapper {

  private static final String COMMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:SS";

  @Mapping(expression = "java(injectReservationComments(reservationId, createMemoRequest))", target = "reservations")
  public abstract ChangeReservation toDto(String reservationId,
      CreateMemoRequest createMemoRequest);

  protected List<HotelReservationInstructionType> injectReservationComments(String reservationId,
      CreateMemoRequest createMemoRequest) {
    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setReservationIdList(getReservationIds(reservationId));
    hotelReservationInstructionType.setComments(getComments(createMemoRequest));

    return List.of(hotelReservationInstructionType);
  }

  private List<UniqueIDType> getReservationIds(String reservationId) {
    final var uniqueIdType = new UniqueIDType();

    uniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    uniqueIdType.setId(reservationId);

    return List.of(uniqueIdType);
  }

  private List<CommentInfoType> getComments(CreateMemoRequest createMemoRequest) {
    final List<CommentInfoType> comments = new ArrayList<>();

    final var commentInfoType = new CommentInfoType();

    final var commentType = new CommentType();
    commentType.setCommentTitle(OhipConstants.AGENT_NOTES_COMMENT_TITLE);
    commentType.setType(OhipConstants.RESERVATION_COMMENT_TYPE);
    commentType.setNotificationLocation(OhipConstants.RESERVATION_NOTIFICATION_AREA);

    final var comment = new FormattedTextTextType();
    comment.setValue(createMemoRequest.getDescription());

    commentType.setText(comment);
    commentInfoType.setComment(commentType);

    comments.add(commentInfoType);

    return comments;
  }

  @Named("toModel")
  public MemosResponse toModel(List<Reservation> reservationsWithComments) {
    final var commentsByReservationId = new HashMap<String, List<CommentInfoType>>();

    reservationsWithComments.forEach(reservation -> {
      List<CommentInfoType> reservationComments = reservation.getReservations().getReservation()
          .get(0).getComments();

      if (reservationComments != null && !reservationComments.isEmpty()) {
        reservation.getReservations().getReservation().get(0).getReservationIdList().stream()
            .filter(uniqueIDType -> uniqueIDType.getType()
                .equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
            .findFirst()
            .map(UniqueIDType::getId)
            .ifPresent(
                reservationId -> commentsByReservationId.put(reservationId, reservationComments));
      }
    });

    final var memosByTextAndType = new HashMap<Pair<String, String>, Memo>();

    commentsByReservationId.forEach((reservationId, comments) -> comments.forEach(comment -> {
      var memoKey = new ImmutablePair<>(comment.getComment().getText().getValue(),
          MemoUtils.getMemoType(comment.getComment().getCommentTitle()));
      if (memosByTextAndType.containsKey(memoKey)) {
        Memo memo = memosByTextAndType.get(memoKey);
        MemoId memoId =
            memo.getIds().stream().filter(id -> id.getReservationId().equals(reservationId))
                .findFirst().orElse(null);
        if (memoId == null) {
          var ids = new ArrayList<String>();
          ids.add(comment.getId());
          memo.getIds().add(new MemoId(reservationId, ids));
        } else {
          memoId.getMemoIds().add(comment.getId());
        }
        // update first creation info
        if (comment.getComment().getCreateDateTime()
            .before(DateUtils.getDateFromString(memo.getCreatedOn(), COMMENT_DATE_PATTERN))) {
          memo.setCreatedOn(getDateAsString(comment.getComment().getCreateDateTime()));
          memo.setCreatedBy(comment.getComment().getCreatorId());
        }
        // update last modified by
        if (comment.getComment().getLastModifyDateTime()
            .after(DateUtils.getDateFromString(memo.getModifiedOn(), COMMENT_DATE_PATTERN))) {
          memo.setModifiedOn(getDateAsString(comment.getComment().getLastModifyDateTime()));
          memo.setModifiedBy(comment.getComment().getLastModifierId());
        }
      } else {
        var ids = new ArrayList<String>();
        ids.add(comment.getId());
        var memoId = new MemoId(reservationId, ids);
        var memoIds = new ArrayList<MemoId>();
        memoIds.add(memoId);
        Memo memo = Memo.builder()
            .ids(memoIds)
            .description(memoKey.getLeft())
            .createdOn(getDateAsString(comment.getComment().getCreateDateTime()))
            .createdBy(comment.getComment().getCreatorId())
            .modifiedOn(getDateAsString(comment.getComment().getLastModifyDateTime()))
            .modifiedBy(comment.getComment().getLastModifierId())
            .memoType(memoKey.getRight())
            .build();
        memosByTextAndType.put(memoKey, memo);
      }
    }));

    return new MemosResponse(memosByTextAndType.values().stream()
        .sorted((m1, m2) -> DateUtils.getDateFromString(m2.getModifiedOn(), COMMENT_DATE_PATTERN).compareTo(
            DateUtils.getDateFromString(m1.getModifiedOn(), COMMENT_DATE_PATTERN)))
        .toList());
  }

  private String getDateAsString(Date date) {
    return DateFormatUtils.format(date, COMMENT_DATE_PATTERN);
  }
}
