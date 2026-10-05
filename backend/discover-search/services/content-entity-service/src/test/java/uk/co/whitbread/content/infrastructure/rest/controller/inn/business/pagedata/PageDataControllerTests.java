package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata;

import static java.util.List.of;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.DictionaryEnum;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.domain.ports.primary.PageDataInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.mapper.PageDataRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.pagedata.model.in.PageDataRequestDto;

@ExtendWith(MockitoExtension.class)
class PageDataControllerTests {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";

  @InjectMocks
  PageDataController pageDataController;

  @Mock
  private PageDataInPort pageDataInPort;

  @Mock
  private PageDataRequestDtoMapper pageDataRequestDtoMapper;

  @Test
  void getPageData_ShouldFindPageData() {
    //Arrange
    var pageDataRequestDto = PageDataRequestDto.builder()
        .dictionaries(of(
            DictionaryEnumDto.LAYOUT_DICTIONARY,
            DictionaryEnumDto.CARD_MANAGEMENT_DICTIONARY,
            DictionaryEnumDto.COMMON_ICONS_DICTIONARY,
            DictionaryEnumDto.USER_MANAGEMENT_DICTIONARY,
            DictionaryEnumDto.PROFILE_MANAGEMENT_DICTIONARY,
            DictionaryEnumDto.COMPANY_MANAGEMENT_DICTIONARY,
            DictionaryEnumDto.PAY_APPLICATION_DICTIONARY,
            DictionaryEnumDto.AUTH_DICTIONARY,
            DictionaryEnumDto.NOTIFICATIONS_DICTIONARY,
            DictionaryEnumDto.CONTACT_US_DICTIONARY
        ))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    PageDataRequest pageDataRequest = PageDataRequest.builder()
        .dictionaries(of(
            DictionaryEnum.LAYOUT_DICTIONARY,
            DictionaryEnum.CARD_MANAGEMENT_DICTIONARY,
            DictionaryEnum.COMMON_ICONS_DICTIONARY,
            DictionaryEnum.USER_MANAGEMENT_DICTIONARY,
            DictionaryEnum.PROFILE_MANAGEMENT_DICTIONARY,
            DictionaryEnum.COMPANY_MANAGEMENT_DICTIONARY,
            DictionaryEnum.PAY_APPLICATION_DICTIONARY,
            DictionaryEnum.AUTH_DICTIONARY,
            DictionaryEnum.NOTIFICATIONS_DICTIONARY,
            DictionaryEnum.CONTACT_US_DICTIONARY
        ))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    Mockito.when(pageDataRequestDtoMapper.toDomainModel(pageDataRequestDto))
        .thenReturn(pageDataRequest);
    Mockito.when(pageDataInPort.getPageData(pageDataRequest))
        .thenReturn(getPageData());

    //act
    var domainPageDataRequest = pageDataRequestDtoMapper.toDomainModel(
        pageDataRequestDto);
    var getPageDataResponse = pageDataInPort.getPageData(domainPageDataRequest);
    final Map<String, Map<String, String>> response = pageDataController.getPageData(
        pageDataRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), getPageDataResponse.get("layoutEndpoint"),
        response.get("layoutEndpoint"));
  }

  Map<String, Map<String, String>> getPageData() {
    Map<String, Map<String, String>> pageData = new HashMap<>();
    Map<String, String> values = new HashMap<>();
    values.put("innbusinessLayout.manageAccount.cards.title", "Cards");
    values.put("innbusinessLayout.menu.spending.icon.active",
        "innbusinessLayout.menu.spending.icon.active");
    values.put("innbusinessLayout.menu.spending.label", "Spending");
    pageData.put("layoutEndpoint", values);
    return pageData;
  }

}
