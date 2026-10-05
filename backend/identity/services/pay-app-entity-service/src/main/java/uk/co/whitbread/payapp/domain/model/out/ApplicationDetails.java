package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDetails {

  private String status;
  private String accountName;
  private String applicationId;
  private String applicationGuid;
  private String resumeUrl;
  private String scheme;

}
