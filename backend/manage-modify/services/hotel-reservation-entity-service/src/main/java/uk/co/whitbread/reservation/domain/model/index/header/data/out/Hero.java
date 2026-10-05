package uk.co.whitbread.reservation.domain.model.index.header.data.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

  private Boolean subHeadingFontShadow;
  private Boolean bottomCaptionArrowShow;
  private String subHeadingFontWeight;
  private Boolean subHeadingShow;
  private String subHeadingText;
  private String analyticsId;
  private Boolean bottomCaptionShadow;
  private String subHeadingFontColor;
  private String backgroundColor;
  private String backgroundImage;
  private Boolean headingFontShadow;
  private String headingFontColor;
  private Boolean bottomCaptionShow;
  private String bottomCaptionText;
  private String headingFontWeight;
  private String headingTextLong;
  private String headingTextShort;
}
