package uk.co.whitbread.content.domain.model.note.business.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class BusinessNotesRequest implements SelfValidation<BusinessNotesRequest> {
  @NotEmpty
  private String lang;

  public BusinessNotesRequest(String lang) {
    this.lang = lang;
    this.validateSelf();
  }
}
