package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactChannel {

    @NotBlank
    private ContactType contactChannelType;

    private String contactChannelValue;

    private String contactChannelId;

}
