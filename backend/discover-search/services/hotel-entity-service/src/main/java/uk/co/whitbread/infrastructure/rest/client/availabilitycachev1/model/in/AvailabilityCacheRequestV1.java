package uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCacheRequestV1 {

  private List<String> hotelCodes;
  private String arrival;
  private String departure;
  private String channel;
  private List<Integer> adults;
  private List<Integer> children;
  private List<Boolean> cot;
  private List<Integer> roomQty;
  private String language;
  private String country;
  private List<List<String>> roomTypesList;
  private int rooms;
  private Boolean flagMlos;
}
