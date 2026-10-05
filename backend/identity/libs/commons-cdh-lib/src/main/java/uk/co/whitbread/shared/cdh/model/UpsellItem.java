package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpsellItem {

  @JsonProperty("PostingDate")
  private String postingDate;

  @JsonProperty("Quantity")
  private Integer quantity;

  @JsonProperty("Adults")
  private Integer adults;

  @JsonProperty("Children")
  private Integer children;

  @JsonProperty("Code")
  private String code;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("Category")
  private String category;

  @JsonProperty("UnitCost")
  private Price unitCost;

  @JsonProperty("Subtotal")
  private Price subtotal;
}
