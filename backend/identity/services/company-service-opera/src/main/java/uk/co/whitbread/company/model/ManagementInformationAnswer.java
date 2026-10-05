package uk.co.whitbread.company.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagementInformationAnswer {
    private AnswerType answerType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> answers;
}
