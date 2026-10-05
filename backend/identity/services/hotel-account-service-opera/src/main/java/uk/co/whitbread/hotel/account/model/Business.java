package uk.co.whitbread.hotel.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business implements Serializable {

    private static final long serialVersionUID = 1L;

    private AccessLevel accessLevel;

    private String purchaseOrderAnswer;

    private String customerReferenceAnswer;

    private String centralCard;

    private String myPILink;

    private Boolean dismissMPILink;

    private Boolean miSetupRequired;

    private Long awaitingApproval;

    private String employeeId;

    private boolean tethered;
}
