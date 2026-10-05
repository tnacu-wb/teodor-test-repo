package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.cdh.CdhSearchCompaniesRequest;
import uk.co.whitbread.basket.domain.model.payments.out.CdhSearchCompaniesResponse;

public interface CdhSearchCompaniesOutPort {

  CdhSearchCompaniesResponse searchCompaniesFromCdh(
          CdhSearchCompaniesRequest cdhSearchCompaniesRequest);
}