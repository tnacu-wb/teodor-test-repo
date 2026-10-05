package uk.co.whitbread.basket.infrastructure.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.model.basket.in.CreateBasketRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.basket.out.ReservationInfoPaymentType;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInRequestDto;
import uk.co.whitbread.basket.generated.models.ohip.PreCheckInResponse;
import uk.co.whitbread.basket.generated.models.ohip.ReservationByBasketRefResponseDto;
import uk.co.whitbread.basket.generated.models.ohip.UdfsRequestDto;
import uk.co.whitbread.basket.infrastructure.repository.exception.BasketNotFoundException;
import uk.co.whitbread.basket.infrastructure.repository.exception.InvalidBasketIdException;
import uk.co.whitbread.basket.infrastructure.repository.id.BasketIdService;
import uk.co.whitbread.basket.infrastructure.repository.mapper.BasketEntityMapper;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketPaymentStatusEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketStatusEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.PaymentProvider;
import uk.co.whitbread.basket.infrastructure.rest.client.ohip.service.OhipAdapterClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@RequiredArgsConstructor
public class BasketOutPortImpl implements BasketOutPort {

  private static final String BASKET_NOT_FOUND = "No basket found with reference: %s";
  private static final String BASKET_ID_NOT_FOUND = "No basket found with id: %s";

  private final BasketRepository basketRepository;
  private final BasketEntityMapper mapper;
  private final BasketIdService basketIdService;
  private final HotelInfoOutPort hotelInfoOutPort;
  private final CleanUpTime cleanUpTime;
  private final OhipAdapterClient ohipAdapterClient;

