package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private boolean success;
    private String sessionId;
    private String customerId;
    private boolean existingCompany;
    private boolean existingEmployee;

}
