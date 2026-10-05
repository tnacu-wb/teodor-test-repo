package uk.co.whitbread.marketing.utils;

import org.springframework.stereotype.Component;
import uk.co.whitbread.bart.marketing.api.ErrorDetails;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse2015;
import uk.co.whitbread.bart.unified.api.ArrayOfRegionRegion;
import uk.co.whitbread.bart.unified.api.Region;
import uk.co.whitbread.bart.unified.api.SharedDataRequestResponse;
import uk.co.whitbread.bart.unified.api.SharedDataResponse;

import java.util.List;
import java.util.Optional;

@Component
public class BartResponseValidator {

    public Optional<String> validate(SharedDataRequestResponse sharedDataRequestResponse) {

        Optional<List<Region>> regions = Optional.ofNullable(sharedDataRequestResponse)
                .map(SharedDataRequestResponse::getSharedDataRequestResult)
                .map(SharedDataResponse::getRegions)
                .map(ArrayOfRegionRegion::getRegion)
                .filter(r4 -> !r4.isEmpty());

        return regions.isPresent() ? Optional.empty() : Optional.of(BartResponseValidatorUtils.ERROR_EMPTY_RESPONSE);
    }

    public Optional<String> validate(SubscriptionStatusResponse response) {


        return Optional.ofNullable(response)
                .map(SubscriptionStatusResponse::getSubscriptionStatusResult)
                .map(this::getErrorMessage)
                .orElse(Optional.of(BartResponseValidatorUtils.ERROR_EMPTY_RESPONSE));

    }


    public Optional<String> validate(SubscribeResponse response) {

        return Optional.ofNullable(response)
                .map(SubscribeResponse::getSubscribeResult)
                .map(this::getErrorMessage)
                .orElse(Optional.of(BartResponseValidatorUtils.ERROR_EMPTY_RESPONSE));

    }

    private Optional<String> getErrorMessage(SubscriptionResponse result) {

        return Optional.ofNullable(result.getErrorDetail())
                .map(ErrorDetails::getErrorMessage).map(BartResponseValidatorUtils::checkErrorMessage);


    }

    private Optional<String> getErrorMessage(SubscriptionStatusResponse2015 result) {

        return Optional.ofNullable(result.getErrorDetail())
                .map(ErrorDetails::getErrorMessage).map(BartResponseValidatorUtils::checkErrorMessage);


    }
}
