package uk.co.whitbread.payapp.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetails {

  private String title;
  private String foreName;
  private String lastName;
  private String position;
  private String telephone;
  private String mobile;
  private String email;

}
