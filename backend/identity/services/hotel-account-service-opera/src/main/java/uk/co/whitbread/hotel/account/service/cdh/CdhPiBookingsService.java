package uk.co.whitbread.hotel.account.service.cdh;

import java.util.Objects;
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

@Slf4j
@Service
public class CdhPiBookingsService extends CdhBookingService {

  private final CdhBookingHistoryRequestMapper bookingHistoryRequestMapper;

  public CdhPiBookingsService(CdhService cdhService, CdhBookingHistoryRequestMapper bookingHistoryRequestMapper,
                              CdhBookingHistoryResponseMapper bookingHistoryResponseMapper,
                              UnleashWrapper<FeatureFlag> unleashWrapper, BookingHistoryProperties bookingHistoryProperties) {
    super(cdhService, bookingHistoryResponseMapper, unleashWrapper, bookingHistoryProperties);
    this.bookingHistoryRequestMapper = bookingHistoryRequestMapper;
  }

  public StaysResponse retrieveCdhPiBookingsV2(BBStaysRequest staysRequest, String customerAccountId,
      String email, Integer pageIndex, Integer pageSize) {
    var queryParams = bookingHistoryRequestMapper.toPiCdhBookingHistoryRequest(staysRequest, pageIndex, pageSize, customerAccountId);
    if (Objects.nonNull(staysRequest.getFilterType())) {
      BookingUtils.addFiltersV2(staysRequest, queryParams);
    }
    return getBookingsV2(queryParams, staysRequest, email);
  }
}
