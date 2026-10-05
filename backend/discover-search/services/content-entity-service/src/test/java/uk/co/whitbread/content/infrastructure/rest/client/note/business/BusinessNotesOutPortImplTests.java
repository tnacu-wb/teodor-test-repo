package uk.co.whitbread.content.infrastructure.rest.client.note.business;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_BUSINESS_NOTE_EXCEPTION;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNote;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.domain.model.note.business.out.Note;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.adapter.BusinessNotesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.mapper.BusinessNotesResponseMapper;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.in.BusinessNotesResponseAemDto;
import uk.co.whitbread.content.infrastructure.rest.client.note.business.model.out.BusinessNotesRequestAemDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNoteDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.NoteDto;

@ExtendWith(MockitoExtension.class)
class BusinessNotesOutPortImplTests {

  @InjectMocks
  private BusinessNotesOutPortImpl businessNotesOutPort;
  @Mock
  private BusinessNotesAemClient businessNotesAemClient;
  @Mock
  private BusinessNotesRequestMapper businessNotesRequestMapper;
  @Mock
  private BusinessNotesResponseMapper businessNotesResponseMapper;

  @Test
  void getBusinessNotes_ShouldReturnOK() {

    when(businessNotesRequestMapper.toDto(any())).thenReturn((createBusinessNotesRequestAemDto()));
    when(businessNotesAemClient.getBusinessNotes(createBusinessNotesRequestAemDto())).thenReturn(
        mockBusinessNotesResponseAemDto());
    when(businessNotesResponseMapper.toModel(any())).thenReturn(mockBusinessNotesResponse());

    var businessNotesAEMResponse = businessNotesOutPort.getBusinessNotes(createBusinessNotesRequest());

    assertThat(businessNotesAEMResponse, notNullValue());
    assertThat(businessNotesAEMResponse.getBusinessNotes().size(), is(1));
    assertThat(businessNotesAEMResponse.getHeaders().size(), is(1));
    assertThat(businessNotesAEMResponse.getFooters().size(), is(1));
    var headerNote = businessNotesAEMResponse.getHeaders().get(0);
    var footerNote = businessNotesAEMResponse.getFooters().get(0);
    var businessNote = businessNotesAEMResponse.getBusinessNotes().get(0);
    assertThat(headerNote.getId(), is("header.spendAuthorisation"));
    assertThat(headerNote.getLang(), is("en"));
    assertThat(headerNote.getValue(), is("Spend Authorisations"));
    assertThat(footerNote.getId(), is("footer.emailAuthorisation"));
    assertThat(footerNote.getLang(), is("en"));
    assertThat(footerNote.getValue(), is("Email central payment authorisation not required for this reservation."));
    assertThat(businessNote.getId(), is("parking"));
    assertThat(businessNote.getLang(), is("en"));
    assertThat(businessNote.getAllow(), is("Parking is authorised"));
    assertThat(businessNote.getDeny(), is("Parking is not authorised"));
    verify(businessNotesResponseMapper).toModel(any(BusinessNotesResponseAemDto.class));
    verify(businessNotesRequestMapper).toDto(any(BusinessNotesRequest.class));
    verify(businessNotesAemClient).getBusinessNotes(any(BusinessNotesRequestAemDto.class));
    verifyNoMoreInteractions(businessNotesResponseMapper, businessNotesRequestMapper, businessNotesAemClient);
  }

  @Test
  void getMeals__ShouldReturnException() {
    //Arrange
    var request = new BusinessNotesRequest("null");
    var expectedMessage = String.format("Get business notes from AEM. Language: ",
        request.getLang());
    when(businessNotesAemClient.getBusinessNotes(any()))
        .thenThrow(new AemResponseException(AEM_BUSINESS_NOTE_EXCEPTION, expectedMessage,
            new Exception()));

    //Act
    var actual = assertThrows(AemResponseException.class, () ->
        businessNotesOutPort.getBusinessNotes(request)
    );

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_BUSINESS_NOTE_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_BUSINESS_NOTE_EXCEPTION.getCode()));
  }

  private BusinessNotesRequest createBusinessNotesRequest() {
    BusinessNotesRequest businessNotesRequest = new BusinessNotesRequest();
    businessNotesRequest.setLang("en");
    return businessNotesRequest;
  }

  private BusinessNotesResponse mockBusinessNotesResponse() {
    Note headerNote = new Note();
    headerNote.setId("header.spendAuthorisation");
    headerNote.setLang("en");
    headerNote.setValue("Spend Authorisations");

    Note footerNote = new Note();
    footerNote.setId("footer.emailAuthorisation");
    footerNote.setLang("en");
    footerNote.setValue("Email central payment authorisation not required for this reservation.");

    BusinessNote businessNote = new BusinessNote();
    businessNote.setId("parking");
    businessNote.setLang("en");
    businessNote.setAllow("Parking is authorised");
    businessNote.setDeny("Parking is not authorised");

    BusinessNotesResponse businessNotesResponse = new BusinessNotesResponse();
    businessNotesResponse.setBusinessNotes(List.of(businessNote));
    businessNotesResponse.setHeaders(List.of(headerNote));
    businessNotesResponse.setFooters(List.of(footerNote));

    return businessNotesResponse;
  }

  private BusinessNotesResponseAemDto mockBusinessNotesResponseAemDto() {
    NoteDto headerNote = new NoteDto();
    headerNote.setId("header.spendAuthorisation");
    headerNote.setLang("en");
    headerNote.setValue("Spend Authorisations");

    NoteDto footerNote = new NoteDto();
    footerNote.setId("footer.emailAuthorisation");
    footerNote.setLang("en");
    footerNote.setValue("Email central payment authorisation not required for this reservation.");

    BusinessNoteDto businessNote = new BusinessNoteDto();
    businessNote.setId("parking");
    businessNote.setLang("en");
    businessNote.setAllow("Parking is authorised");
    businessNote.setDeny("Parking is not authorised");

    BusinessNotesResponseAemDto businessNotesResponseAemDto = new BusinessNotesResponseAemDto();
    businessNotesResponseAemDto.setBusinessNotes(List.of(businessNote));
    businessNotesResponseAemDto.setHeaders(List.of(headerNote));
    businessNotesResponseAemDto.setFooters(List.of(footerNote));

    return businessNotesResponseAemDto;
  }

  private BusinessNotesRequestAemDto createBusinessNotesRequestAemDto() {
    BusinessNotesRequestAemDto businessNotesRequestAemDto = new BusinessNotesRequestAemDto();
    businessNotesRequestAemDto.setLang("en");
    return businessNotesRequestAemDto;
  }
}
