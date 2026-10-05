package uk.co.whitbread.content.infrastructure.rest.controller.cookies;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.CookieGroup;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePoliciesInformation;
import uk.co.whitbread.content.domain.model.cookies.out.CookiePolicies;
import uk.co.whitbread.content.domain.model.cookies.out.IntroView;
import uk.co.whitbread.content.domain.model.cookies.out.ManageView;
import uk.co.whitbread.content.domain.ports.primary.CookiePoliciesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper.CookiePoliciesDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.mapper.CookiePoliciesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.in.CookiePoliciesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.CookieGroupDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.CookiePoliciesInformationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.CookiePoliciesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.IntroViewDto;
import uk.co.whitbread.content.infrastructure.rest.controller.cookies.model.out.ManageViewDto;

@ExtendWith(MockitoExtension.class)
class CookiePoliciesControllerTest {

  @InjectMocks
  private CookiePoliciesController cookiePoliciesController;

  @Mock
  private CookiePoliciesInPort cookiePoliciesInPort;
  @Mock
  private CookiePoliciesRequestDtoMapper cookiePoliciesRequestDtoMapper;
  @Mock
  private CookiePoliciesDtoMapper cookiePoliciesDtoMapper;

  @Test
  void getCookiePolicies__ShouldReturnOk() {
    //Arrange
    var cookiePoliciesRequestDto = createCookiePoliciesRequestDto();
    var cookiePoliciesRequest = mockCookiePoliciesRequest();
    var cookiePolicies = mockCookiePolicies();
    when(cookiePoliciesRequestDtoMapper.toDomainModel(cookiePoliciesRequestDto)).thenReturn(cookiePoliciesRequest);
    when(cookiePoliciesInPort.getCookiePolicies(cookiePoliciesRequest)).thenReturn(cookiePolicies);
    when(cookiePoliciesDtoMapper.toDto(cookiePolicies)).thenReturn(mockCookiePoliciesDto());

    //Act
    var response = cookiePoliciesController.getCookiePolicies(cookiePoliciesRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    Assertions.assertEquals("pi", response.getBody().getCookiePolicies().getBrand());
    Assertions.assertEquals("Cookies and how we use them",
        response.getBody().getCookiePolicies().getIntroView().getTitle());
    Assertions.assertEquals(
        "Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you.",
        response.getBody().getCookiePolicies().getIntroView().getDescription());
    Assertions.assertEquals("Manage cookies",
        response.getBody().getCookiePolicies().getIntroView().getManageButtonText());
    Assertions.assertEquals("Accept all cookies",
        response.getBody().getCookiePolicies().getIntroView().getAcceptAllButtonText());
    Assertions.assertEquals("Necessary Only",
            response.getBody().getCookiePolicies().getIntroView().getNecessaryOnlyButtonText());
    Assertions.assertEquals("Manage cookies", response.getBody().getCookiePolicies().getManageView().getTitle());
    Assertions.assertEquals("<p>Choose the cookies that work for you.</p>",
        response.getBody().getCookiePolicies().getManageView().getDescription());
    Assertions.assertEquals("Confirm settings",
        response.getBody().getCookiePolicies().getManageView().getSaveSettingsButtonText());
    Assertions.assertEquals("Always active",
        response.getBody().getCookiePolicies().getManageView().getAlwaysActiveText());
    Assertions.assertEquals("permissionEssential",
        response.getBody().getCookiePolicies().getManageView().getCookieGroup().get(0).getCookieName());
    Assertions.assertEquals("Essential",
        response.getBody().getCookiePolicies().getManageView().getCookieGroup().get(0).getTitle());
    Assertions.assertEquals("<p>Some cookies are essential – our website wouldn’t work without them!</p>",
        response.getBody().getCookiePolicies().getManageView().getCookieGroup().get(0).getDescription());
    Assertions.assertEquals(true,
        response.getBody().getCookiePolicies().getManageView().getCookieGroup().get(0).getIsAlwaysActive());
    Assertions.assertEquals("Essentials are always active.",
        response.getBody().getCookiePolicies().getManageView().getCookieGroup().get(0).getToggleLabel());

  }

  private CookiePoliciesInformationDto mockCookiePoliciesDto() {
    var cookieGroupDto = CookieGroupDto.builder()
        .cookieName("permissionEssential")
        .title("Essential")
        .description("<p>Some cookies are essential – our website wouldn’t work without them!</p>")
        .isAlwaysActive(true)
        .toggleLabel("Essentials are always active.")
        .build();

    var manageViewDto = ManageViewDto.builder()
        .title("Manage cookies")
        .description("<p>Choose the cookies that work for you.</p>")
        .saveSettingsButtonText("Confirm settings")
        .alwaysActiveText("Always active")
        .cookieGroup(List.of(cookieGroupDto))
        .build();

    var introViewDto = IntroViewDto.builder()
        .title("Cookies and how we use them")
        .description(
            "Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you.")
        .manageButtonText("Manage cookies")
        .acceptAllButtonText("Accept all cookies")
        .necessaryOnlyButtonText("Necessary Only")
        .build();

    var cookiePoliciesInformationDto = CookiePoliciesDto.builder()
        .brand("pi")
        .introView(introViewDto)
        .manageView(manageViewDto)
        .build();

    return CookiePoliciesInformationDto.builder()
        .cookiePolicies(cookiePoliciesInformationDto)
        .build();

  }

  private CookiePoliciesInformation mockCookiePolicies() {

    var cookieGroup = CookieGroup.builder()
        .cookieName("permissionEssential")
        .title("Essential")
        .description("<p>Some cookies are essential – our website wouldn’t work without them!</p>")
        .isAlwaysActive(true)
        .toggleLabel("Essentials are always active.")
        .build();

    var manageView = ManageView.builder()
        .title("Manage cookies")
        .description("<p>Choose the cookies that work for you.</p>")
        .saveSettingsButtonText("Confirm settings")
        .alwaysActiveText("Always active")
        .cookieGroup(List.of(cookieGroup))
        .build();

    var introView = IntroView.builder()
        .title("Cookies and how we use them")
        .description(
            "Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you.")
        .manageButtonText("Manage cookies")
        .acceptAllButtonText("Accept all cookies")
        .necessaryOnlyButtonText("Necessary Only")
        .build();

    var cookiePoliciesInformation = CookiePolicies.builder()
        .brand("pi")
        .introView(introView)
        .manageView(manageView)
        .build();

    return CookiePoliciesInformation.builder()
        .cookiePolicies(cookiePoliciesInformation)
        .build();
  }

  private CookiePoliciesRequest mockCookiePoliciesRequest() {
    return CookiePoliciesRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

  private CookiePoliciesRequestDto createCookiePoliciesRequestDto() {
    return CookiePoliciesRequestDto.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

}
