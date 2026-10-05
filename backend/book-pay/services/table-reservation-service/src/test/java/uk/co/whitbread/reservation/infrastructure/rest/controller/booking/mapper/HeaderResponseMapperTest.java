package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.NavbarItem;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemHeaderResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LinkItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.NavbarItemDto;
@ExtendWith(MockitoExtension.class)
class HeaderResponseMapperTest {
  @InjectMocks
  private HeaderResponseMapperImpl mapper;

  @Test
  void testToDto() {

    AemHeaderResponse aemHeaderResponse = mock(AemHeaderResponse.class);
    when(aemHeaderResponse.getLogoSrc()).thenReturn("logo.png");
    when(aemHeaderResponse.getLogoAlt()).thenReturn("Logo Alt");
    when(aemHeaderResponse.getLocationSrc()).thenReturn(Collections.singletonList("location.png"));
    when(aemHeaderResponse.getMoreLocationName()).thenReturn("More Locations");

    AemHeaderResponseDto dto = mapper.toDto(aemHeaderResponse);


    assertEquals("logo.png", dto.getLogoSrc());
    assertEquals("Logo Alt", dto.getLogoAlt());
    assertEquals(Collections.singletonList("location.png"), dto.getLocationSrc());
    assertEquals("More Locations", dto.getMoreLocationName());
  }
  @Test
  void testToDtoWithNullInput(){
    AemHeaderResponseDto responseDto= mapper.toDto(null);
    assertNull(responseDto);


  }


  @Test
  void testToNavBarDto() {
    // Create a mock List of NavbarItem
    List<NavbarItem> navbarItems = new ArrayList<>();
    NavbarItem navbarItem1 = mock(NavbarItem.class);
    when(navbarItem1.getName()).thenReturn("Item 1");

    NavbarItem navbarItem2 = mock(NavbarItem.class);
    when(navbarItem2.getName()).thenReturn("Item 2");

    navbarItems.add(navbarItem1);
    navbarItems.add(navbarItem2);

    List<NavbarItemDto> dtoList = mapper.toNavBarDto(navbarItems);

    assertEquals(2, dtoList.size());
    assertEquals("Item 1", dtoList.get(0).getName());
    assertEquals("Item 2", dtoList.get(1).getName());
  }
  @Test
  void testToNavBarDtoWithNullInput(){
    List<NavbarItemDto> dtoList = mapper.toNavBarDto(null);
    assertNotNull(dtoList);
    assertEquals(0, dtoList.size());


  }

  @Test
  void testToLinkItemsDto() {
    // Create a mock List of LinkItem
    List<LinkItem> linkItems = new ArrayList<>();
    LinkItem linkItem1 = mock(LinkItem.class);
    when(linkItem1.getLinkSrc()).thenReturn("link1.html");
    when(linkItem1.getName()).thenReturn("Link 1");
    when(linkItem1.isOpenInNewTab()).thenReturn(false);

    LinkItem linkItem2 = mock(LinkItem.class);
    when(linkItem2.getLinkSrc()).thenReturn("link2.html");
    when(linkItem2.getName()).thenReturn("Link 2");
    when(linkItem2.isOpenInNewTab()).thenReturn(true);

    linkItems.add(linkItem1);
    linkItems.add(linkItem2);

    // Create a DTO list using the method being tested
    List<LinkItemDto> dtoList = mapper.toLinkItemsDto(linkItems);

    // Verify that the DTO list contains the expected items
    assertEquals(2, dtoList.size());
    assertEquals("link1.html", dtoList.get(0).getLinkSrc());
    assertEquals("Link 1", dtoList.get(0).getName());
    assertFalse(dtoList.get(0).isOpenInNewTab());

    assertEquals("link2.html", dtoList.get(1).getLinkSrc());
    assertEquals("Link 2", dtoList.get(1).getName());
    assertTrue(dtoList.get(1).isOpenInNewTab());
  }
  @Test
  void testToLinkItemsDtoWithNullInput(){
    List<LinkItemDto> dtoList = mapper.toLinkItemsDto(null);
    assertNotNull(dtoList);
    assertEquals(0, dtoList.size());


  }


}