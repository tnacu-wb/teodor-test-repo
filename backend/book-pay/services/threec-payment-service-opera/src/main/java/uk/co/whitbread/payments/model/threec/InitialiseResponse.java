package uk.co.whitbread.payments.model.threec;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitialiseResponse {

    private String ipgSession;
    private int ipgResultCode;
    private  String ipgResultText;
}
