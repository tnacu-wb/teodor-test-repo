package uk.co.whitbread.employee.bulk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class HeaderProperties {

  private String[] headerValues;
  private List<String> headerQuestionIds;
}