package uk.co.whitbread.basket.infrastructure.repository.id;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketInternalException;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Service
@Slf4j
public class BasketIdService {
  private static final String DEFAULT_ID_GENERATOR = "DIGIT";

  private final Map<String, BasketIdGenerator> generatorsById;

  public BasketIdService(List<BasketIdGenerator> generators) {
    this.generatorsById =
        generators.stream().collect(toMap(BasketIdGenerator::getId, Function.identity()));

    if (!this.generatorsById.containsKey(DEFAULT_ID_GENERATOR)) {
      var exception = new BasketInternalException(ErrorCode.BASKET_GENERATED_INVALID_EXCEPTION,
          "Invalid generated basket id");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  public String generateReference(String generatorId, String hotelId) {
    return getGenerator(generatorId).generateReference(hotelId);
  }

  public String generateReference(String hotelId) {
    return generateReference(DEFAULT_ID_GENERATOR, hotelId);
  }

  public String extractBasketIdHotelId(String generatorId, String basketId) {
    return getGenerator(generatorId).extractBasketIdHotelId(basketId);
  }

  public String extractBasketIdHotelId(String basketId) {
    return extractBasketIdHotelId(DEFAULT_ID_GENERATOR, basketId);
  }

  public String extractBasketIdSortKey(String generatorId, String basketId) {
    return getGenerator(generatorId).extractBasketIdSortKey(basketId);
  }

  public String extractBasketIdSortKey(String basketId) {
    return extractBasketIdSortKey(DEFAULT_ID_GENERATOR, basketId);
  }

  public String generateSortKey(String generatorId, String hotelId) {
    return getGenerator(generatorId).generateSortKey(hotelId);
  }

  public String generateSortKey(String hotelId) {
    return generateSortKey(DEFAULT_ID_GENERATOR, hotelId);
  }

  public String generateBasketId(String generatorId, String hotelId, String sortKey) {
    return getGenerator(generatorId).generateBasketId(hotelId, sortKey);
  }

  public String generateBasketId(String hotelId, String sortKey) {
    return generateBasketId(DEFAULT_ID_GENERATOR, hotelId, sortKey);
  }

  private BasketIdGenerator getGenerator(String generatorId) {
    return Optional.ofNullable(generatorsById.get(generatorId))
        .orElseThrow(() -> {
          var exception = new BasketInternalException(ErrorCode.BASKET_GENERATED_NULL_EXCEPTION,
              "Invalid generated basket id");
          ExceptionLogger.log(log, exception);
          return exception;
        });
  }

}
