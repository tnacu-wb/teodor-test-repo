package uk.co.whitbread.common.exceptions.mapping;

import lombok.Data;

import java.util.List;

@Data
public class ErrorCodeMapping {

    private String code;
    private String message;
    private int httpStatus;
    private List<String> bartCodes;

}
