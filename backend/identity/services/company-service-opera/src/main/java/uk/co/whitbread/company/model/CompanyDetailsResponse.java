package uk.co.whitbread.company.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDetailsResponse {
    private Company requestedCompany;
    private List<CellCode> companyCellCodes;
    private boolean allowCentralCreditCard;
    private boolean marketingAllowed;
    private boolean companyLockedForEditing;
    private boolean success;
}

