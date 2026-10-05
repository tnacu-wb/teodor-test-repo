package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.DictionaryEnum;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.domain.ports.secondary.PageDataOutPort;

@ExtendWith(MockitoExtension.class)
class PageDataInPortImplTest {

  @InjectMocks
  PageDataInPortImpl pageDataInPortImpl;

  @Mock
  PageDataOutPort pageDataOutPort;

  @Test
  void getPageData_ShouldReturnOk() {
    //given
    when(this.pageDataOutPort.getPageData(any())).thenReturn(mockPageDataResponse());

    //when
    final var pageDataAemResponse = pageDataInPortImpl.getPageData(createPageDataRequest());

    //then
    assertThat(pageDataAemResponse, notNullValue());
  }

  private PageDataRequest createPageDataRequest() {
    return PageDataRequest.builder()
        .country("gb")
        .language("en")
        .dictionaries(List.of(DictionaryEnum.LAYOUT_DICTIONARY))
        .build();
  }

  private Map<String, Map<String, String>> mockPageDataResponse() {
    Map<String, Map<String, String>> response = new HashMap<>();
    Map<String, String> data = new HashMap<>();
    data.put("innbusinessLayout.manageAccount.cards.title", "Cards");
    data.put("innbusinessLayout.manageAccount.profile.text",
        "Enhance your stay with us! Complete your profile to enjoy customised services.");
    response.put("layoutEndpoint", data);
    return response;
  }

}
