package uk.co.whitbread.company.client.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business implements Serializable {

  private AccessLevel accessLevel;
  private String employeeId;
}
