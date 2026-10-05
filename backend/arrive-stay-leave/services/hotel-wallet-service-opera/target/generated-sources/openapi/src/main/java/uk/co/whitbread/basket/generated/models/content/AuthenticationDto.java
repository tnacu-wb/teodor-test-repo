package uk.co.whitbread.basket.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.basket.generated.models.content.AccountLinkDto;
import uk.co.whitbread.basket.generated.models.content.BusinessDto;
import uk.co.whitbread.basket.generated.models.content.ForgottenPasswordDto;
import uk.co.whitbread.basket.generated.models.content.LoginDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * AuthenticationDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-04T08:11:24.587145+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class AuthenticationDto {

  @Valid
  private List<@Valid AccountLinkDto> accountLinks = new ArrayList<>();

  private @Nullable BusinessDto business;

  private @Nullable ForgottenPasswordDto forgottenPassword;

  private @Nullable LoginDto login;

  private @Nullable String logoutButton;

  public AuthenticationDto accountLinks(List<@Valid AccountLinkDto> accountLinks) {
    this.accountLinks = accountLinks;
    return this;
  }

  public AuthenticationDto addAccountLinksItem(AccountLinkDto accountLinksItem) {
    if (this.accountLinks == null) {
      this.accountLinks = new ArrayList<>();
    }
    this.accountLinks.add(accountLinksItem);
    return this;
  }

  /**
   * Get accountLinks
   * @return accountLinks
   */
  @Valid 
  @Schema(name = "accountLinks", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("accountLinks")
  public List<@Valid AccountLinkDto> getAccountLinks() {
    return accountLinks;
  }

  public void setAccountLinks(List<@Valid AccountLinkDto> accountLinks) {
    this.accountLinks = accountLinks;
  }

  public AuthenticationDto business(BusinessDto business) {
    this.business = business;
    return this;
  }

  /**
   * Get business
   * @return business
   */
  @Valid 
  @Schema(name = "business", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("business")
  public BusinessDto getBusiness() {
    return business;
  }

  public void setBusiness(BusinessDto business) {
    this.business = business;
  }

  public AuthenticationDto forgottenPassword(ForgottenPasswordDto forgottenPassword) {
    this.forgottenPassword = forgottenPassword;
    return this;
  }

  /**
   * Get forgottenPassword
   * @return forgottenPassword
   */
  @Valid 
  @Schema(name = "forgottenPassword", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("forgottenPassword")
  public ForgottenPasswordDto getForgottenPassword() {
    return forgottenPassword;
  }

  public void setForgottenPassword(ForgottenPasswordDto forgottenPassword) {
    this.forgottenPassword = forgottenPassword;
  }

  public AuthenticationDto login(LoginDto login) {
    this.login = login;
    return this;
  }

  /**
   * Get login
   * @return login
   */
  @Valid 
  @Schema(name = "login", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("login")
  public LoginDto getLogin() {
    return login;
  }

  public void setLogin(LoginDto login) {
    this.login = login;
  }

  public AuthenticationDto logoutButton(String logoutButton) {
    this.logoutButton = logoutButton;
    return this;
  }

  /**
   * Get logoutButton
   * @return logoutButton
   */
  
  @Schema(name = "logoutButton", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("logoutButton")
  public String getLogoutButton() {
    return logoutButton;
  }

  public void setLogoutButton(String logoutButton) {
    this.logoutButton = logoutButton;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationDto authenticationDto = (AuthenticationDto) o;
    return Objects.equals(this.accountLinks, authenticationDto.accountLinks) &&
        Objects.equals(this.business, authenticationDto.business) &&
        Objects.equals(this.forgottenPassword, authenticationDto.forgottenPassword) &&
        Objects.equals(this.login, authenticationDto.login) &&
        Objects.equals(this.logoutButton, authenticationDto.logoutButton);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountLinks, business, forgottenPassword, login, logoutButton);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationDto {\n");
    sb.append("    accountLinks: ").append(toIndentedString(accountLinks)).append("\n");
    sb.append("    business: ").append(toIndentedString(business)).append("\n");
    sb.append("    forgottenPassword: ").append(toIndentedString(forgottenPassword)).append("\n");
    sb.append("    login: ").append(toIndentedString(login)).append("\n");
    sb.append("    logoutButton: ").append(toIndentedString(logoutButton)).append("\n");
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

