package uk.co.whitbread.hotel.card.client.worldline.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorldlineContactDetails {

  private String title;

  private String foreName;

  private String lastName;

  @Nullable
  private String position;

  @Nullable
  private String telephone;

  @Nullable
  private String mobile;

  @Nullable
  private String email;

}
