package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import lombok.Data;

@Data
public class CompanyDto {

  private CompanyDetailsDto companyDetails;
  private PaymentDetailsDto paymentDetails;
  private BookingAllowancesDto bookingAllowances;

}
