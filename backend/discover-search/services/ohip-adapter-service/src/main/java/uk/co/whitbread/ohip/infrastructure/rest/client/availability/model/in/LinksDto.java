package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import java.util.ArrayList;
import lombok.Data;
import org.springframework.validation.annotation.Validated;


@Validated
@Data
public class LinksDto extends ArrayList<InstanceLinkDto> {

}
