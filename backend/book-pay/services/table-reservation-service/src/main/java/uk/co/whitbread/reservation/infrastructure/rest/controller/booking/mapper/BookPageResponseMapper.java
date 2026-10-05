package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.BookPageDto;

@Mapper(componentModel = "spring")
public interface BookPageResponseMapper {

  default BookPageDto toDto(BookPage bookPage) {
    if (bookPage == null) {
      return null;
    }
    return BookPageDto.builder()
        .name(bookPage.getName())
        .subtitleName(bookPage.getSubtitleName())
        .heroImageSrc(bookPage.getHeroImageSrc())
        .heroBackgroundImageSrc(bookPage.getHeroBackgroundImageSrc())
        .build();


  }

}
