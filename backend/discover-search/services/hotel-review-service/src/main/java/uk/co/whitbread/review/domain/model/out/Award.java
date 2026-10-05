package uk.co.whitbread.review.domain.model.out;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Award implements Serializable {

  private String awardType;

  private Integer year;

  private String image;
}

