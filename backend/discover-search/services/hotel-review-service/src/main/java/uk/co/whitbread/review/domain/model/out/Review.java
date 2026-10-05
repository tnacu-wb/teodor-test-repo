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
public class Review implements Serializable {

  private String publishedDate;
  private double rating;
  private String tripType;
  private String title;
  private String text;
  private User user;
}
