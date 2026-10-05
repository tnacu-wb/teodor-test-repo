package uk.co.whitbread.hotel.account.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.bart.business.api.UserLogin;
import uk.co.whitbread.bart.registeredguest.api.RegisteredGuestLoginRequest;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {

    @Mapping(target = "request.userName", source = "loginRequest.username")
    @Mapping(target = "request.password", source = "loginRequest.password")
    UserLogin toUserLogin(LoginRequest loginRequest);

    @Mapping(target = "registeredGuestLoginDetails.emailAddress", source = "loginRequest.username")
    @Mapping(target = "registeredGuestLoginDetails.password", source = "loginRequest.password")
    RegisteredGuestLoginRequest toRegisteredGuestLoginRequest(LoginRequest loginRequest);

}
