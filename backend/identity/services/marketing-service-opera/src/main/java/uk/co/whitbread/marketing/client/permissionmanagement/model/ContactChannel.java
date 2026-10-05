package uk.co.whitbread.marketing.client.permissionmanagement.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactChannel {

    @NotBlank
    private String contactChannelType;

    private String contactChannelValue;

    private String contactChannelId;
}
