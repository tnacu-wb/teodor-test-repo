package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.piba.account.model.enums.Scheme;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMemorableWordRequest {

    private String sessionId;
    private String nonce;
    private String timestamp;
    private String hash;
    private String newMemorableWord;
    private String tetheredUserGuid;
    private Scheme scheme;
}