package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.company.out.Company;

public interface CompanyEntityServiceOutPort {

  Company getCompanyById(String id);
}
