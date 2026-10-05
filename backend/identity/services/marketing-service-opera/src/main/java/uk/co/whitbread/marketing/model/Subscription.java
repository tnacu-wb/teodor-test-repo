package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class Subscription {

    @NotNull
    private ContactType contactType;

    @NotBlank
    private String contactValue;

    @NotNull
    private BrandCode brandCode;

    @NotNull
    private Boolean subscribe;
}
