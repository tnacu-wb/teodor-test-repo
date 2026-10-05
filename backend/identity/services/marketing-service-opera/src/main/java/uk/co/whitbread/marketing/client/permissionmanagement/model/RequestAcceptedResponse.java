package uk.co.whitbread.marketing.client.permissionmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestAcceptedResponse {
    private String message;
    private int status;
}
