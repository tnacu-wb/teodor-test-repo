package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.out.aem.BookPage;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.BookPageDto;

@ExtendWith(MockitoExtension.class)
class BookPageResponseMapperTest {

  @InjectMocks
  BookPageResponseMapperImpl mapper;
  @Test
  void testToDto() {
    BookPage mockBookPage = new BookPage();
    mockBookPage.setName("Sample Book");
    mockBookPage.setSubtitleName("Subtitle");
    mockBookPage.setHeroImageSrc("book_image.jpg");
    mockBookPage.setHeroBackgroundImageSrc("background_image.jpg");
    BookPageDto result = mapper.toDto(mockBookPage);
    assertEquals(mockBookPage.getName(), result.getName());

  }

  @Test
  void testToDtoWithNullInput() {
    BookPageDto result = mapper.toDto(null);
    assertNull(result);
  }

}