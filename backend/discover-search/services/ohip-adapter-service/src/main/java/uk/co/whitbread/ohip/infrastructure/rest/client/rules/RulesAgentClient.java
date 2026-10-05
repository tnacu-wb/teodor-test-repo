package uk.co.whitbread.ohip.infrastructure.rest.client.rules;


import static uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils.logErrorHeader;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.BaseRateException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.BookingChannelException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.RoomSubstitutionException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BaseRateRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BookingChannelInfoResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.ChannelRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.WebClientUtils;


@Slf4j
@RequiredArgsConstructor
public class RulesAgentClient {

  private static final String PMS_TYPE = "OP";
  private final WebClient rulesAgentWebClient;
  private final RulesAgentProperties rulesAgentProperties;

  public RoomSubstitutionRuleResponseDto getRoomSubstitution(String roomType, Integer adultsNumber,
      Integer childrenNumber, String channel) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getRoomSubstitutionEndpoint())
            .queryParam("adults", adultsNumber)
            .queryParam("children", childrenNumber)
            .queryParam("pms", PMS_TYPE)
            .queryParam("roomType", roomType)
            .queryParam("channel", channel)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(RoomSubstitutionException.class);
        })
        .bodyToMono(RoomSubstitutionRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get room substitution for roomType %s, adultsNumber %s, childrenNumber"
                + " %s and  channel %s", roomType, adultsNumber, childrenNumber, channel)))
        .block();
  }

  public BookingChannelInfoResponseDto getBookingChannelInfo(BookingChannel bookingChannel) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getBookingChannelInfo())
            .queryParam("channel", bookingChannel.getChannel())
            .queryParam("subchannel", bookingChannel.getSubchannel())
            .queryParam("language", StringUtils.upperCase(bookingChannel.getLanguage()))
            .queryParam("pms", PMS_TYPE)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(BookingChannelException.class);
        })
        .bodyToMono(BookingChannelInfoResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get channel rates for channelId=%s",
                bookingChannel.getChannel())))
        .block();
  }

  public VatRuleResponseDto getVatCodes(String vatRegion, List<String> pkgCodeArr) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getVatCodesEndpoint())
            .queryParam("vatRegion", vatRegion)
            .queryParam("pkgCodeArr", pkgCodeArr)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(BookingChannelException.class);
        })
        .bodyToMono(VatRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error while trying to get transaction codes for vatRegion: %s",
                vatRegion)))
        .block();
  }

  public BusinessAllowanceRuleResponseDto getBusinessAllowances() {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getBusinessAllowanceEndpoint())
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(BookingChannelException.class);
        })
        .bodyToMono(BusinessAllowanceRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to get business allowances."))
        .block();
  }

  public ChannelRuleResponseDto getChannelSourceInfo(String sourceId) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
            .path(rulesAgentProperties.getChannelSourceInfoEndpoint())
            .queryParam("sourceId", sourceId)
            .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(BookingChannelException.class);
        })
        .bodyToMono(ChannelRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get channel source info for sourceId=%s",
            sourceId
        )))
        .block();
  }
  
  public BaseRateRuleResponseDto getBaseRate(String ratePlanCode) {
    return rulesAgentWebClient.get().uri(uriBuilder -> uriBuilder
        .path(rulesAgentProperties.getBaseRateEndpoint())
        .queryParam("ratePlanCode", ratePlanCode)
        .build())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          logErrorHeader(log, response);
          return response.bodyToMono(BaseRateException.class);
        })
        .bodyToMono(BaseRateRuleResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get base rate code for ratePlanCode=%s", ratePlanCode)))
        .block();
  }
}
