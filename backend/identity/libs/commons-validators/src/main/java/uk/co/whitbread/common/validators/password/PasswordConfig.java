package uk.co.whitbread.common.validators.password;

import lombok.Data;

import java.util.List;

@Data
public class PasswordConfig {

    private Integer minLength;
    private Integer maxLength;
    private String illegalChars;
    private List<String> regExps;
    private String staticErrorMessage;
    private boolean allowNull;
    private boolean enabled = true;

}
