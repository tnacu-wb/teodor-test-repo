package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.header.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionsDto {

  private String employees;
  private String allowances;
  private String alerts;
  private String cards;
  private String questions;
  private String company;

}
