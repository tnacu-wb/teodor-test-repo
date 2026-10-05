package uk.co.whitbread.shared.azureemail.requestbuilder;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import org.mapstruct.Mapper;
import uk.co.whitbread.shared.azureemail.api.ContentPremierGuestInternet;
import uk.co.whitbread.shared.azureemail.model.PiRegister;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
interface PiRegisterTransformer {

  ContentPremierGuestInternet toPremierGuestInternetContent(PiRegister piRegister);
}
