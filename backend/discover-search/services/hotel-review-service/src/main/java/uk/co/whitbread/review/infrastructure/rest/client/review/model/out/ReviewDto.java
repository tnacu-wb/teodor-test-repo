package uk.co.whitbread.review.infrastructure.rest.client.review.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {

  private String publishedDate;
  private double rating;
  private String tripType;
  private String title;
  private String text;
  private UserDto user;
}