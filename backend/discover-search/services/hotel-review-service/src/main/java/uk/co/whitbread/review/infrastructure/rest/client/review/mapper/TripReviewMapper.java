package uk.co.whitbread.review.infrastructure.rest.client.review.mapper;


import org.mapstruct.Mapper;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.ReviewResponseDto;


@Mapper(componentModel = "spring")
public interface TripReviewMapper {

  ReviewResponseDto toDto(ReviewResponse reviewResponse);

}