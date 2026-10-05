package uk.co.whitbread.content.infrastructure.rest.client.inn.business.header.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LayoutResponseAemDto {

  @JsonProperty("innbusinessLayout.manageAccount.cards.title")
  private String cardsTitle;

  @JsonProperty("innbusinessLayout.manageAccount.profile.text")
  private String profileText;

  @JsonProperty("innbusinessLayout.manageAccount.title")
  private String manageAccountTitle;

  @JsonProperty("innbusinessLayout.menu.spending.icon.active")
  private String menuSpendingIconActive;

  @JsonProperty("innbusinessLayout.help.faq.icon")
  private String helpFaqIcon;

  @JsonProperty("innbusinessLayout.menu.bookings.icon")
  private String menuBookingsIcon;

  @JsonProperty("innbusinessLayout.help.tour.icon")
  private String helpTourIcon;

  @JsonProperty("innbusinessLayout.menu.manage.options.allowances")
  private String menuManageOptionsAllowances;

  @JsonProperty("innbusinessLayout.menu.manage.options.cards")
  private String menuManageOptionsCards;

  @JsonProperty("innbusinessLayout.menu.manage.icon.active")
  private String menuManageIconActive;

  @JsonProperty("innbusinessLayout.manageAccount.profile.title")
  private String profileTitle;

  @JsonProperty("innbusinessLayout.menu.spending.label")
  private String menuSpendingLabel;

  @JsonProperty("innbusinessLayout.menu.manage.options.alerts")
  private String menuManageOptionsAlerts;

  @JsonProperty("innbusinessLayout.sidebar.collapse")
  private String sidebarCollapse;

  @JsonProperty("innbusinessLayout.menu.manage.options.company")
  private String menuManageOptionsCompany;

  @JsonProperty("innbusinessLayout.manageAccount.cards.icon")
  private String cardsIcon;

  @JsonProperty("innbusinessLayout.menu.manage.options.employees")
  private String menuManageOptionsEmployees;

  @JsonProperty("innbusinessLayout.menu.bookings.label")
  private String bookingsLabel;

  @JsonProperty("innbusinessLayout.manageAccount.employees.title")
  private String employeesTitle;

  @JsonProperty("innbusinessLayout.menu.bookings.icon.active")
  private String menuBookingsIconActive;

  @JsonProperty("innbusinessLayout.sidebar.expand")
  private String sidebarExpand;

  @JsonProperty("innbusinessLayout.manageAccount.cards.link")
  private String cardsLink;

  @JsonProperty("innbusinessLayout.help.faq.label")
  private String faqLabel;

  @JsonProperty("innbusinessLayout.manageAccount.employees.icon")
  private String employeesIcon;

  @JsonProperty("innbusinessLayout.manageAccount.employees.link")
  private String employeesLink;

  @JsonProperty("innbusinessLayout.manageAccount.profile.link")
  private String profileLink;

  @JsonProperty("innbusinessLayout.help.needHelp")
  private String needHelp;

  @JsonProperty("innbusinessLayout.menu.manage.icon")
  private String menuManageIcon;

  @JsonProperty("innbusinessLayout.menu.spending.icon")
  private String menuSpendingIcon;

  @JsonProperty("innbusinessLayout.menu.home.icon.active")
  private String homeIconActive;

  @JsonProperty("innbusinessLayout.help.contact.icon")
  private String helpContactIcon;

  @JsonProperty("innbusinessLayout.help.tour.label")
  private String helpTourLabel;

  @JsonProperty("innbusinessLayout.menu.manage.options.questions")
  private String menuManageOptionsQuestions;

  @JsonProperty("innbusinessLayout.help.contact.label")
  private String helpContactLabel;

  @JsonProperty("innbusinessLayout.menu.home.icon")
  private String homeIcon;

  @JsonProperty("innbusinessLayout.menu.home.label")
  private String homeLabel;

  @JsonProperty("innbusinessLayout.menu.manage.label")
  private String menuManageLabel;

  @JsonProperty("innbusinessLayout.menu.contact.icon")
  private String menuContactIcon;

  @JsonProperty("innbusinessLayout.menu.contact.label")
  private String menuContactLabel;

  @JsonProperty("innbusinessLayout.menu.contact.icon.active")
  private String menuContactIconActive;

}
