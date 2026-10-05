package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroDto {

  private Boolean subHeadingFontShadow;
  private Boolean bottomCaptionArrowShow;
  private String subHeadingFontWeight;
  private Boolean subHeadingShow;
  private String subHeadingText;
  private Boolean bottomCaptionShadow;
  private String subHeadingFontColor;
  private String backgroundColor;
  private String backgroundImage;
  private Boolean headingFontShadow;
  private String headingFontColor;
  private Boolean bottomCaptionShow;
  private String bottomCaptionText;
  private String headingFontWeight;
  private Boolean footnotePosRight;
  private String headingTextLong;
  private String headingTextShort;
}
