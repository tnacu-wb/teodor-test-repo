package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.CookieGroup;
import uk.co.whitbread.reservation.domain.model.out.aem.CookiePolicies;
import uk.co.whitbread.reservation.domain.model.out.aem.IntroView;
import uk.co.whitbread.reservation.domain.model.out.aem.ManageView;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemCookieContentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookieGroupDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookiePoliciesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.IntroViewDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ManageViewDto;

@ExtendWith(MockitoExtension.class)
class CookieContentResponseMapperTest {

  @InjectMocks
  CookieContentResponseMapperImpl mapper;

  @Test
  void testToDto() {
    AemCookieContentResponse mockResponse = new AemCookieContentResponse();
    CookiePolicies mockCookiePolicies = new CookiePolicies();
    mockCookiePolicies.setBrand("BrandName");
    mockCookiePolicies.setManageView(new ManageView());
    mockCookiePolicies.setVersion("1.0");
    mockCookiePolicies.setIntroView(new IntroView());
    mockResponse.setCookiePolicies(mockCookiePolicies);
    AemCookieContentResponseDto result = mapper.toDto(mockResponse);
    assertEquals(mockResponse.getCookiePolicies().getBrand(), result.getCookiePolicies().getBrand());
  }
  @Test
  void testToDtoWithNullInput(){
    AemCookieContentResponseDto result = mapper.toDto(null);
    assertNull(result);



  }

  @Test
  void testToCookiePoliciesDto() {
    CookiePolicies mockCookiePolicies = new CookiePolicies();
    mockCookiePolicies.setBrand("BrandName");
    mockCookiePolicies.setManageView(new ManageView());
    mockCookiePolicies.setVersion("1.0");
    mockCookiePolicies.setIntroView(new IntroView());
    CookiePoliciesDto result = mapper.toCookiePoliciesDto(mockCookiePolicies);
    assertEquals(mockCookiePolicies.getBrand(), result.getBrand());
  }
  @Test
  void testToCookiePoliciesDtoWithNullInput() {
    CookiePoliciesDto result = mapper.toCookiePoliciesDto(null);
    assertNull(result);
  }

  @Test
  void testToManageViewDto() {

    // Create a mock ManageView
    ManageView mockManageView = new ManageView();
    mockManageView.setTitle("Manage Cookies");
    mockManageView.setDescription("Description");
    mockManageView.setAlwaysActiveText("Always Active");
    mockManageView.setSaveSettingsButtonText("Save Settings");
    mockManageView.setCookieGroup(new ArrayList<>());

    ManageViewDto result = mapper.toManageViewDto(mockManageView);
    assertEquals(mockManageView.getTitle(), result.getTitle());

  }

  @Test
  void testToManageViewDtoWithNullInput() {
    ManageViewDto result = mapper.toManageViewDto(null);
    assertNull(result);
  }

  @Test
  void testToIntroViewDto() {
    IntroView mockIntroView = new IntroView();
    mockIntroView.setAcceptAllButtonText("Accept All");
    mockIntroView.setManageButtonText("Manage Cookies");
    mockIntroView.setDescription("Cookie Description");
    mockIntroView.setTitle("Cookie Title");
    IntroViewDto result = mapper.toIntroViewDto(mockIntroView);
    assertEquals(mockIntroView.getTitle(), result.getTitle());
  }

  @Test
  void testToIntroViewDtoWithNullInput() {
   // CookieContentResponseMapper mapper = Mockito.mock(CookieContentResponseMapper.class);
    IntroViewDto result = mapper.toIntroViewDto(null);
    assertNull(result);
  }

  @Test
  void testToCookieGroupDto() {
   // CookieContentResponseMapper mapper = Mockito.mock(CookieContentResponseMapper.class);
    List<CookieGroup> cookieGroups = new ArrayList<>();
    CookieGroup group1 = new CookieGroup();
    group1.setCookieName("Group 1");
    group1.setToggleLabel("Toggle 1");
    group1.setAlwaysActive(true);
    group1.setTitle("Group Title 1");
    group1.setDescription("Group Description 1");
    cookieGroups.add(group1);

    CookieGroup group2 = new CookieGroup();
    group2.setCookieName("Group 2");
    group2.setToggleLabel("Toggle 2");
    group2.setAlwaysActive(false);
    group2.setTitle("Group Title 2");
    group2.setDescription("Group Description 2");
    cookieGroups.add(group2);
    List<CookieGroupDto> result = mapper.toCookieGroupDto(cookieGroups);
    assertEquals(cookieGroups.size(), result.size());

  }

  @Test
  void testToCookieGroupDtoWithNullInput() {
    //CookieContentResponseMapper mapper = Mockito.mock(CookieContentResponseMapper.class);
    List<CookieGroupDto> result = mapper.toCookieGroupDto(null);
    assertEquals(0, result.size());
  }


}