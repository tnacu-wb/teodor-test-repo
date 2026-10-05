package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class Company {

  private CompanyDetails companyDetails;
  private PaymentDetails paymentDetails;
  private BookingAllowances bookingAllowances;
}
