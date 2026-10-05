package uk.co.whitbread.hotel.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationsResponse {

    private String status;
    private Boolean usersAwaitingApproval;
    private Boolean profileUpdateRequired;
}
