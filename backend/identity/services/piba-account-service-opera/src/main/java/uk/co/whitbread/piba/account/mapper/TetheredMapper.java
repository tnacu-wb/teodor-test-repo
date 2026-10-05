package uk.co.whitbread.piba.account.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.piba.account.model.TetheredLoginResponse;
import uk.co.whitbread.piba.account.model.TetheredUserAccountOverview;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsOverview;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.TetheredUserRequest;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountOverviewType;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserOverviewType;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface TetheredMapper {

    @Mapping(source = "response.newSession.sessionId", target = "sessionId")
    @Mapping(source = "response.newSession.sharedSecret", target = "sharedSecret")
    TetheredLoginResponse toTetheredLoginResponse(LoginTetheredUserResponse loginTetheredUserResponse);

    @Mapping(source = "response.tetheredUserDetails.tetheredUserOverview", target = "tetheredUserOverview")
    @Mapping(source = "response.tetheredUserDetails.customerAccountOverview", target = "customerAccountOverview")
    TetheredUserDetailsResponse toTetheredUserDetailsResponse(TetheredUserDetailsGetResponse tetheredUserDetailsResponse);

    @Mapping(source = "tetheredUserGuid", target = "tetheredUserGuid")
    @Mapping(source = "APIUserGuid", target = "apiUserGuid")
    @Mapping(source = "userRole", target = "userRole")
    @Mapping(source = "countMyCards", target = "myCards")
    TetheredUserDetailsOverview tetheredTypeToUserDetailsOverview(TetheredUserOverviewType tetheredUserOverviewType);

    @Mapping(source = "primarySchemeCustomerId", target = "primarySchemeCustomerId")
    @Mapping(source = "schemeCustomerId", target = "schemeCustomerId")
    @Mapping(source = "accountName", target = "accountName")
    @Mapping(source = "accountNumber", target = "accountNumber")
    TetheredUserAccountOverview customerAccToTetheredUserDetailsOverview(CustomerAccountOverviewType tetheredUserOverviewType);

    uk.co.whitbread.shared.cdh.model.TetheredUserRequest toTetheredUserRequest(
        TetheredUserRequest tetheredUserRequest);

}
