package uk.co.whitbread.piba.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.piba.account.converter.Converters;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimit;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CreditProposeMapper {

    @Mapping(source = "tetheredUserGuid", target = "request.tetheredUserGuid", qualifiedByName = "convertFromTetheredGuid")
    @Mapping(source = "schemeCustomerId", target = "request.schemeCustomerId")
    @Mapping(source = "proposedCreditLimit", target = "request.proposedCreditLimit")
    CreditProposeNewLimit toCreditProposeNewLimit(CreditProposeLimitRequest creditProposeLimitRequest);

    @Mapping(source = "response.CLIRequestId", target = "requestId")
    CreditProposeLimitResponse toCreditProposeLimitResponse(CreditProposeNewLimitResponse creditProposeNewLimitResponse);

    @Named("convertFromTetheredGuid")
    default String convertFromTetheredGuid(String tetheredGuid) {
        return Converters.convertFromTetheredGuid(tetheredGuid);
    }
}
