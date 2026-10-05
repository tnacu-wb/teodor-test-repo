package uk.co.whitbread.company.employee.model;


import lombok.Data;

@Data
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
