package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.Column;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.SocialMediaIcon;
import uk.co.whitbread.reservation.domain.model.out.aem.Tab;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemFooterResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ColumnDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LinkItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.SocialMediaIconDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.TabDto;

@ExtendWith(MockitoExtension.class)
class FooterResponseMapperTest {

  @InjectMocks
  FooterResponseMapperImpl mapper;

  @Test
  void testToDto() {

    // Create a mock AemFooterResponse
    AemFooterResponse mockResponse = new AemFooterResponse();
    mockResponse.setLegalCopyRightLabel("Copyright");
    mockResponse.setCopyrightInfo("2023");
    mockResponse.setSocialMediaIcons(new ArrayList<>());
    mockResponse.setTabs(new ArrayList<>());

    // Create a mock AemFooterResponseDto
    AemFooterResponseDto mockResponseDto = new AemFooterResponseDto();
    mockResponseDto.setLegalCopyRightLabel("Copyright");
    mockResponseDto.setCopyrightInfo("2023");
    mockResponseDto.setSocialMediaIcons(new ArrayList<>());
    mockResponseDto.setTabs(new ArrayList<>());
    AemFooterResponseDto result = mapper.toDto(mockResponse);

    // Verify that the result matches the expected mock response
    assertEquals(mockResponseDto.getCopyrightInfo(), result.getCopyrightInfo());
  }
  @Test
  void testToDtoWithNullInput() {
    AemFooterResponseDto result = mapper.toDto(null);
    assertNull(result);


  }


  @Test
  void testToSocialMediaIconsDto() {
    // Create a list of mock SocialMediaIcon objects
    List<SocialMediaIcon> socialMediaIcons = new ArrayList<>();
    SocialMediaIcon icon1 = new SocialMediaIcon();
    icon1.setLabel("Facebook");
    icon1.setVisible(true);
    icon1.setLinkSrc("https://www.facebook.com");
    socialMediaIcons.add(icon1);

    SocialMediaIcon icon2 = new SocialMediaIcon();
    icon2.setLabel("Twitter");
    icon2.setVisible(true);
    icon2.setLinkSrc("https://www.twitter.com");
    socialMediaIcons.add(icon2);

    List<SocialMediaIconDto> expected = new ArrayList<>();
    expected.add(new SocialMediaIconDto("Facebook", "https://www.facebook.com", true));
    expected.add(new SocialMediaIconDto("Twitter", "https://www.twitter.com", true));
    // Test the toSocialMediaIconsDto method
    List<SocialMediaIconDto> result = mapper.toSocialMediaIconsDto(socialMediaIcons);
    assertNotNull(result);
    assertEquals(socialMediaIcons.size(), result.size());
  }

  @Test
  void testToSocialMediaIconsDtoWithNullInput(){
    List<SocialMediaIconDto> result = mapper.toSocialMediaIconsDto(null);
    assertNotNull(result);
    assertEquals(0, result.size());



  }

  @Test
  void testToTabsDto() {
   // FooterResponseMapper mapper = mock(FooterResponseMapper.class);

    List<Tab> tabs = new ArrayList<>();
    Tab tab1 = new Tab();
    tab1.setName("Tab 1");
    tab1.setColumns(new ArrayList<>());
    tabs.add(tab1);

    Tab tab2 = new Tab();
    tab2.setName("Tab 2");
    tab2.setColumns(new ArrayList<>());
    tabs.add(tab2);

    List<TabDto> expected1 = new ArrayList<>();
    expected1.add(new TabDto("Tab 1", new ArrayList<>()));
    expected1.add(new TabDto("Tab 2", new ArrayList<>()));
    //when(mapper.toTabsDto(tabs)).thenReturn(expected1);

    List<TabDto> result1 = mapper.toTabsDto(tabs);
    assertEquals(tabs.size(), result1.size());
  }

  @Test
  void testToTabsDtoWithNullInput(){
    List<TabDto> result1 = mapper.toTabsDto(null);
    assertNotNull(result1);
    assertEquals(0, result1.size());

  }

  @Test
  void testToColumnsDto() {
    List<Column> columns = new ArrayList<>();
    Column column1 = new Column();
    column1.setLinkItems(new ArrayList<>());
    column1.setName("abc");
    columns.add(column1);

    Column column2 = new Column();
    column2.setName("xyz");
    column2.setLinkItems(new ArrayList<>());
    columns.add(column2);

    List<ColumnDto> columnDto = mapper.toColumnsDto(columns);
    assertEquals(columns.size(), columnDto.size());


  }
  @Test
  void testToColumnsDtoWithNullInput(){
    List<ColumnDto> columnDto = mapper.toColumnsDto(null);
    assertNotNull(columnDto);
    assertEquals(0, columnDto.size());


  }

  @Test
  void testToLinkItemsDto() {
    List<LinkItem> linkItems = new ArrayList<>();
    LinkItem linkItem1 = new LinkItem();
    linkItem1.setOpenInNewTab(true);
    linkItem1.setLinkSrc("linkSrc");
    linkItem1.setName("abc");
    linkItems.add(linkItem1);
    List<LinkItemDto> linkItemDto = mapper.toLinkItemsDto(linkItems);
    assertNotNull(linkItemDto);

  }
  @Test
  void testToLinkItemsDtoWithNullInput(){
    List<LinkItemDto> linkItemDto = mapper.toLinkItemsDto(null);
    assertNotNull(linkItemDto);
    assertEquals(0, linkItemDto.size());

  }




}
