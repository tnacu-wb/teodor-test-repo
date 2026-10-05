package uk.co.whitbread.content.domain.model.inn.business.cardmanagement.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardManagementContent {

  private String title;
  private Tabs tabs;
  private ApplyBanner applyBanner;
  private CreditBox creditBox;
  private ExpenseBox expenseBox;
  private InvoicesBox invoicesBox;
  private LinkAccountBanner linkAccountBanner;
  private CardManagement cardManagement;
  private Filters filters;
  private Columns columns;
  private CardHolder cardHolder;
  private CardStatus cardStatus;
  private Download download;
  private NewCard newCard;
  private Badge badge;

}
