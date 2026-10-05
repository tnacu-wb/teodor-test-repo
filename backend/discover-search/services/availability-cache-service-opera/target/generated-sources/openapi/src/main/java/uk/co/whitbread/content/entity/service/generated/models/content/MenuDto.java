package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * MenuDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:49:41.570079+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class MenuDto {

  private @Nullable String agentMemo;

  private @Nullable String bookHotel;

  private @Nullable String business;

  private @Nullable String changeLogs;

  private @Nullable String discoverPI;

  private @Nullable String findBooking;

  private @Nullable String guestAccount;

  private @Nullable String language;

  private @Nullable String languageButton;

  private @Nullable String logIn;

  private @Nullable String mobileMenuButton;

  private @Nullable String tick;

  public MenuDto agentMemo(String agentMemo) {
    this.agentMemo = agentMemo;
    return this;
  }

  /**
   * Get agentMemo
   * @return agentMemo
   */
  
  @Schema(name = "agentMemo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("agentMemo")
  public String getAgentMemo() {
    return agentMemo;
  }

  public void setAgentMemo(String agentMemo) {
    this.agentMemo = agentMemo;
  }

  public MenuDto bookHotel(String bookHotel) {
    this.bookHotel = bookHotel;
    return this;
  }

  /**
   * Get bookHotel
   * @return bookHotel
   */
  
  @Schema(name = "bookHotel", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookHotel")
  public String getBookHotel() {
    return bookHotel;
  }

  public void setBookHotel(String bookHotel) {
    this.bookHotel = bookHotel;
  }

  public MenuDto business(String business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public String getBusiness() {
    return business;
  }

  public void setBusiness(String business) {
    this.business = business;
  }

  public MenuDto changeLogs(String changeLogs) {
    this.changeLogs = changeLogs;
    return this;
  }

  /**
   * Get changeLogs
   * @return changeLogs
   */
  
  @Schema(name = "changeLogs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("changeLogs")
  public String getChangeLogs() {
    return changeLogs;
  }

  public void setChangeLogs(String changeLogs) {
    this.changeLogs = changeLogs;
  }

  public MenuDto discoverPI(String discoverPI) {
    this.discoverPI = discoverPI;
    return this;
  }

  /**
   * Get discoverPI
   * @return discoverPI
   */
  
  @Schema(name = "discoverPI", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("discoverPI")
  public String getDiscoverPI() {
    return discoverPI;
  }

  public void setDiscoverPI(String discoverPI) {
    this.discoverPI = discoverPI;
  }

  public MenuDto findBooking(String findBooking) {
    this.findBooking = findBooking;
    return this;
  }

  /**
   * Get findBooking
   * @return findBooking
   */
  
  @Schema(name = "findBooking", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("findBooking")
  public String getFindBooking() {
    return findBooking;
  }

  public void setFindBooking(String findBooking) {
    this.findBooking = findBooking;
  }

  public MenuDto guestAccount(String guestAccount) {
    this.guestAccount = guestAccount;
    return this;
  }

  /**
   * Get guestAccount
   * @return guestAccount
   */
  
  @Schema(name = "guestAccount", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("guestAccount")
  public String getGuestAccount() {
    return guestAccount;
  }

  public void setGuestAccount(String guestAccount) {
    this.guestAccount = guestAccount;
  }

  public MenuDto language(String language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   */
  
  @Schema(name = "language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public MenuDto languageButton(String languageButton) {
    this.languageButton = languageButton;
    return this;
  }

  /**
   * Get languageButton
   * @return languageButton
   */
  
  @Schema(name = "languageButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("languageButton")
  public String getLanguageButton() {
    return languageButton;
  }

  public void setLanguageButton(String languageButton) {
    this.languageButton = languageButton;
  }

  public MenuDto logIn(String logIn) {
    this.logIn = logIn;
    return this;
  }

  /**
   * Get logIn
   * @return logIn
   */
  
  @Schema(name = "logIn", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logIn")
  public String getLogIn() {
    return logIn;
  }

  public void setLogIn(String logIn) {
    this.logIn = logIn;
  }

  public MenuDto mobileMenuButton(String mobileMenuButton) {
    this.mobileMenuButton = mobileMenuButton;
    return this;
  }

  /**
   * Get mobileMenuButton
   * @return mobileMenuButton
   */
  
  @Schema(name = "mobileMenuButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mobileMenuButton")
  public String getMobileMenuButton() {
    return mobileMenuButton;
  }

  public void setMobileMenuButton(String mobileMenuButton) {
    this.mobileMenuButton = mobileMenuButton;
  }

  public MenuDto tick(String tick) {
    this.tick = tick;
    return this;
  }

  /**
   * Get tick
   * @return tick
   */
  
  @Schema(name = "tick", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tick")
  public String getTick() {
    return tick;
  }

  public void setTick(String tick) {
    this.tick = tick;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MenuDto menuDto = (MenuDto) o;
    return Objects.equals(this.agentMemo, menuDto.agentMemo) &&
        Objects.equals(this.bookHotel, menuDto.bookHotel) &&
        Objects.equals(this.business, menuDto.business) &&
        Objects.equals(this.changeLogs, menuDto.changeLogs) &&
        Objects.equals(this.discoverPI, menuDto.discoverPI) &&
        Objects.equals(this.findBooking, menuDto.findBooking) &&
        Objects.equals(this.guestAccount, menuDto.guestAccount) &&
        Objects.equals(this.language, menuDto.language) &&
        Objects.equals(this.languageButton, menuDto.languageButton) &&
        Objects.equals(this.logIn, menuDto.logIn) &&
        Objects.equals(this.mobileMenuButton, menuDto.mobileMenuButton) &&
        Objects.equals(this.tick, menuDto.tick);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agentMemo, bookHotel, business, changeLogs, discoverPI, findBooking, guestAccount, language, languageButton, logIn, mobileMenuButton, tick);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MenuDto {\n");
    sb.append("    agentMemo: ").append(toIndentedString(agentMemo)).append("\n");
    sb.append("    bookHotel: ").append(toIndentedString(bookHotel)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    changeLogs: ").append(toIndentedString(changeLogs)).append("\n");
    sb.append("    discoverPI: ").append(toIndentedString(discoverPI)).append("\n");
    sb.append("    findBooking: ").append(toIndentedString(findBooking)).append("\n");
    sb.append("    guestAccount: ").append(toIndentedString(guestAccount)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    languageButton: ").append(toIndentedString(languageButton)).append("\n");
    sb.append("    logIn: ").append(toIndentedString(logIn)).append("\n");
    sb.append("    mobileMenuButton: ").append(toIndentedString(mobileMenuButton)).append("\n");
    sb.append("    tick: ").append(toIndentedString(tick)).append("\n");
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

