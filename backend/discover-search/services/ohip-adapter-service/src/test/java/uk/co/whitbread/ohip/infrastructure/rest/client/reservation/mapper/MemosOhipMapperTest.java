package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_FORMAT_DATE_RESERVATION_EXCEPTION;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MemosOhipMapperImpl.class)
@Slf4j
class MemosOhipMapperTest {


  private static final String COMMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:SS";
  @Autowired
  MemosOhipMapper memosOhipMapper;

  @Test
  void createChangeReservationRequest_ShouldReturnOk() {
    //Arrange
    var createMemoRequest = mockCreateMemoRequest();

    //Act
    var changeReservation =
        memosOhipMapper.toDto("reservationId1", createMemoRequest);

    //Assert
    assertEquals(1, changeReservation.getReservations().get(0).getComments().size());
    assertEquals("This is a memo",
        changeReservation.getReservations().get(0).getComments().get(0).getComment().getText().getValue());
  }

  @Test
  void createMemosResponse_ShouldReturnOk() {
    //Arrange
    var reservationsWithComments = Arrays.asList(
        mockReservationWithComments("reservationId1",
            new ImmutablePair<>(OhipConstants.AGENT_NOTES_COMMENT_TITLE, OhipConstants.AGENT_NOTES_COMMENT_TITLE),
            new ImmutablePair<>("11", "12"),
            new ImmutablePair<>("This is the first memo", "This is the second memo"),
            new ImmutablePair<>(getDateFromString("2023-07-07 12:00:00"), getDateFromString("2023-07-07 12:00:00")),
            new ImmutablePair<>("creatorId1", "modifierId1")),
        mockReservationWithComments("reservationId2",
            new ImmutablePair<>(OhipConstants.AGENT_NOTES_COMMENT_TITLE, OhipConstants.AGENT_NOTES_COMMENT_TITLE),
            new ImmutablePair<>("21", "22"),
            new ImmutablePair<>("This is the first memo", "This is the second memo"),
            new ImmutablePair<>(getDateFromString("2023-07-06 12:00:00"), getDateFromString("2023-07-08 12:00:00")),
            new ImmutablePair<>("creatorId2", "modifierId2"))
    );

    //Act
    var memosResponse = memosOhipMapper.toModel(reservationsWithComments);

    //Assert
    assertEquals(2, memosResponse.getMemos().size());

    assertEquals("This is the second memo", memosResponse.getMemos().get(0).getDescription());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getCreatedOn());
    assertEquals("creatorId1", memosResponse.getMemos().get(0).getCreatedBy());
    assertEquals("2023-07-08 12:00:00", memosResponse.getMemos().get(0).getModifiedOn());
    assertEquals("modifierId2", memosResponse.getMemos().get(0).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(0).getMemoType().equalsIgnoreCase("AGENT"));

