package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.ApplyBannerDto;
import uk.co.whitbread.content.entity.service.generated.models.content.BadgeDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CardHolderDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CardManagementDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CardStatusDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ColumnsDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CreditBoxDto;
import uk.co.whitbread.content.entity.service.generated.models.content.DownloadDto;
import uk.co.whitbread.content.entity.service.generated.models.content.ExpenseBoxDto;
import uk.co.whitbread.content.entity.service.generated.models.content.FiltersDto;
import uk.co.whitbread.content.entity.service.generated.models.content.InvoicesBoxDto;
import uk.co.whitbread.content.entity.service.generated.models.content.LinkAccountBannerDto;
import uk.co.whitbread.content.entity.service.generated.models.content.NewCardDto;
import uk.co.whitbread.content.entity.service.generated.models.content.TabsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CardManagementContentDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardManagementContentDto {

  private @Nullable ApplyBannerDto applyBanner;

  private @Nullable BadgeDto badge;

  private @Nullable CardHolderDto cardHolder;

  private @Nullable CardManagementDto cardManagement;

  private @Nullable CardStatusDto cardStatus;

  private @Nullable ColumnsDto columns;

  private @Nullable CreditBoxDto creditBox;

  private @Nullable DownloadDto download;

  private @Nullable ExpenseBoxDto expenseBox;

  private @Nullable FiltersDto filters;

  private @Nullable InvoicesBoxDto invoicesBox;

  private @Nullable LinkAccountBannerDto linkAccountBanner;

  private @Nullable NewCardDto newCard;

  private @Nullable TabsDto tabs;

  private @Nullable String title;

  public CardManagementContentDto applyBanner(ApplyBannerDto applyBanner) {
    this.applyBanner = applyBanner;
    return this;
  }

  /**
   * Get applyBanner
   * @return applyBanner
   */
  @Valid 
  @Schema(name = "applyBanner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("applyBanner")
  public ApplyBannerDto getApplyBanner() {
    return applyBanner;
  }

  public void setApplyBanner(ApplyBannerDto applyBanner) {
    this.applyBanner = applyBanner;
  }

  public CardManagementContentDto badge(BadgeDto badge) {
    this.badge = badge;
    return this;
  }

  /**
   * Get badge
   * @return badge
   */
  @Valid 
  @Schema(name = "badge", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("badge")
  public BadgeDto getBadge() {
    return badge;
  }

  public void setBadge(BadgeDto badge) {
    this.badge = badge;
  }

  public CardManagementContentDto cardHolder(CardHolderDto cardHolder) {
    this.cardHolder = cardHolder;
    return this;
  }

  /**
   * Get cardHolder
   * @return cardHolder
   */
  @Valid 
  @Schema(name = "cardHolder", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardHolder")
  public CardHolderDto getCardHolder() {
    return cardHolder;
  }

  public void setCardHolder(CardHolderDto cardHolder) {
    this.cardHolder = cardHolder;
  }

  public CardManagementContentDto cardManagement(CardManagementDto cardManagement) {
    this.cardManagement = cardManagement;
    return this;
  }

  /**
   * Get cardManagement
   * @return cardManagement
   */
  @Valid 
  @Schema(name = "cardManagement", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardManagement")
  public CardManagementDto getCardManagement() {
    return cardManagement;
  }

  public void setCardManagement(CardManagementDto cardManagement) {
    this.cardManagement = cardManagement;
  }

  public CardManagementContentDto cardStatus(CardStatusDto cardStatus) {
    this.cardStatus = cardStatus;
    return this;
  }

  /**
   * Get cardStatus
   * @return cardStatus
   */
  @Valid 
  @Schema(name = "cardStatus", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardStatus")
  public CardStatusDto getCardStatus() {
    return cardStatus;
  }

  public void setCardStatus(CardStatusDto cardStatus) {
    this.cardStatus = cardStatus;
  }

  public CardManagementContentDto columns(ColumnsDto columns) {
    this.columns = columns;
    return this;
  }

  /**
   * Get columns
   * @return columns
   */
  @Valid 
  @Schema(name = "columns", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("columns")
  public ColumnsDto getColumns() {
    return columns;
  }

  public void setColumns(ColumnsDto columns) {
    this.columns = columns;
  }

  public CardManagementContentDto creditBox(CreditBoxDto creditBox) {
    this.creditBox = creditBox;
    return this;
  }

  /**
   * Get creditBox
   * @return creditBox
   */
  @Valid 
  @Schema(name = "creditBox", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("creditBox")
  public CreditBoxDto getCreditBox() {
    return creditBox;
  }

  public void setCreditBox(CreditBoxDto creditBox) {
    this.creditBox = creditBox;
  }

  public CardManagementContentDto download(DownloadDto download) {
    this.download = download;
    return this;
  }

  /**
   * Get download
   * @return download
   */
  @Valid 
  @Schema(name = "download", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("download")
  public DownloadDto getDownload() {
    return download;
  }

  public void setDownload(DownloadDto download) {
    this.download = download;
  }

  public CardManagementContentDto expenseBox(ExpenseBoxDto expenseBox) {
    this.expenseBox = expenseBox;
    return this;
  }

  /**
   * Get expenseBox
   * @return expenseBox
   */
  @Valid 
  @Schema(name = "expenseBox", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("expenseBox")
  public ExpenseBoxDto getExpenseBox() {
    return expenseBox;
  }

  public void setExpenseBox(ExpenseBoxDto expenseBox) {
    this.expenseBox = expenseBox;
  }

  public CardManagementContentDto filters(FiltersDto filters) {
    this.filters = filters;
    return this;
  }

  /**
   * Get filters
   * @return filters
   */
  @Valid 
  @Schema(name = "filters", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("filters")
  public FiltersDto getFilters() {
    return filters;
  }

  public void setFilters(FiltersDto filters) {
    this.filters = filters;
  }

  public CardManagementContentDto invoicesBox(InvoicesBoxDto invoicesBox) {
    this.invoicesBox = invoicesBox;
    return this;
  }

  /**
   * Get invoicesBox
   * @return invoicesBox
   */
  @Valid 
  @Schema(name = "invoicesBox", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("invoicesBox")
  public InvoicesBoxDto getInvoicesBox() {
    return invoicesBox;
  }

  public void setInvoicesBox(InvoicesBoxDto invoicesBox) {
    this.invoicesBox = invoicesBox;
  }

  public CardManagementContentDto linkAccountBanner(LinkAccountBannerDto linkAccountBanner) {
    this.linkAccountBanner = linkAccountBanner;
    return this;
  }

  /**
   * Get linkAccountBanner
   * @return linkAccountBanner
   */
  @Valid 
  @Schema(name = "linkAccountBanner", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("linkAccountBanner")
  public LinkAccountBannerDto getLinkAccountBanner() {
    return linkAccountBanner;
  }

  public void setLinkAccountBanner(LinkAccountBannerDto linkAccountBanner) {
    this.linkAccountBanner = linkAccountBanner;
  }

  public CardManagementContentDto newCard(NewCardDto newCard) {
    this.newCard = newCard;
    return this;
  }

  /**
   * Get newCard
   * @return newCard
   */
  @Valid 
  @Schema(name = "newCard", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("newCard")
  public NewCardDto getNewCard() {
    return newCard;
  }

  public void setNewCard(NewCardDto newCard) {
    this.newCard = newCard;
  }

  public CardManagementContentDto tabs(TabsDto tabs) {
    this.tabs = tabs;
    return this;
  }

  /**
   * Get tabs
   * @return tabs
   */
  @Valid 
  @Schema(name = "tabs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tabs")
  public TabsDto getTabs() {
    return tabs;
  }

  public void setTabs(TabsDto tabs) {
    this.tabs = tabs;
  }

  public CardManagementContentDto title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardManagementContentDto cardManagementContentDto = (CardManagementContentDto) o;
    return Objects.equals(this.applyBanner, cardManagementContentDto.applyBanner) &&
        Objects.equals(this.badge, cardManagementContentDto.badge) &&
        Objects.equals(this.cardHolder, cardManagementContentDto.cardHolder) &&
        Objects.equals(this.cardManagement, cardManagementContentDto.cardManagement) &&
        Objects.equals(this.cardStatus, cardManagementContentDto.cardStatus) &&
        Objects.equals(this.columns, cardManagementContentDto.columns) &&
        Objects.equals(this.creditBox, cardManagementContentDto.creditBox) &&
        Objects.equals(this.download, cardManagementContentDto.download) &&
        Objects.equals(this.expenseBox, cardManagementContentDto.expenseBox) &&
        Objects.equals(this.filters, cardManagementContentDto.filters) &&
        Objects.equals(this.invoicesBox, cardManagementContentDto.invoicesBox) &&
        Objects.equals(this.linkAccountBanner, cardManagementContentDto.linkAccountBanner) &&
        Objects.equals(this.newCard, cardManagementContentDto.newCard) &&
        Objects.equals(this.tabs, cardManagementContentDto.tabs) &&
        Objects.equals(this.title, cardManagementContentDto.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applyBanner, badge, cardHolder, cardManagement, cardStatus, columns, creditBox, download, expenseBox, filters, invoicesBox, linkAccountBanner, newCard, tabs, title);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardManagementContentDto {\n");
    sb.append("    applyBanner: ").append(toIndentedString(applyBanner)).append("\n");
    sb.append("    badge: ").append(toIndentedString(badge)).append("\n");
    sb.append("    cardHolder: ").append(toIndentedString(cardHolder)).append("\n");
    sb.append("    cardManagement: ").append(toIndentedString(cardManagement)).append("\n");
    sb.append("    cardStatus: ").append(toIndentedString(cardStatus)).append("\n");
    sb.append("    columns: ").append(toIndentedString(columns)).append("\n");
    sb.append("    creditBox: ").append(toIndentedString(creditBox)).append("\n");
    sb.append("    download: ").append(toIndentedString(download)).append("\n");
    sb.append("    expenseBox: ").append(toIndentedString(expenseBox)).append("\n");
    sb.append("    filters: ").append(toIndentedString(filters)).append("\n");
    sb.append("    invoicesBox: ").append(toIndentedString(invoicesBox)).append("\n");
    sb.append("    linkAccountBanner: ").append(toIndentedString(linkAccountBanner)).append("\n");
    sb.append("    newCard: ").append(toIndentedString(newCard)).append("\n");
    sb.append("    tabs: ").append(toIndentedString(tabs)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

