package uk.co.whitbread.company.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserDefinedQuestion extends ManagementInformationQuestion {
    @NotEmpty
    private String userDefinedAnswer;
}


