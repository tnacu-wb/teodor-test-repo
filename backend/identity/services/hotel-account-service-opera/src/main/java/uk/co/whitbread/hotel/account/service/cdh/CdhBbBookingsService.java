package uk.co.whitbread.hotel.account.service.cdh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.hotel.account.mapper.CdhBookingHistoryRequestMapper;
import uk.co.whitbread.hotel.account.mapper.CdhBookingHistoryResponseMapper;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.BookingHistoryProperties;
import uk.co.whitbread.hotel.account.utils.BookingUtils;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@Slf4j
@Service
public class CdhBbBookingsService extends CdhBookingService {

  private final CdhBookingHistoryRequestMapper bookingHistoryRequestMapper;

  public CdhBbBookingsService(CdhService cdhService, CdhBookingHistoryRequestMapper bookingHistoryRequestMapper,
                              CdhBookingHistoryResponseMapper bookingHistoryResponseMapper,
                              UnleashWrapper<FeatureFlag> unleashWrapper, BookingHistoryProperties bookingHistoryProperties) {
    super(cdhService, bookingHistoryResponseMapper, unleashWrapper, bookingHistoryProperties);
    this.bookingHistoryRequestMapper = bookingHistoryRequestMapper;
  }

  public StaysResponse retrieveCdhBbBookingsV2(BBStaysRequest staysRequest,
      CdhEmployeeDetails cdhEmployeeDetails, Integer pageIndex, Integer pageSize) {
    var queryParams = bookingHistoryRequestMapper.toBbCdhBookingHistoryRequest(staysRequest, pageIndex, pageSize, cdhEmployeeDetails);
    BookingUtils.addFiltersV2(staysRequest, queryParams);
    return getBookingsV2(queryParams, staysRequest, cdhEmployeeDetails.getUserEmail());
  }

}
