package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.content.domain.ports.secondary.BusinessNotesOutPort;

@ExtendWith(MockitoExtension.class)
class BusinessNotesInPortImplTest {

  @InjectMocks
  private BusinessNotesInPortImpl businessNotesInPort;

  @Mock
  private BusinessNotesOutPort businessNotesOutPort;

  @Test
  void getBusinessNotes_ShouldReturnOk() {
    BusinessNotesRequest businessNotesRequest = new BusinessNotesRequest("en");
    when(this.businessNotesOutPort.getBusinessNotes(businessNotesRequest)).thenReturn(mockBusinessNotesResponse());

    final var businessNotesResponse = businessNotesInPort.getBusinessNotes(businessNotesRequest);

    assertThat(businessNotesResponse, notNullValue());
    assertThat(businessNotesResponse.getBusinessNotes().size(), is(1));
    assertThat(businessNotesResponse.getHeaders().size(), is(1));
    assertThat(businessNotesResponse.getFooters().size(), is(1));
    var businessNote = businessNotesResponse.getBusinessNotes().get(0);
    var header = businessNotesResponse.getHeaders().get(0);
    var footer = businessNotesResponse.getFooters().get(0);
    assertThat(header.getId(), is("header.spendAuthorisation"));
    assertThat(header.getLang(), is("en"));
    assertThat(header.getValue(), is("Spend Authorisations"));
    assertThat(footer.getId(), is("footer.emailAuthorisation"));
    assertThat(footer.getLang(), is("en"));
    assertThat(footer.getValue(), is("Email central payment authorisation not required for this reservation."));
    assertThat(businessNote.getId(), is("parking"));
    assertThat(businessNote.getLang(), is("en"));
    assertThat(businessNote.getAllow(), is("Parking is authorised"));
    assertThat(businessNote.getDeny(), is("Parking is not authorised"));
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
}
