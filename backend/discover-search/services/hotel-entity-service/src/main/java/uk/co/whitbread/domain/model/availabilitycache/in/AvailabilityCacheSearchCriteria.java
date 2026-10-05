package uk.co.whitbread.domain.model.availabilitycache.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityCacheSearchCriteria {

  private List<String> hotelCodes;
  private String arrival;
  private String departure;
  private String language;
  private String country;
  private int rooms;
  private List<Integer> adults;
  private List<Integer> children;
  protected List<String> type;
  private int page;
  private int size;
}
