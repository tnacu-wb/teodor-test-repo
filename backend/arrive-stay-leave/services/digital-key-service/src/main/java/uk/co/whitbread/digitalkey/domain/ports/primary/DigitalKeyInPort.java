package uk.co.whitbread.digitalkey.domain.ports.primary;


import uk.co.whitbread.digitalkey.domain.model.axp.in.GoogleWalletProvisioningRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpProvisionRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.OtpRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.in.RegisterMobileDeviceRequest;
import uk.co.whitbread.digitalkey.domain.model.axp.out.GoogleWalletProvisioningResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpProvisionResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.OtpResponse;
import uk.co.whitbread.digitalkey.domain.model.axp.out.RegisterMobileDeviceResponse;

public interface DigitalKeyInPort {
  OtpResponse generateOtp(OtpRequest request);

  OtpProvisionResponse passProvisioningWithOtp(OtpProvisionRequest otpProvisionRequest);

  RegisterMobileDeviceResponse registerMobileDevice(RegisterMobileDeviceRequest request);

  GoogleWalletProvisioningResponse googleWalletProvisioningWithOtp(GoogleWalletProvisioningRequest request);
}
