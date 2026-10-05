package uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexHeaderDataDto {

  private ContentDto content;
  private FormDto form;
  private DatePickerDto datePicker;
  private ResultsDto results;
  private AnnouncementDto announcement;
  private ContactBannerDto contactBanner;
  private ConfigDto config;
}
