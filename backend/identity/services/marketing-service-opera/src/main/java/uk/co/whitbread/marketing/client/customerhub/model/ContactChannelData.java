package uk.co.whitbread.marketing.client.customerhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactChannelData {

    @NotBlank
    private String contactChannelType;

    private String contactChannelValue;

    private String contactChannelId;

}
