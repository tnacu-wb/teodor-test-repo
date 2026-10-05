package uk.co.whitbread.spending.domain.ports.secondary;

import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;

public interface WorldlineOutPort {
  AccountInfoResponse getAccountInfo(String location, String ipAddress, String tetheredUserGuid);

  PaymentInfoResponse getPaymentInfo(String location, String tetheredUserGuid,
      PaymentInfoModel paymentInfoModel);
}
