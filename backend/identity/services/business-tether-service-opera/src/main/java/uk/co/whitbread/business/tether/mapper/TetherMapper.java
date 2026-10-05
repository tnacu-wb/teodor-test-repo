package uk.co.whitbread.business.tether.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import worldline.mst.bsm.api.b2b.pi.data.TetherByAccountNumber;
import worldline.mst.bsm.api.b2b.pi.data.TetherByCardNumber;


@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface TetherMapper {

    @Mapping(source = "request.linkCode", target = "linkCode")
    TetherLinkRequest toTetherLinkRequest(TetherByAccountNumber tetherByAccountNumber);

    @Mapping(source = "linkCode", target = "request.linkCode")
    TetherByCardNumber toTetherByCardNumber(TetherLinkRequest tetherLinkRequest);
}
