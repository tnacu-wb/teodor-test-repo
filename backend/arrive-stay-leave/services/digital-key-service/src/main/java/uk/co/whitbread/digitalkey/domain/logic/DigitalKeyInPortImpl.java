package uk.co.whitbread.digitalkey.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;
import uk.co.whitbread.digitalkey.domain.ports.primary.DigitalKeyInPort;
import uk.co.whitbread.digitalkey.domain.ports.secondary.AxpOutPort;

@Slf4j
@RequiredArgsConstructor
public class DigitalKeyInPortImpl implements DigitalKeyInPort {

  private final AxpOutPort axpOutPort;

  @Override
  public OtpResponse generateOtp(OtpRequest request) {
    return axpOutPort.sendOtp(request);
  }

  @Override
  public OtpProvisionResponse passProvisioningWithOtp(OtpProvisionRequest otpProvisionRequest) {
    return axpOutPort.verifyOtpAndGetDigitalKey(otpProvisionRequest);
  }

  @Override
  public RegisterMobileDeviceResponse registerMobileDevice(RegisterMobileDeviceRequest request) {
    return axpOutPort.registerMobileDevice(request);
  }

  @Override
  public GoogleWalletProvisioningResponse googleWalletProvisioningWithOtp(GoogleWalletProvisioningRequest request) {
    return axpOutPort.googleWalletProvisioningWithOtp(request);
  }
}
