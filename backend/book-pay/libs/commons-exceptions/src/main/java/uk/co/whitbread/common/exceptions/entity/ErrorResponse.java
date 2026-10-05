package uk.co.whitbread.common.exceptions.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Standard error class returned by all services
 */
public class ErrorResponse implements Serializable {

    @Schema(example = "001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private String code;

    @Schema(example = "[\"Error message\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> details = new ArrayList<>();

    /**
     * Default Constructor only used for serialisation purposes
     */
    public ErrorResponse(){
        super();
    }

    public ErrorResponse(String code, String... details) {
        this.code = code;
        this.details = Arrays.asList(details);
    }

    public ErrorResponse(String code, Errors errors) {
        this.code = code;
        this.details = errors.getAllErrors()
                .stream()
                .map(this::formatErrorMessage)
                .collect(toList());
    }

    private String formatErrorMessage(ObjectError error) {
        String errorObjectName = (FieldError.class.isAssignableFrom(error.getClass())) ?
                ((FieldError) error).getField() : error.getObjectName();
        return String.format("%s %s", errorObjectName, error.getDefaultMessage());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return String.format("ErrorResponse{code='%s', details=%s}", code, details);
    }
}