package uk.co.whitbread.content.domain.model.apps.homepage.out;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class AppsHomepageCard extends AppsHomepageFieldsCard {

  @NotEmpty
  private String title;
}