package uk.co.whitbread.content.infrastructure.rest.controller.apps.homepage.model.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AppsHomepageCardDto extends AppsHomepageFieldsCardDto {

  @NotEmpty
  private String title;
}