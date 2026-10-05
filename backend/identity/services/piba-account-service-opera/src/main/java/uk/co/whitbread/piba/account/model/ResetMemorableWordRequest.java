package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.piba.account.model.enums.Scheme;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetMemorableWordRequest {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example= "514E03C5-F54E-4DE0-A47E-0C2BBEE744F6")
    private String tetheredUserGuid;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example= "Hello1234")
    private String memorableWord;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, example= "GB")
    private Scheme scheme;

    public ResetMemorableWordRequest(String tetheredUserGuid, String memorableWord) {
        this.tetheredUserGuid = tetheredUserGuid;
        this.memorableWord = memorableWord;
    }
}
