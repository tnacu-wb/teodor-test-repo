package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.shared.azureemail.api.Content;
import uk.co.whitbread.shared.azureemail.model.PasswordReset;


@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
abstract class PasswordResetTransformer {

  @Mapping(source = "passwordReset.resetPasswordUrl", target = "resetLink")
  @Mapping(source = "passwordReset.email", target = "emailAddress")
  @Mapping(expression = "java(passwordReset.getFirstName() + \" \" + passwordReset.getLastName())", target = "infName")
  @Mapping(source = "language", target = "languageCode")
  abstract Content toForgotPasswordResetContent(PasswordReset passwordReset);
}
