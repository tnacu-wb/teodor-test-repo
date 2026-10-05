package uk.co.whitbread.company.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CheckCompanyRequest {
    @NotEmpty
    @Schema(required = true, example = "Whitbread")
    private String companyName;

    @NotNull
    @Schema(required = true)
    private Address address;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
