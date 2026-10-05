package uk.co.whitbread.marketing.model.permissionmanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.marketing.model.SourceChannel;
import uk.co.whitbread.marketing.model.SourceLocale;
import uk.co.whitbread.marketing.model.UserJourney;
import uk.co.whitbread.marketing.validations.ValidateSourceSystem;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SourceDetails {
    @ValidateSourceSystem(enumClass = SourceChannel.class)
    @Schema(example = "WEB/BB/APPS_IOS/APPS_ANDROID", requiredMode = RequiredMode.NOT_REQUIRED, description = "Channel is the source from where Opt-In/Opt-Out requested")
    private String channel;
    @ValidateSourceSystem(enumClass = UserJourney.class)
    @Schema(example = "PERMISSIONCENTRE/SIGNUP/NEWSLETTERSIGNUP/NEWSLETTERSIGNUP/ACTIVATE", requiredMode = RequiredMode.NOT_REQUIRED, description = "Journey is User flow from where Opt-In/Opt-Out requested")
    private String journey;
    @ValidateSourceSystem(enumClass = SourceLocale.class)
    @Schema(example = "UK/DE", requiredMode = RequiredMode.NOT_REQUIRED, description = "Locale is the logical location from Opt-In/Opt-Out requested")
    private String locale;
}
