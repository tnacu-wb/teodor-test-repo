package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuRespDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuResponseDto;
@ExtendWith(MockitoExtension.class)
class MenuResponseMapperTest {
  @InjectMocks
  MenuResponseMapperImpl menuResponseMapper;



  @Test
  void testToDto() {
    MenuResponse menuResponse = mockMenuResponse();
    MenuResponseDto menuResponseDto = menuResponseMapper.toDto(menuResponse);
    assertEquals(1, menuResponseDto.getMenus().size());
  }
  @Test
  void testToDto_withNullData() {
    MenuResponseDto menuResponseDto = menuResponseMapper.toDto(null);
    assertNull(menuResponseDto);
  }
  @Test
  void testToDto_withEmptyMenu() {
    MenuResponse menuResponse = new MenuResponse();
    menuResponse.setMenus(null);
    List<MenuRespDto> result = menuResponseMapper.toMenuResponseDto(menuResponse);
    assertNotNull(result);
    assertEquals(Collections.emptyList(), result);
  }
  private MenuResponse mockMenuResponse() {
    MenuResponse menuResponse = new MenuResponse();
    MenuResp menuResp = new MenuResp();
    menuResp.setName("abc");
    menuResp.setId("abc123");
    menuResp.setAvailable(true);
    menuResponse.setMenus(List.of(menuResp));
    return menuResponse;

  }
}