package uk.co.whitbread.marketing.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CDHError {
    private int status;
    private String errorCode = "3001";
    private String message;

    public CDHError(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
