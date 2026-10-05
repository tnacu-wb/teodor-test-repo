package uk.co.whitbread.content.entity.service.generated.models.content;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import uk.co.whitbread.content.entity.service.generated.models.content.CardManagementContentDto;
import uk.co.whitbread.content.entity.service.generated.models.content.CommonIconsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * CardManagementResponseDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-27T18:51:28.563902+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class CardManagementResponseDto {

  private @Nullable CardManagementContentDto cardManagementContent;

  private @Nullable CommonIconsDto commonIcons;

  public CardManagementResponseDto cardManagementContent(CardManagementContentDto cardManagementContent) {
    this.cardManagementContent = cardManagementContent;
    return this;
  }

  /**
   * Get cardManagementContent
   * @return cardManagementContent
   */
  @Valid 
  @Schema(name = "cardManagementContent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("cardManagementContent")
  public CardManagementContentDto getCardManagementContent() {
    return cardManagementContent;
  }

  public void setCardManagementContent(CardManagementContentDto cardManagementContent) {
    this.cardManagementContent = cardManagementContent;
  }

  public CardManagementResponseDto commonIcons(CommonIconsDto commonIcons) {
    this.commonIcons = commonIcons;
    return this;
  }

  /**
   * Get commonIcons
   * @return commonIcons
   */
  @Valid 
  @Schema(name = "commonIcons", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commonIcons")
  public CommonIconsDto getCommonIcons() {
    return commonIcons;
  }

  public void setCommonIcons(CommonIconsDto commonIcons) {
    this.commonIcons = commonIcons;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardManagementResponseDto cardManagementResponseDto = (CardManagementResponseDto) o;
    return Objects.equals(this.cardManagementContent, cardManagementResponseDto.cardManagementContent) &&
        Objects.equals(this.commonIcons, cardManagementResponseDto.commonIcons);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cardManagementContent, commonIcons);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardManagementResponseDto {\n");
    sb.append("    cardManagementContent: ").append(toIndentedString(cardManagementContent)).append("\n");
    sb.append("    commonIcons: ").append(toIndentedString(commonIcons)).append("\n");
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

