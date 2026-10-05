package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptResponse {
    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String encrypted;
    @JsonProperty("am7VLjo2qMes8BT4PAMBLkNEKVCYPvMMcfSUlbihxZsvlC4w")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cyphered;
}
