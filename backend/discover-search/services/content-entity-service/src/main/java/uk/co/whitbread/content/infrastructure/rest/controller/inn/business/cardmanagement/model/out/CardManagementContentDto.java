package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardManagementContentDto {

  private String title;
  private TabsDto tabs;
  private ApplyBannerDto applyBanner;
  private CreditBoxDto creditBox;
  private ExpenseBoxDto expenseBox;
  private InvoicesBoxDto invoicesBox;
  private LinkAccountBannerDto linkAccountBanner;
  private CardManagementDto cardManagement;
  private FiltersDto filters;
  private ColumnsDto columns;
  private CardHolderDto cardHolder;
  private CardStatusDto cardStatus;
  private DownloadDto download;
  private NewCardDto newCard;
  private BadgeDto badge;

}
