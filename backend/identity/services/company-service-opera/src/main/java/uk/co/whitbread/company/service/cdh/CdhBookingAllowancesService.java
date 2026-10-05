package uk.co.whitbread.company.service.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.mapper.CompanyMapper;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhBookingAllowancesService {

  private final CdhService cdhService;
  private final CompanyMapper companyMapper;

  public void updateBookingAllowances(String companyId, BookingAllowances bookingAllowances, String userEmail) {
    GetCompanyResponse getCompanyResponse = cdhService.getCompanyDetails(companyId, userEmail);
    CompanyAccountRequest companyAccountRequest = companyMapper.toCompanyAccountRequest(getCompanyResponse);
    CompanyAccountRequest updatedCompanyAccountRequest =  companyMapper.toUpdatedCompanyAccountRequest(companyAccountRequest, bookingAllowances);
    cdhService.updateCompanyDetails(companyId, updatedCompanyAccountRequest, userEmail);
  }

}
