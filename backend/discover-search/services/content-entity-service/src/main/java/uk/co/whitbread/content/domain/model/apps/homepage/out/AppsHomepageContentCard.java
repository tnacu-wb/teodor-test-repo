package uk.co.whitbread.content.domain.model.apps.homepage.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
public class AppsHomepageContentCard extends AppsHomepageFieldsCard {

  private String title;
}