    assertEquals("This is the first memo", memosResponse.getMemos().get(1).getDescription());
    assertEquals("2023-07-06 12:00:00", memosResponse.getMemos().get(1).getCreatedOn());
    assertEquals("creatorId2", memosResponse.getMemos().get(1).getCreatedBy());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(1).getModifiedOn());
    assertEquals("modifierId1", memosResponse.getMemos().get(1).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(0).getMemoType().equalsIgnoreCase("AGENT"));
  }

  @Test
  void getMemosResponse_memoType_agent_ShouldReturnOk() {
    //Arrange
    var reservationsWithComments = Arrays.asList(
        mockReservationWithComments("reservationId",
            OhipConstants.AGENT_NOTES_COMMENT_TITLE, "11", "This is an agent memo",
            getDateFromString("2023-07-07 12:00:00"), "creatorId")
    );

    //Act
    var memosResponse = memosOhipMapper.toModel(reservationsWithComments);

    //Assert
    assertEquals(1, memosResponse.getMemos().size());

    assertEquals("This is an agent memo", memosResponse.getMemos().get(0).getDescription());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getCreatedOn());
    assertEquals("creatorId", memosResponse.getMemos().get(0).getCreatedBy());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getModifiedOn());
    assertEquals("creatorId", memosResponse.getMemos().get(0).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(0).getMemoType().equalsIgnoreCase("AGENT"));
  }

  @Test
  void getMemosResponse_memoType_system_ShouldReturnOk() {
    //Arrange
    var reservationsWithComments = Arrays.asList(
        mockReservationWithComments("reservationId1",
            OhipConstants.BUSINESS_NOTES_COMMENT_TITLE, "11", "This is system memo 1",
            getDateFromString("2023-07-07 12:00:00"), "creatorId1"),
        mockReservationWithComments("reservationId2",
            OhipConstants.SPECIAL_NOTES_COMMENT_TITLE, "22", "This is system memo 2",
            getDateFromString("2023-07-07 12:00:00"), "creatorId2")
    );

    //Act
    var memosResponse = memosOhipMapper.toModel(reservationsWithComments);

    //Assert
    assertEquals(2, memosResponse.getMemos().size());

    assertEquals("This is system memo 1", memosResponse.getMemos().get(0).getDescription());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getCreatedOn());
    assertEquals("creatorId1", memosResponse.getMemos().get(0).getCreatedBy());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getModifiedOn());
    assertEquals("creatorId1", memosResponse.getMemos().get(0).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(0).getMemoType().equalsIgnoreCase("SYSTEM"));

    assertEquals("This is system memo 2", memosResponse.getMemos().get(1).getDescription());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(1).getCreatedOn());
    assertEquals("creatorId2", memosResponse.getMemos().get(1).getCreatedBy());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(1).getModifiedOn());
    assertEquals("creatorId2", memosResponse.getMemos().get(1).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(1).getMemoType().equalsIgnoreCase("SYSTEM"));
  }

  @Test
  void getMemosResponse_memoType_opera_ShouldReturnOk() {
    //Arrange
    var reservationsWithComments = Arrays.asList(
        mockReservationWithComments("reservationId",
            "CUSTOM OPERA TITLE", "11", "This is an opera memo",
            getDateFromString("2023-07-07 12:00:00"), "creatorId")
    );

    //Act
    var memosResponse = memosOhipMapper.toModel(reservationsWithComments);

    //Assert
    assertEquals(1, memosResponse.getMemos().size());

    assertEquals("This is an opera memo", memosResponse.getMemos().get(0).getDescription());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getCreatedOn());
    assertEquals("creatorId", memosResponse.getMemos().get(0).getCreatedBy());
    assertEquals("2023-07-07 12:00:00", memosResponse.getMemos().get(0).getModifiedOn());
    assertEquals("creatorId", memosResponse.getMemos().get(0).getModifiedBy());
    assertTrue(memosResponse.getMemos().get(0).getMemoType().equalsIgnoreCase("OPERA"));
  }

  private CreateMemoRequest mockCreateMemoRequest() {
    return CreateMemoRequest.builder().hotelId("hotelId").description("This is a memo").reservationIds(List.of(
        "reservationId1", "reservationId2")).build();
  }

  private Reservation mockReservationWithComments(String reservationId, Pair<String, String> memoTitles,
      Pair<String, String> memoIds, Pair<String, String> memoTexts,
      Pair<Date, Date> createModifyDates, Pair<String, String> createModifyUsers) {
    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId(reservationId);

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));

    var comments = new ArrayList<CommentInfoType>();
    comments.add(buildCommentType(memoIds.getLeft(), memoTexts.getLeft(), memoTitles.getLeft(),
        createModifyDates.getLeft(), createModifyUsers.getLeft(),
        createModifyDates.getLeft(), createModifyUsers.getRight()));
    comments.add(buildCommentType(memoIds.getRight(), memoTexts.getRight(), memoTitles.getRight(),
        createModifyDates.getRight(), createModifyUsers.getLeft(),
        createModifyDates.getRight(), createModifyUsers.getRight()));

    hotelReservationType.setComments(comments);

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private Reservation mockReservationWithComments(String reservationId, String memoTitle,
      String memoId, String memoText, Date createModifyDate, String createModifyUser) {
    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId(reservationId);

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));

    var comments = List.of(buildCommentType(memoId, memoText, memoTitle,
        createModifyDate, createModifyUser,
        createModifyDate, createModifyUser));

    hotelReservationType.setComments(comments);

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private Date getDateFromString(String dateAsString) {
    try {
      return new SimpleDateFormat(COMMENT_DATE_PATTERN).parse(dateAsString);
    } catch (ParseException e) {
      var exception = new HotelReservationException(DIGITAL_FORMAT_DATE_RESERVATION_EXCEPTION,
              String.format("Could not parse date from %s", dateAsString));
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private CommentInfoType buildCommentType(String id, String text, String commentTitle, Date createdDate,
      String createdBy,
      Date lastUpdatedDate, String lastUpdatedBy) {
    var commentInfoType = new CommentInfoType();
    commentInfoType.setId(id);

    final var commentType = new CommentType();
    commentType.setCommentTitle(commentTitle);
    commentType.setType(OhipConstants.RESERVATION_COMMENT_TYPE);
    commentType.setNotificationLocation(OhipConstants.RESERVATION_NOTIFICATION_AREA);

    final var comment = new FormattedTextTextType();
    comment.setValue(text);

    commentType.setText(comment);
    commentType.setCreateDateTime(createdDate);
    commentType.setCreatorId(createdBy);
    commentType.setLastModifyDateTime(lastUpdatedDate);
    commentType.setLastModifierId(lastUpdatedBy);

    commentInfoType.setComment(commentType);

    return commentInfoType;
  }

}
