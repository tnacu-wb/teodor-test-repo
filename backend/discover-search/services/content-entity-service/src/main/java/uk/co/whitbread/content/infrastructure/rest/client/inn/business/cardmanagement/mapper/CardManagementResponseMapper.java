package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out.CardManagementContent;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in.CardManagementContentResponseAemDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CardManagementResponseMapper {

  @Mapping(source = "filtersMyCards", target = "filters.myCards")
  @Mapping(source = "applyBannerImage", target = "applyBanner.image")
  @Mapping(source = "cardManagementTitle", target = "cardManagement.title")
  @Mapping(source = "linkAccountBannerLinkAccountButton", target = "linkAccountBanner.linkAccountButton")
  @Mapping(source = "applyBannerSubtitle", target = "applyBanner.subtitle")
  @Mapping(source = "newCardLabel", target = "newCard.label")
  @Mapping(source = "linkAccountBannerSubtitle", target = "linkAccountBanner.subtitle")
  @Mapping(source = "expenseBoxTitle", target = "expenseBox.title")
  @Mapping(source = "cardStatusOptionsActivate", target = "cardStatus.cardStatusOptions.activate")
  @Mapping(source = "cardStatusOptionsCancelled", target = "cardStatus.cardStatusOptions.cancelled")
  @Mapping(source = "filtersShow", target = "filters.show")
  @Mapping(source = "columnsYourCard", target = "columns.yourCard")
  @Mapping(source = "newCardIcon", target = "newCard.icon")
  @Mapping(source = "cardStatusOptionsDispatching", target = "cardStatus.cardStatusOptions.dispatching")
  @Mapping(source = "applyBannerLinkAccountButton", target = "applyBanner.linkAccountButton")
  @Mapping(source = "title", target = "title")
  @Mapping(source = "columnsCardHolderName", target = "columns.cardHolderName")
  @Mapping(source = "applyBannerApplyNowButton", target = "applyBanner.applyNowButton")
  @Mapping(source = "tabsCentrallyStoredDescription", target = "tabs.centrallyStored.description")
  @Mapping(source = "tabsCentrallyStored", target = "tabs.centrallyStoredTitle")
  @Mapping(source = "badgeAccountHolder", target = "badge.accountHolder")
  @Mapping(source = "applyBannerTitle", target = "applyBanner.title")
  @Mapping(source = "tabsInnBusinessPay", target = "tabs.innBusinessPay")
  @Mapping(source = "invoicesBoxTitle", target = "invoicesBox.title")
  @Mapping(source = "downloadLabel", target = "download.label")
  @Mapping(source = "invoicesBoxSubtitle", target = "invoicesBox.subtitle")
  @Mapping(source = "columnsCardStatus", target = "columns.cardStatus")
  @Mapping(source = "columnsEdit", target = "columns.edit")
  @Mapping(source = "columnsCardNumber", target = "columns.cardNumber")
  @Mapping(source = "invoicesBoxIcon", target = "invoicesBox.icon")
  @Mapping(source = "columnsCardHolderRegistered", target = "columns.cardHolderRegistered")
  @Mapping(source = "expenseBoxSubtitle", target = "expenseBox.subtitle")
  @Mapping(source = "creditBoxSubtitle", target = "creditBox.subtitle")
  @Mapping(source = "cardHolderOptionsResendCode", target = "cardHolder.cardHolderOptions.resendCode")
  @Mapping(source = "filtersCancelledCards", target = "filters.cancelledCards")
  @Mapping(source = "cardStatusOptionsActive", target = "cardStatus.cardStatusOptions.active")
  @Mapping(source = "creditBoxIcon", target = "creditBox.icon")
  @Mapping(source = "downloadIcon", target = "download.icon")
  @Mapping(source = "creditBoxTitle", target = "creditBox.title")
  @Mapping(source = "cardHolderOptionsRegistered", target = "cardHolder.cardHolderOptions.registered")
  @Mapping(source = "linkAccountBannerTitle", target = "linkAccountBanner.title")
  @Mapping(source = "expenseBoxIcon", target = "expenseBox.icon")
  @Mapping(source = "cardManagementInfo", target = "cardManagement.info")
  @Mapping(source = "cardStatusOptionsExpired", target = "cardStatus.cardStatusOptions.expired")
  @Mapping(source = "columnsCardId", target = "columns.cardId")
  @Mapping(source = "columnsCardLabel", target = "columns.cardLabel")
  @Mapping(source = "columnsCardExpiry", target = "columns.expiry")
  CardManagementContent toModel(CardManagementContentResponseAemDto cardManagementContentResponseAemDto);

}
