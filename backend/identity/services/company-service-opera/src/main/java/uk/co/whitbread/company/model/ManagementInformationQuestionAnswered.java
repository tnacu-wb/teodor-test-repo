package uk.co.whitbread.company.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ManagementInformationQuestionAnswered extends ManagementInformationQuestion {
    @NotEmpty
    private String answer;
}
