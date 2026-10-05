package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.out.ActualTimeSpan;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionControl;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionStatus;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRange;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeParent;
import uk.co.whitbread.ohip.domain.model.availability.out.RestrictionsByDateRangeResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RestrictionsByDateRangeMapperImpl.class)
class RestrictionsByDateRangeMapperTest {

  @Autowired
  private RestrictionsByDateRangeMapper restrictionsByDateRangeMapper;

  @Test
  void toDto_WhenConvertingDomainModelToDto_ThenAllFieldsAreMapped() {
    var source = RestrictionsByDateRangeResult.builder()
        .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
            .restrictionsByDateRange(RestrictionsByDateRange.builder()
                .hotelId("hotelId")
                .hasMore(true)
                .restrictionSets(List.of(RestrictionSets.builder()
                    .restrictionControl(RestrictionControl.builder()
                        .house(true)
                        .ratePlanCode("ratePlanCode")
                        .roomType("roomType")
                        .ratePlanCategory("ratePlanCategory")
                        .roomClass("roomClass")
                        .build())
                    .restrictionStatus(RestrictionStatus.builder()
                        .unit(5)
                        .code("someCode")
                        .build())
                    .actualTimeSpan(ActualTimeSpan.builder()
                        .startDate("2025-01-01")
                        .endDate("2025-01-10")
                        .build())
                    .onRequest(false)
                    .start("2025-01-20")
                    .end("2025-01-30")
                    .monday(false)
                    .tuesday(false)
                    .wednesday(false)
                    .thursday(false)
                    .friday(false)
                    .saturday(true)
                    .sunday(true)
                    .build()))
                .build())
            .build())
        .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    var sourceRestrictions = source.getRestrictionsByDateRange().getRestrictionsByDateRange();
    var sourceRestrictionSet = sourceRestrictions.getRestrictionSets().get(0);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertEquals(sourceRestrictions.getHotelId(), result.getHotelId());
    assertEquals(sourceRestrictions.isHasMore(), result.isHasMore());
    assertEquals(sourceRestrictionSet.getRestrictionControl().isHouse(), resultRestrictionSet.getRestrictionControl().isHouse());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCode(), resultRestrictionSet.getRestrictionControl().getRatePlanCode());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomType(), resultRestrictionSet.getRestrictionControl().getRoomType());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCategory(), resultRestrictionSet.getRestrictionControl().getRatePlanCategory());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomClass(), resultRestrictionSet.getRestrictionControl().getRoomClass());
    assertEquals(sourceRestrictionSet.getRestrictionStatus().getUnit(), resultRestrictionSet.getRestrictionStatus().getUnit());
    assertEquals(sourceRestrictionSet.getRestrictionStatus().getCode(), resultRestrictionSet.getRestrictionStatus().getCode());
    assertEquals(sourceRestrictionSet.getActualTimeSpan().getStartDate(), resultRestrictionSet.getActualTimeSpan().getStartDate());
    assertEquals(sourceRestrictionSet.getActualTimeSpan().getEndDate(), resultRestrictionSet.getActualTimeSpan().getEndDate());
    assertEquals(sourceRestrictionSet.isOnRequest(), resultRestrictionSet.isOnRequest());
    assertEquals(sourceRestrictionSet.getStart(), resultRestrictionSet.getStart());
    assertEquals(sourceRestrictionSet.getEnd(), resultRestrictionSet.getEnd());
    assertEquals(sourceRestrictionSet.isMonday(), resultRestrictionSet.isMonday());
    assertEquals(sourceRestrictionSet.isTuesday(), resultRestrictionSet.isTuesday());
    assertEquals(sourceRestrictionSet.isWednesday(), resultRestrictionSet.isWednesday());
    assertEquals(sourceRestrictionSet.isThursday(), resultRestrictionSet.isThursday());
    assertEquals(sourceRestrictionSet.isFriday(), resultRestrictionSet.isFriday());
    assertEquals(sourceRestrictionSet.isSaturday(), resultRestrictionSet.isSaturday());
    assertEquals(sourceRestrictionSet.isSunday(), resultRestrictionSet.isSunday());
  }

  @Test
  void toDto_WhenActualTimeSpanIsNull_ThenObjectMappedCorrectly() {
    var source = RestrictionsByDateRangeResult.builder()
          .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
                .restrictionsByDateRange(RestrictionsByDateRange.builder()
                      .hotelId("hotelId")
                      .hasMore(true)
                      .restrictionSets(List.of(RestrictionSets.builder()
                            .restrictionControl(RestrictionControl.builder()
                                  .house(true)
                                  .ratePlanCode("ratePlanCode")
                                  .roomType("roomType")
                                  .ratePlanCategory("ratePlanCategory")
                                  .roomClass("roomClass")
                                  .build())
                            .restrictionStatus(RestrictionStatus.builder()
                                  .unit(5)
                                  .code("someCode")
                                  .build())
                            .onRequest(false)
                            .start("2025-01-20")
                            .end("2025-01-30")
                            .monday(false)
                            .tuesday(false)
                            .wednesday(false)
                            .thursday(false)
                            .friday(false)
                            .saturday(true)
                            .sunday(true)
                            .build()))
                      .build())
                .build())
          .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    var sourceRestrictions = source.getRestrictionsByDateRange().getRestrictionsByDateRange();
    var sourceRestrictionSet = sourceRestrictions.getRestrictionSets().get(0);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertNull(resultRestrictionSet.getActualTimeSpan());
    assertEquals(sourceRestrictions.getHotelId(), result.getHotelId());
    assertEquals(sourceRestrictions.isHasMore(), result.isHasMore());
    assertEquals(sourceRestrictionSet.getRestrictionControl().isHouse(), resultRestrictionSet.getRestrictionControl().isHouse());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCode(), resultRestrictionSet.getRestrictionControl().getRatePlanCode());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomType(), resultRestrictionSet.getRestrictionControl().getRoomType());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCategory(), resultRestrictionSet.getRestrictionControl().getRatePlanCategory());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomClass(), resultRestrictionSet.getRestrictionControl().getRoomClass());
    assertEquals(sourceRestrictionSet.getRestrictionStatus().getUnit(), resultRestrictionSet.getRestrictionStatus().getUnit());
    assertEquals(sourceRestrictionSet.getRestrictionStatus().getCode(), resultRestrictionSet.getRestrictionStatus().getCode());
    assertEquals(sourceRestrictionSet.isOnRequest(), resultRestrictionSet.isOnRequest());
    assertEquals(sourceRestrictionSet.getStart(), resultRestrictionSet.getStart());
    assertEquals(sourceRestrictionSet.getEnd(), resultRestrictionSet.getEnd());
    assertEquals(sourceRestrictionSet.isMonday(), resultRestrictionSet.isMonday());
    assertEquals(sourceRestrictionSet.isTuesday(), resultRestrictionSet.isTuesday());
    assertEquals(sourceRestrictionSet.isWednesday(), resultRestrictionSet.isWednesday());
    assertEquals(sourceRestrictionSet.isThursday(), resultRestrictionSet.isThursday());
    assertEquals(sourceRestrictionSet.isFriday(), resultRestrictionSet.isFriday());
    assertEquals(sourceRestrictionSet.isSaturday(), resultRestrictionSet.isSaturday());
    assertEquals(sourceRestrictionSet.isSunday(), resultRestrictionSet.isSunday());
  }

  @Test
  void toDto_WhenRestrictionStatusIsNull_ThenObjectMappedCorrectly() {
    var source = RestrictionsByDateRangeResult.builder()
          .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
                .restrictionsByDateRange(RestrictionsByDateRange.builder()
                      .hotelId("hotelId")
                      .hasMore(true)
                      .restrictionSets(List.of(RestrictionSets.builder()
                            .restrictionControl(RestrictionControl.builder()
                                  .house(true)
                                  .ratePlanCode("ratePlanCode")
                                  .roomType("roomType")
                                  .ratePlanCategory("ratePlanCategory")
                                  .roomClass("roomClass")
                                  .build())
                            .onRequest(false)
                            .start("2025-01-20")
                            .end("2025-01-30")
                            .monday(false)
                            .tuesday(false)
                            .wednesday(false)
                            .thursday(false)
                            .friday(false)
                            .saturday(true)
                            .sunday(true)
                            .build()))
                      .build())
                .build())
          .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    var sourceRestrictions = source.getRestrictionsByDateRange().getRestrictionsByDateRange();
    var sourceRestrictionSet = sourceRestrictions.getRestrictionSets().get(0);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertNull(resultRestrictionSet.getActualTimeSpan());
    assertNull(resultRestrictionSet.getRestrictionStatus());
    assertEquals(sourceRestrictions.getHotelId(), result.getHotelId());
    assertEquals(sourceRestrictions.isHasMore(), result.isHasMore());
    assertEquals(sourceRestrictionSet.getRestrictionControl().isHouse(), resultRestrictionSet.getRestrictionControl().isHouse());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCode(), resultRestrictionSet.getRestrictionControl().getRatePlanCode());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomType(), resultRestrictionSet.getRestrictionControl().getRoomType());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRatePlanCategory(), resultRestrictionSet.getRestrictionControl().getRatePlanCategory());
    assertEquals(sourceRestrictionSet.getRestrictionControl().getRoomClass(), resultRestrictionSet.getRestrictionControl().getRoomClass());
    assertEquals(sourceRestrictionSet.isOnRequest(), resultRestrictionSet.isOnRequest());
    assertEquals(sourceRestrictionSet.getStart(), resultRestrictionSet.getStart());
    assertEquals(sourceRestrictionSet.getEnd(), resultRestrictionSet.getEnd());
    assertEquals(sourceRestrictionSet.isMonday(), resultRestrictionSet.isMonday());
    assertEquals(sourceRestrictionSet.isTuesday(), resultRestrictionSet.isTuesday());
    assertEquals(sourceRestrictionSet.isWednesday(), resultRestrictionSet.isWednesday());
    assertEquals(sourceRestrictionSet.isThursday(), resultRestrictionSet.isThursday());
    assertEquals(sourceRestrictionSet.isFriday(), resultRestrictionSet.isFriday());
    assertEquals(sourceRestrictionSet.isSaturday(), resultRestrictionSet.isSaturday());
    assertEquals(sourceRestrictionSet.isSunday(), resultRestrictionSet.isSunday());
  }

  @Test
  void toDto_WhenRestrictionControlIsNull_ThenObjectMappedCorrectly() {
    var source = RestrictionsByDateRangeResult.builder()
          .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
                .restrictionsByDateRange(RestrictionsByDateRange.builder()
                      .hotelId("hotelId")
                      .hasMore(true)
                      .restrictionSets(List.of(RestrictionSets.builder()
                            .onRequest(false)
                            .start("2025-01-20")
                            .end("2025-01-30")
                            .monday(false)
                            .tuesday(false)
                            .wednesday(false)
                            .thursday(false)
                            .friday(false)
                            .saturday(true)
                            .sunday(true)
                            .build()))
                      .build())
                .build())
          .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    var sourceRestrictions = source.getRestrictionsByDateRange().getRestrictionsByDateRange();
    var sourceRestrictionSet = sourceRestrictions.getRestrictionSets().get(0);
    var resultRestrictionSet = result.getRestrictionSets().get(0);
    assertNull(resultRestrictionSet.getActualTimeSpan());
    assertNull(resultRestrictionSet.getRestrictionStatus());
    assertNull(resultRestrictionSet.getRestrictionControl());
    assertEquals(sourceRestrictions.getHotelId(), result.getHotelId());
    assertEquals(sourceRestrictions.isHasMore(), result.isHasMore());
    assertEquals(sourceRestrictionSet.isOnRequest(), resultRestrictionSet.isOnRequest());
    assertEquals(sourceRestrictionSet.getStart(), resultRestrictionSet.getStart());
    assertEquals(sourceRestrictionSet.getEnd(), resultRestrictionSet.getEnd());
    assertEquals(sourceRestrictionSet.isMonday(), resultRestrictionSet.isMonday());
    assertEquals(sourceRestrictionSet.isTuesday(), resultRestrictionSet.isTuesday());
    assertEquals(sourceRestrictionSet.isWednesday(), resultRestrictionSet.isWednesday());
    assertEquals(sourceRestrictionSet.isThursday(), resultRestrictionSet.isThursday());
    assertEquals(sourceRestrictionSet.isFriday(), resultRestrictionSet.isFriday());
    assertEquals(sourceRestrictionSet.isSaturday(), resultRestrictionSet.isSaturday());
    assertEquals(sourceRestrictionSet.isSunday(), resultRestrictionSet.isSunday());
  }

  @Test
  void toDto_WhenRestrictionByDateRangeChildIsNull_ThenObjectMappedCorrectly() {
    var source = RestrictionsByDateRangeResult.builder()
          .restrictionsByDateRange(RestrictionsByDateRangeParent.builder()
                .restrictionsByDateRange(null)
                .build())
          .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    assertNull(result.getRestrictionSets());
    assertNull(result.getHotelId());
    assertFalse(result.isHasMore());
  }

  @Test
  void toDto_WhenRestrictionByDateRangeParentIsNull_ThenObjectMappedCorrectly() {
    var source = RestrictionsByDateRangeResult.builder()
          .restrictionsByDateRange(null)
          .build();

    var result = restrictionsByDateRangeMapper.toDto(source);

    assertNull(result.getRestrictionSets());
    assertNull(result.getHotelId());
    assertFalse(result.isHasMore());
  }
}