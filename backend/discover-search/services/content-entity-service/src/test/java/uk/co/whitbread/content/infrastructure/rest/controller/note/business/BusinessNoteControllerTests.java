package uk.co.whitbread.content.infrastructure.rest.controller.note.business;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.note.business.in.BusinessNotesRequest;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNote;
import uk.co.whitbread.content.domain.model.note.business.out.BusinessNotesResponse;
import uk.co.whitbread.content.domain.model.note.business.out.Note;
import uk.co.whitbread.content.domain.ports.primary.BusinessNotesInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper.BusinessNotesRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.mapper.BusinessNotesResponseDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.in.BusinessNotesRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNoteDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.BusinessNotesResponseDto;
import uk.co.whitbread.content.infrastructure.rest.controller.note.business.model.out.NoteDto;

@ExtendWith(MockitoExtension.class)
class BusinessNoteControllerTests {

  @InjectMocks
  private BusinessNoteController businessNoteController;

  @Mock
  private BusinessNotesInPort businessNotesInPort;
  @Mock
  private BusinessNotesRequestDtoMapper businessNotesRequestDtoMapper;
  @Mock
  private BusinessNotesResponseDtoMapper businessNotesResponseDtoMapper;

  @Test
  void getBusinessNotes_ShouldReturnOK() {

    BusinessNotesRequest businessNotesRequest = new BusinessNotesRequest("en");
    BusinessNotesRequestDto businessNotesRequestDto = new BusinessNotesRequestDto("en");
    BusinessNotesResponseDto businessNotesResponseDto = mockBusinessNotesResponseDto();
    BusinessNotesResponse businessNotesResponse = mockBusinessNotesResponse();
    Mockito.when(businessNotesRequestDtoMapper.toDomainModel(businessNotesRequestDto)).thenReturn(businessNotesRequest);
    Mockito.when(businessNotesInPort.getBusinessNotes(businessNotesRequest)).thenReturn(businessNotesResponse);
    Mockito.when(businessNotesResponseDtoMapper.toDto(businessNotesResponse)).thenReturn(businessNotesResponseDto);

    final ResponseEntity<BusinessNotesResponseDto> response = businessNoteController.getBusinessNotes(businessNotesRequestDto);

    Mockito.verify(businessNotesRequestDtoMapper).toDomainModel(businessNotesRequestDto);
    Mockito.verify(businessNotesInPort).getBusinessNotes(businessNotesRequest);
    Mockito.verify(businessNotesResponseDtoMapper).toDto(businessNotesResponse);
    Mockito.verifyNoMoreInteractions(businessNotesInPort, businessNotesRequestDtoMapper, businessNotesResponseDtoMapper);
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), response.getBody().getBusinessNotes().size(), 1);
    assertEquals(response.toString(), response.getBody().getHeaders().size(), 1);
    assertEquals(response.toString(), response.getBody().getFooters().size(), 1);
    assertEquals(response.toString(), response.getBody().getPackages().size(), 1);
    assertEquals(response.toString(), response.getBody().getCardTypes().size(), 1);
    assertEquals(response.toString(), response.getBody().getAllowances().size(), 1);
    BusinessNoteDto businessNote = response.getBody().getBusinessNotes().get(0);
    NoteDto headerNote = response.getBody().getHeaders().get(0);
    NoteDto footerNote = response.getBody().getFooters().get(0);
    NoteDto packageNote = response.getBody().getPackages().get(0);
    NoteDto cardTypeNote = response.getBody().getCardTypes().get(0);
    NoteDto allowanceNote = response.getBody().getAllowances().get(0);

    assertEquals(response.toString(), businessNote.getId(), "parking");
    assertEquals(response.toString(), businessNote.getLang(), "en");
    assertEquals(response.toString(), businessNote.getAllow(), "Parking is authorised");
    assertEquals(response.toString(), businessNote.getDeny(), "Parking is not authorised");

    assertEquals(response.toString(), headerNote.getId(), "header.spendAuthorisation");
    assertEquals(response.toString(), headerNote.getLang(), "en");
    assertEquals(response.toString(), headerNote.getValue(), "Spend Authorisations");

    assertEquals(response.toString(), footerNote.getId(), "footer.emailAuthorisation");
    assertEquals(response.toString(), footerNote.getLang(), "en");
    assertEquals(response.toString(), footerNote.getValue(), "Email central payment authorisation not required for this reservation.");

    assertEquals(response.toString(), packageNote.getId(), "mealDeal");
    assertEquals(response.toString(), packageNote.getLang(), "en");
    assertEquals(response.toString(), packageNote.getValue(), "Meal Deal is Pre-Booked and Authorised.");

    assertEquals(response.toString(), cardTypeNote.getId(), "card");
    assertEquals(response.toString(), cardTypeNote.getLang(), "en");
    assertEquals(response.toString(), cardTypeNote.getValue(), "Credit");

    assertEquals(response.toString(), allowanceNote.getId(), "authorizedAllowances");
    assertEquals(response.toString(), allowanceNote.getLang(), "en");
    assertEquals(response.toString(), allowanceNote.getValue(), "The following Allowances are to be charged to the {cardType} card used to secure the reservation if authorised");
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

    Note packageNote = new Note();
    packageNote.setId("mealDeal");
    packageNote.setLang("en");
    packageNote.setValue("Meal Deal is Pre-Booked and Authorised.");

    Note cardTypeNote = new Note();
    cardTypeNote.setId("card");
    cardTypeNote.setLang("en");
    cardTypeNote.setValue("Credit");

    Note allowanceNote = new Note();
    allowanceNote.setId("authorizedAllowances");
    allowanceNote.setLang("en");
    allowanceNote.setValue("The following Allowances are to be charged to the {cardType} card used to secure the reservation if authorised");

    BusinessNote businessNote = new BusinessNote();
    businessNote.setId("parking");
    businessNote.setLang("en");
    businessNote.setAllow("Parking is authorised");
    businessNote.setDeny("Parking is not authorised");

    BusinessNotesResponse businessNotesResponse = new BusinessNotesResponse();
    businessNotesResponse.setBusinessNotes(List.of(businessNote));
    businessNotesResponse.setHeaders(List.of(headerNote));
    businessNotesResponse.setFooters(List.of(footerNote));
    businessNotesResponse.setPackages(List.of(packageNote));
    businessNotesResponse.setCardTypes(List.of(cardTypeNote));
    businessNotesResponse.setAllowances(List.of(allowanceNote));

    return businessNotesResponse;
  }

  private BusinessNotesResponseDto mockBusinessNotesResponseDto() {
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

    NoteDto packageNote = new NoteDto();
    packageNote.setId("mealDeal");
    packageNote.setLang("en");
    packageNote.setValue("Meal Deal is Pre-Booked and Authorised.");

    NoteDto cardTypeNote = new NoteDto();
    cardTypeNote.setId("card");
    cardTypeNote.setLang("en");
    cardTypeNote.setValue("Credit");

    NoteDto allowanceNote = new NoteDto();
    allowanceNote.setId("authorizedAllowances");
    allowanceNote.setLang("en");
    allowanceNote.setValue("The following Allowances are to be charged to the {cardType} card used to secure the reservation if authorised");

    BusinessNotesResponseDto businessNotesResponseDto = new BusinessNotesResponseDto();
    businessNotesResponseDto.setBusinessNotes(List.of(businessNote));
    businessNotesResponseDto.setHeaders(List.of(headerNote));
    businessNotesResponseDto.setFooters(List.of(footerNote));
    businessNotesResponseDto.setPackages(List.of(packageNote));
    businessNotesResponseDto.setCardTypes(List.of(cardTypeNote));
    businessNotesResponseDto.setAllowances(List.of(allowanceNote));

    return businessNotesResponseDto;
  }
}
