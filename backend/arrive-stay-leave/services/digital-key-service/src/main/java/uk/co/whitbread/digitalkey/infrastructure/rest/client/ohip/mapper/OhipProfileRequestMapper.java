package uk.co.whitbread.digitalkey.infrastructure.rest.client.ohip.mapper;

import java.util.Arrays;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class OhipProfileRequestMapper {

}
