package uk.co.whitbread.basket.infrastructure.repository.id;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.ErrorCode.Constants;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketInternalException;

@ExtendWith(MockitoExtension.class)
class BasketIdServiceTest {

  @Test
  void testBasketIdServiceCreation_success() {
    var service = new BasketIdService(asList(mockGenerator("DIGIT")));

    assertThat(service, notNullValue());
  }

  @Test
  void testGenerateSortKey_success() {
    var generator = mockGenerator("DIGIT");
    when(generator.generateSortKey("ABC")).thenReturn("9763ce3e-2636-404f-8a68-9e41c954794f");
    var service = new BasketIdService(asList(generator));

    final String sortKey = service.generateSortKey("ABC");

    assertThat(sortKey, is("9763ce3e-2636-404f-8a68-9e41c954794f"));
    verify(generator, times(1)).generateSortKey("ABC");
  }

  @Test
  void testGenerateResNo_noGenerator() {
    var generator = mockGenerator("DIGIT");
    var service = new BasketIdService(asList(generator));

    final BasketInternalException ex = assertThrows(BasketInternalException.class, () -> {
      service.generateSortKey("OTHERGENERATOR", "123");
    });

    assertThat(ex.getDebugMessage(), is("Invalid generated basket id"));
    assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INTERNAL_SERVER_EXCEPTION));
  }

  @Test
  void testGenerateReference_success() {
    var generator = mockGenerator("DIGIT");
    when(generator.generateBasketId("TST", "1231231")).thenReturn("TST1231231");
    var service = new BasketIdService(List.of(generator));

    final String reference = service.generateBasketId("TST", "1231231");

        assertThat(reference, is("TST1231231"));
        verify(generator, times(1)).generateBasketId("TST", "1231231");
    }

  @Test
  void testGenerateReference_noGenerator() {
    var generator = mockGenerator("DIGIT");
    var service = new BasketIdService(asList(generator));

    final BasketInternalException ex = assertThrows(BasketInternalException.class, () -> {
      service.generateBasketId("OTHERGENERATOR", "TST", "1231231");
    });

    assertThat(ex.getDebugMessage(), is("Invalid generated basket id"));
    assertThat(ex.getGlobalErrTextTemplate(), is(ErrorCode.Constants.INTERNAL_SERVER_EXCEPTION));
  }

  @Test
  void testExtractSortKey_success() {
    var generator = mockGenerator("DIGIT");
    when(generator.extractBasketIdSortKey("TST-9763ce3e-2636-404f-8a68-9e41c954794f")).thenReturn("9763ce3e-2636-404f-8a68-9e41c954794f");
    var service = new BasketIdService(asList(generator));

    final String sortKey = service.extractBasketIdSortKey("TST-9763ce3e-2636-404f-8a68-9e41c954794f");

    assertThat(sortKey, is("9763ce3e-2636-404f-8a68-9e41c954794f"));
    verify(generator, times(1)).extractBasketIdSortKey("TST-9763ce3e-2636-404f-8a68-9e41c954794f");
  }

  @Test
  void testExtractSortKey_noGenerator() {
    var generator = mockGenerator("DIGIT");
    var service = new BasketIdService(asList(generator));

    final BasketInternalException ex = assertThrows(BasketInternalException.class, () -> {
      service.extractBasketIdSortKey("OTHERGENERATOR", "TST-9763ce3e-2636-404f-8a68-9e41c954794f");
    });

    assertThat(ex.getDebugMessage(), is("Invalid generated basket id"));
    assertThat(ex.getGlobalErrTextTemplate(), is(Constants.INTERNAL_SERVER_EXCEPTION));

  }

  private BasketIdGenerator mockGenerator(final String id) {
    var generatorMock = mock(BasketIdGenerator.class);
    when(generatorMock.getId()).thenReturn(id);

    return generatorMock;
  }


}
