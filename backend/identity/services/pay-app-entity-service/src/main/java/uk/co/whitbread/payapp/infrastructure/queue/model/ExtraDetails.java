package uk.co.whitbread.payapp.infrastructure.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString(exclude = "sharedBy")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExtraDetails {

  private String applicationGuid;
  private String sharedBy;
}
