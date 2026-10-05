package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Global {

  private String addRoom;
  private String adults;
  private String doubleLabel;
  private String family;
  private String accessible;
  private String roomLabel;
  private String nights;
  private String tomorrow;
  private List<Offer> offers;
  private String single;
  private String done;
  private String room;
  private String twin;
  private String adult;
  private String child;
  private String night;
  private String rooms;
  private String today;
  private Integer maxRooms;
  private String adultsLabel;
  private String childrenLabel;
  private String children;
  private String roomsLabel;
  private Brand brand;
}
