package uk.co.whitbread.infrastructure.rest.client.opera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.infrastructure.rest.client.opera.mapper.HotelStatusMapper;

@ExtendWith(MockitoExtension.class)
class OnSaleFlagOutPortImplTest {

  @Mock
  private CacheSearchOutPort cacheSearchOutPort;
  @Mock
  private HotelStatusMapper hotelStatusMapper;

  private OnSaleFlagOutPortImpl onSaleFlagOutPort;

  @Nested
  class GetHotelStatusTests {

    @BeforeEach
    void setUp() {
      onSaleFlagOutPort = new OnSaleFlagOutPortImpl(hotelStatusMapper, cacheSearchOutPort);
    }

    @Test
    void returnsEmptyList_WhenHotelIdsNullOrEmpty() {
      assertNotNull(onSaleFlagOutPort.getHotelStatus(null));
      assertEquals(0, onSaleFlagOutPort.getHotelStatus(null).size());
      assertEquals(0, onSaleFlagOutPort.getHotelStatus(List.of()).size());
      verifyNoMoreInteractions(cacheSearchOutPort);
    }

    @Test
    void returnsEmptyList_WhenCacheReturnsNullOrEmpty() {
      when(cacheSearchOutPort.getOnsaleFlagFromCache(anyList())).thenReturn(null);

      assertEquals(0, onSaleFlagOutPort.getHotelStatus(List.of("A", "B")).size());
      verify(cacheSearchOutPort, times(1)).getOnsaleFlagFromCache(anyList());

      when(cacheSearchOutPort.getOnsaleFlagFromCache(anyList())).thenReturn(List.of());

      assertEquals(0, onSaleFlagOutPort.getHotelStatus(List.of("A")).size());
      verify(cacheSearchOutPort, times(2)).getOnsaleFlagFromCache(anyList());
    }

    @Test
    void returnsMappedStatuses_WhenCacheReturnsDtos() {
      reset(cacheSearchOutPort);

      // Create mocks instead of concrete DTOs to avoid javax.* test-time deps
      HotelStatusDto dto1 = mock(HotelStatusDto.class);
      when(cacheSearchOutPort.getOnsaleFlagFromCache(anyList())).thenReturn(List.of(dto1));
      when(hotelStatusMapper.toDomainModel(dto1)).thenReturn(
          new HotelStatus("LONKIN", true, "OPERA"));

      List<HotelStatus> result = onSaleFlagOutPort.getHotelStatus(List.of("LONKIN"));

      ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
      verify(cacheSearchOutPort, times(1)).getOnsaleFlagFromCache(captor.capture());
      assertEquals(List.of("LONKIN"), captor.getValue());

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals("LONKIN", result.get(0).getHotelId());
      assertEquals(true, result.get(0).getOnSale());
    }
  }
}
