package uk.co.whitbread.company.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDefinedAnswer {
    private String miID;
    private String miAnswer;
}
