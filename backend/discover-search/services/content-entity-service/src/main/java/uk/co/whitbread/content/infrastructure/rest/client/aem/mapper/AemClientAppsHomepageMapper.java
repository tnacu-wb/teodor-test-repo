package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.apps.homepage.in.AppsHomepageRequest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageContentCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageDestinationCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageResponse;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsNotification;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.in.AppsHomepageRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsHomepageCardDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsHomepageResponseDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsNotificationDto;

@Mapper(componentModel = "spring")
public interface AemClientAppsHomepageMapper {

  Logger log = LogManager.getLogger(AemClientAppsHomepageMapper.class);

  AppsHomepageRequestDto toDto(AppsHomepageRequest request);

  @Mapping(target = "destinationCards",
      expression = "java(toValidDestinationCardModel(response.getDestinationCards()))")
  @Mapping(target = "promoCards", expression = "java(toValidCardModel(response.getPromoCards()))")
  @Mapping(target = "contentCards", expression = "java(toValidContentCardModel(response.getContentCards()))")
  @Mapping(target = "notification", expression = "java(toValidNotificationModel(response.getNotification()))")
  AppsHomepageResponse toDomainModel(AppsHomepageResponseDto response);

  default List<AppsHomepageDestinationCard> toValidDestinationCardModel(
      List<AppsHomepageCardDto> appsCardDto) {
    Predicate<AppsHomepageCardDto> validCardPredicate = cardDto -> isValidCard(
        cardDto.getImagePath(), cardDto.getTitle(), cardDto.getOrder());

    Map<Boolean, List<AppsHomepageCardDto>> map = appsCardDto.stream()
        .collect(Collectors.partitioningBy(validCardPredicate));

    map.get(false).stream()
        .forEach(card -> log.warn(
            String.format("Missing the imagePath, title or order for the card: %s",
                card.toString())));

    return map.get(true).stream()
        .map(this::toDestinationCardModel)
        .toList();
  }

  default List<AppsHomepageCard> toValidCardModel(List<AppsHomepageCardDto> appsCardDto) {
    Predicate<AppsHomepageCardDto> validCardPredicate = cardDto -> isValidCard(
        cardDto.getImagePath(), cardDto.getTitle(), cardDto.getOrder());

    Map<Boolean, List<AppsHomepageCardDto>> map = appsCardDto.stream()
        .collect(Collectors.partitioningBy(validCardPredicate));

    map.get(false).stream()
        .forEach(card -> log.warn(
            String.format("Missing the imagePath, title or order for the card: %s",
                card.toString())));

    return map.get(true).stream()
        .map(this::toCardModel)
        .toList();
  }

  default List<AppsHomepageContentCard> toValidContentCardModel(
      List<AppsHomepageCardDto> appsCardDto) {

    Predicate<AppsHomepageCardDto> pred = cardDto -> isValidContentCard(
        cardDto.getImagePath(), cardDto.getOrder());

    Map<Boolean, List<AppsHomepageCardDto>> map = appsCardDto.stream()
        .collect(Collectors.partitioningBy(pred));

    map.get(false).stream()
        .forEach(card -> log.warn(
            String.format("Missing the imagePath or order for the card: %s",
                card.toString())));

    return map.get(true).stream()
        .map(this::toContentCardModel)
        .toList();
  }

  default AppsNotification toValidNotificationModel(AppsNotificationDto notificationDto) {
    if (!Objects.isNull(notificationDto)) {
      if (isValidNotification(notificationDto)) {
        return toNotificationModel(notificationDto);
      }
      log.warn(
          String.format("Missing the type or message for the notification: %s", notificationDto));
    }
    return null;
  }

  AppsHomepageCard toCardModel(AppsHomepageCardDto request);

  AppsHomepageDestinationCard toDestinationCardModel(AppsHomepageCardDto request);

  AppsHomepageContentCard toContentCardModel(AppsHomepageCardDto request);

  AppsNotification toNotificationModel(AppsNotificationDto request);

  private boolean isValidCard(String pathImage, String title, Integer order) {
    return isValidContentCard(pathImage, order) && !Strings.isBlank(title);
  }

  private boolean isValidContentCard(String pathImage, Integer order) {

    return !Strings.isBlank(pathImage) && Objects.nonNull(order);
  }

  private boolean isValidNotification(AppsNotificationDto dto) {
    return !Strings.isBlank(dto.getType()) && !Strings.isBlank(dto.getMessage());
  }
}
