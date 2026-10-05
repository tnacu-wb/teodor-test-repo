package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferenceSettingsItemDto {

  @JsonProperty("smsTypeID")
  private Integer smsTypeId;

  @JsonProperty("isSMSSelected")
  private Boolean isSmsSelected;

  private String smsTypeDescription;

  private String smsType;
}