package uk.co.whitbread.employee.bulk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDefinedAnswer {

    public UserDefinedAnswer() {
    }

    public UserDefinedAnswer(String miID, String miAnswer) {
        this.miID = miID;
        this.miAnswer = miAnswer;
    }

    private String miID;
    private String miAnswer;

}
