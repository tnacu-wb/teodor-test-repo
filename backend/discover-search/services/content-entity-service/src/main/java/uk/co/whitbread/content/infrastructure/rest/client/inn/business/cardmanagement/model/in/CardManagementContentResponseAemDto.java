package uk.co.whitbread.content.infrastructure.rest.client.inn.business.cardmanagement.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out.CardManagementContentDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardManagementContentResponseAemDto {

  private CardManagementContentDto cardMgmt;

  @JsonProperty("cardMgmt.filters.myCards")
  private String filtersMyCards;

  @JsonProperty("cardMgmt.applyBanner.image")
  private String applyBannerImage;

  @JsonProperty("cardMgmt.cardManagement.title")
  private String cardManagementTitle;

  @JsonProperty("cardMgmt.linkAccountBanner.linkAccountButton")
  private String linkAccountBannerLinkAccountButton;

  @JsonProperty("cardMgmt.applyBanner.subtitle")
  private String applyBannerSubtitle;

  @JsonProperty("cardMgmt.newCard.label")
  private String newCardLabel;

  @JsonProperty("cardMgmt.newCard.icon")
  private String newCardIcon;

  @JsonProperty("cardMgmt.linkAccountBanner.subtitle")
  private String linkAccountBannerSubtitle;

  @JsonProperty("cardMgmt.expenseBox.title")
  private String expenseBoxTitle;

  @JsonProperty("cardMgmt.cardStatus.options.activate")
  private String cardStatusOptionsActivate;

  @JsonProperty("cardMgmt.cardStatus.options.cancelled")
  private String cardStatusOptionsCancelled;

  @JsonProperty("cardMgmt.filters.show")
  private String filtersShow;

  @JsonProperty("cardMgmt.columns.yourCard")
  private String columnsYourCard;

  @JsonProperty("cardMgmt.cardStatus.options.dispatching")
  private String cardStatusOptionsDispatching;

  @JsonProperty("cardMgmt.applyBanner.linkAccountButton")
  private String applyBannerLinkAccountButton;

  @JsonProperty("cardMgmt.title")
  private String title;

  @JsonProperty("cardMgmt.columns.cardId")
  private String columnsCardId;

  @JsonProperty("cardMgmt.columns.cardLabel")
  private String columnsCardLabel;

  @JsonProperty("cardMgmt.columns.expiry")
  private String columnsCardExpiry;

  @JsonProperty("cardMgmt.columns.cardHolderName")
  private String columnsCardHolderName;

  @JsonProperty("cardMgmt.applyBanner.applyNowButton")
  private String applyBannerApplyNowButton;

  @JsonProperty("cardMgmt.tabs.centrallyStored")
  private String tabsCentrallyStored;

  @JsonProperty("cardMgmt.tabs.centrallyStored.description")
  private String tabsCentrallyStoredDescription;

  @JsonProperty("cardMgmt.badge.accountHolder")
  private String badgeAccountHolder;

  @JsonProperty("cardMgmt.applyBanner.title")
  private String applyBannerTitle;

  @JsonProperty("cardMgmt.tabs.innBusinessPay")
  private String tabsInnBusinessPay;

  @JsonProperty("cardMgmt.invoicesBox.title")
  private String invoicesBoxTitle;

  @JsonProperty("cardMgmt.download.label")
  private String downloadLabel;

  @JsonProperty("cardMgmt.invoicesBox.subtitle")
  private String invoicesBoxSubtitle;

  @JsonProperty("cardMgmt.columns.cardStatus")
  private String columnsCardStatus;

  @JsonProperty("cardMgmt.columns.edit")
  private String columnsEdit;

  @JsonProperty("cardMgmt.columns.cardNumber")
  private String columnsCardNumber;

  @JsonProperty("cardMgmt.invoicesBox.icon")
  private String invoicesBoxIcon;

  @JsonProperty("cardMgmt.columns.cardHolderRegistered")
  private String columnsCardHolderRegistered;

  @JsonProperty("cardMgmt.expenseBox.subtitle")
  private String expenseBoxSubtitle;

  @JsonProperty("cardMgmt.creditBox.subtitle")
  private String creditBoxSubtitle;

  @JsonProperty("cardMgmt.cardHolder.options.resendCode")
  private String cardHolderOptionsResendCode;

  @JsonProperty("cardMgmt.filters.cancelledCards")
  private String filtersCancelledCards;

  @JsonProperty("cardMgmt.cardStatus.options.active")
  private String cardStatusOptionsActive;

  @JsonProperty("cardMgmt.creditBox.icon")
  private String creditBoxIcon;

  @JsonProperty("cardMgmt.download.icon")
  private String downloadIcon;

  @JsonProperty("cardMgmt.creditBox.title")
  private String creditBoxTitle;

  @JsonProperty("cardMgmt.cardHolder.options.registered")
  private String cardHolderOptionsRegistered;

  @JsonProperty("cardMgmt.linkAccountBanner.title")
  private String linkAccountBannerTitle;

  @JsonProperty("cardMgmt.expenseBox.icon")
  private String expenseBoxIcon;

  @JsonProperty("cardMgmt.cardManagement.info")
  private String cardManagementInfo;

  @JsonProperty("cardMgmt.cardStatus.options.expired")
  private String cardStatusOptionsExpired;

}