  private static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.SECONDS);
  }

  @Override
  public Basket createBasket(final CreateBasketRequest createBasketRequest) {
    log.debug("Entering create basket with createBasketRequest={}", createBasketRequest);
    BasketEntity basketEntity = buildBaseBasketEntity(createBasketRequest);
    basketRepository.save(basketEntity);
    return mapper.toDomainModel(basketEntity);
  }

  private String getSortKey(String threeLetterHotelId) {
    return basketIdService.generateSortKey(threeLetterHotelId);
  }

  private String getReference(String threeLetterHotelId, CreateBasketRequest createBasketRequest) {
    if (!Objects.isNull(createBasketRequest.getMigratedResNo())
        && !createBasketRequest.getMigratedResNo().isEmpty()) {
      return createBasketRequest.getMigratedResNo();
    }
    return basketIdService.generateReference(threeLetterHotelId);
  }

  @Override
  public Basket updateBasket(final Basket basket) {
    log.debug("Entering update basket with basket={}", basket);
    return mapper.toDomainModel(
        basketRepository.updateBasket(mapper.toEntityModel(basket)).block());
  }

  @Override
  public Optional<Basket> getBasketByReference(final String reference) {
    return basketRepository.getByReference(reference)
        .map(mapper::toDomainModel)
        .map(Optional::of)
        .orElseGet(() -> {
          log.info(BASKET_NOT_FOUND + "{}", sanitizeString(reference));
          return Optional.empty();
        });
  }

  @Override
  public Basket getBasketById(final String basketId) {
    return mapper.toDomainModel(basketRepository.getByBasketId(basketId)
        .orElseThrow(
            () -> {
              var exception = new InvalidBasketIdException(
                  ErrorCode.BASKET_NOT_FOUND_EXCEPTION,
                  String.format(BASKET_ID_NOT_FOUND, basketId));
              ExceptionLogger.log(log, exception);
              throw exception;
            }));
  }


  @Override
  public void updateBasketPayment(final Basket basket) {
    log.info("Entering update basket payment with reference={}, paymentId={} and paymentOption={}",
        basket.getBasketId(), basket.getPaymentID(), basket.getPaymentOption());
    basketRepository.getByBasketId(basket.getBasketId())
        .ifPresentOrElse(basketEntity -> {
          basketEntity.setPaymentID(basket.getPaymentID());
          basketEntity.setPaymentOption(basket.getPaymentOption());
          basketEntity.setTotalCost(basket.getTotalCost());
          basketEntity.setCurrency(basket.getCurrency());
          basketEntity.setEmailAddress(basket.getEmailAddress());
          basketEntity.setPaymentChannel(basket.getPaymentChannel());
          basketEntity.setStatus(BasketStatusEntity.valueOf(basket.getStatus().name()));
          basketEntity.setPaymentProvider(basket.getPaymentProvider() != null
              ? PaymentProvider.valueOf(basket.getPaymentProvider()) : null);
          basketRepository.updateBasket(basketEntity);
        }, () -> {
          var message = String.format("Error while updating basket payment with "
                  + "reference=%s, paymentId=%s and paymentOption=%s.%s %s",
              basket.getBasketId(), basket.getPaymentID(), basket.getPaymentOption(),
              BASKET_NOT_FOUND, basket.getReference());
          var exception = new BasketNotFoundException(
              ErrorCode.UPDATE_BASKET_NOT_FOUND_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        });
  }

  @Override
  public void updateBasketStatus(final String basketId, final BasketStatus basketStatus,
                                 final Optional<Instant> cleanUpBaseTime) {
    log.info("Entering update basket status with basket id={} and basketStatus={}",
        sanitizeString(basketId),
        basketStatus);
    basketRepository.getByBasketId(basketId)
        .ifPresentOrElse(basketEntity -> {
          basketEntity.setStatus(BasketStatusEntity.valueOf(basketStatus.name()));
          cleanUpBaseTime.ifPresent(instant ->
              basketEntity.setCleanUpTime(cleanUpTime.getCleanUpTime(basketStatus, instant)));
          basketRepository.updateBasket(basketEntity);
        }, () -> {
          var message = String.format(
              "Error while update basket status with basket id=%s and basketStatus=%s.%s %s",
              basketId, basketStatus, BASKET_NOT_FOUND, basketId);
          var exception = new BasketNotFoundException(
              ErrorCode.UPDATE_STATUS_BASKET_NOT_FOUND_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        });
  }

  @Override
  public void updatePollingStartedAt(final String basketId, final String timestamp) {
    log.debug("Entering update polling started at with basket id={} and timestamp={}",
        sanitizeString(basketId),
        timestamp);
    basketRepository.getByBasketId(basketId)
        .ifPresentOrElse(basketEntity -> {
          basketEntity.setPollingStartedAt(timestamp);
          basketRepository.save(basketEntity);
        }, () -> {
          var message = String.format(
              "Error while updating polling started at with basket id=%s.%s %s",
              basketId, BASKET_NOT_FOUND, basketId);
          var exception = new BasketNotFoundException(
              ErrorCode.UPDATE_POLLING_BASKET_NOT_FOUND_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        });
  }

  @Override
  public void deleteBasket(final String threeLetterHotelId, final String sortKey) {
    log.debug("Entering deleteBasket with hotelId={} and resNo={}", threeLetterHotelId, sortKey);
    basketRepository.deleteBasket(threeLetterHotelId, sortKey);
  }

  /**
   * BatchGetItem only works with the tables pk(partition key and optional sort key).
   * BatchGetItem will not work with secondary indexes (bookingReference)
   * When querying for multiple booking references the queries for
   * individual bookingReferences will be performed in parallel
   *
   * @param references list of booking references
   * @return list of baskets
   */
  @Override
  public List<Basket> getBasketsByReferences(List<String> references) {
    List<CompletableFuture<Optional<BasketEntity>>> futureList = references.stream()
        .map(reference -> CompletableFuture.supplyAsync(
            () -> basketRepository.getByReference(reference)))
        .toList();

    CompletableFuture<Void> allFutures = CompletableFuture.allOf(
        futureList.toArray(new CompletableFuture[0]));

    return allFutures.thenApply(v -> futureList.stream()
            .map(CompletableFuture::join)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(mapper::toDomainModel)
            .toList())
        .join();
  }

  @Override
  public Basket createBasketReservation(final CreateBasketRequest createBasketRequest) {
    log.debug("Entering create basket new Reservation with createBasketRequest={}", createBasketRequest);
    BasketEntity basketEntity = buildBaseBasketEntity(createBasketRequest);
    basketEntity.setItems(mapper.toBasketItemEntityModel(createBasketRequest.getBasketItems()));
    basketEntity.setItemTypes(mapper.toBasketItemTypeEntityModel(createBasketRequest.getBasketItemTypes()));
    basketRepository.save(basketEntity);
    return mapper.toDomainModel(basketEntity);
  }

  public void updateCharacterUdfs(final UdfsRequestDto characterUDFsDto) {
    log.info("update UDFC20 to CIOL_COMPLETED for reservationIds={}",
        characterUDFsDto.getReservationIds());
    ohipAdapterClient.updateCharacterUdfs(characterUDFsDto);
  }

  private BasketStatusEntity getBasketStatus(BasketStatus basketStatus) {
    return (basketStatus != null)
        ? BasketStatusEntity.valueOf(basketStatus.name()) : BasketStatusEntity.OPEN;
  }

  private String sanitizeString(String reference) {
    String sanitizedReference = StringUtils
        .normalizeSpace(reference);
    sanitizedReference = sanitizedReference.replaceAll("[^a-zA-Z0-9]", "_");
    return sanitizedReference;
  }

  private BasketEntity buildBaseBasketEntity(CreateBasketRequest createBasketRequest) {
    final var threeLetterHotelId = hotelInfoOutPort.getHotelInfo(createBasketRequest.getHotelId())
        .getThreeLetterId();
    final var sortKey = getSortKey(threeLetterHotelId);
    final var basketId = basketIdService.generateBasketId(threeLetterHotelId, sortKey);
    final var reference = getReference(threeLetterHotelId, createBasketRequest);
    final var paymentOption = createBasketRequest.getPaymentOption() != null
        ? createBasketRequest.getPaymentOption().name() : null;
    final var paymentId = createBasketRequest.getPaymentId();

    if (Objects.nonNull(createBasketRequest.getMigratedResNo())
        && basketRepository.getByBasketId(basketId).isPresent()) {
      var exception = new InvalidBasketIdException(
          ErrorCode.BASKET_EXISTS_EXCEPTION,
          "The provided basket id already exists");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    var basketStatus = getBasketStatus(createBasketRequest.getBasketStatus());
    var now = now();
    return BasketEntity.builder()
        .sortKey(sortKey)
        .basketId(basketId)
        .reference(reference)
        .threeLetterHotelId(threeLetterHotelId)
        .hotelId(createBasketRequest.getHotelId())
        .userId(createBasketRequest.getUserId())
        .originalBasketId(createBasketRequest.getOriginalBasketId())
        .linkAmendReservations(createBasketRequest.getLinkAmendReservations())
        .status(basketStatus)
        .paymentOption(paymentOption)
        .paymentID(paymentId)
        .channel(createBasketRequest.getChannel())
        .subChannel(createBasketRequest.getSubChannel())
        .sendMail(true)
        .idContext(createBasketRequest.getIdContext())
        .createdAt(now.toString())
        .cleanUpTime(cleanUpTime.getCleanUpTime(mapper.toBasketStatusModel(basketStatus), now))
        .build();

  }

  public List<ReservationInfoPaymentType> getPaymentType(String hotelId, Set<String> sourceIds) {
    return ohipAdapterClient.getPaymentType(hotelId, sourceIds);
  }

  public void postReservationPreregister(PreCheckInRequestDto preCheckInRequestDto) {
    log.info("log_pre_register_basket : Posting status for hotelId={} and reservationId {}",
        preCheckInRequestDto.getHotelId(), preCheckInRequestDto.getReservationId());
    ResponseEntity<PreCheckInResponse> response = ohipAdapterClient.saveReservationPreRegister(preCheckInRequestDto);
    if (response != null && response.getBody() != null) {
      log.info("log_pre_register_basket : hotelId={} reservationId={} status={} responseStatus={} message={}",
          preCheckInRequestDto.getHotelId(), preCheckInRequestDto.getReservationId(), response.getStatusCode(),
          response.getBody().getStatus(), response.getBody().getMessage());
    } else {
      log.warn("log_pre_register_basket : Null response or empty body for hotelId={} reservationId={}",
          preCheckInRequestDto.getHotelId(), preCheckInRequestDto.getReservationId());
    }
  }

  public ReservationByBasketRefResponseDto getReservationDetails(String hotelId, String sourceId) {
    return ohipAdapterClient.getReservationDetails(hotelId, sourceId);
  }

  private BasketPaymentStatusEntity getPaymentStatus(Basket basket) {
    if (basket.getPaymentStatus() == null) {
      return null;
    }
    try {
      return BasketPaymentStatusEntity.valueOf(basket.getPaymentStatus().name());
    } catch (IllegalArgumentException ex) {
      log.error("Invalid payment status: {}", basket.getPaymentStatus(), ex);
      return null;
    }
  }

  @Override
  public void updateBasketWithPaymentStatus(final Basket basket) {
    log.info("Entering update basket with payment status with reference={}, paymentId={} and paymentOption={}",
            basket.getBasketId(), basket.getPaymentID(), basket.getPaymentOption());
    basketRepository.getByBasketId(basket.getBasketId())
        .ifPresentOrElse(basketEntity -> {
          basketEntity.setPaymentID(basket.getPaymentID());
          basketEntity.setPaymentStatus(getPaymentStatus(basket));
          basketEntity.setPaymentOption(basket.getPaymentOption());
          basketEntity.setTotalCost(basket.getTotalCost());
          basketEntity.setCurrency(basket.getCurrency());
          basketEntity.setEmailAddress(basket.getEmailAddress());
          basketEntity.setPaymentChannel(basket.getPaymentChannel());
          basketEntity.setStatus(BasketStatusEntity.valueOf(basket.getStatus().name()));
          basketRepository.updateBasket(basketEntity);
        }, () -> {
          var message = String.format("Error while updating basket payment with "
                          + "reference=%s, paymentId=%s and paymentOption=%s.%s %s",
                  basket.getBasketId(), basket.getPaymentID(), basket.getPaymentOption(),
                  BASKET_NOT_FOUND, basket.getReference());
          var exception = new BasketNotFoundException(ErrorCode.UPDATE_BASKET_NOT_FOUND_EXCEPTION, message);
          ExceptionLogger.log(log, exception);
          throw exception;
        });
  }
}
