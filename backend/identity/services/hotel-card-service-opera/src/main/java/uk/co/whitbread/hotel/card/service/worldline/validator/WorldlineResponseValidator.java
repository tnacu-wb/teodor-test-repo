package uk.co.whitbread.hotel.card.service.worldline.validator;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.exceptions.ErrorCodes;
import uk.co.whitbread.hotel.card.exceptions.WorldlineGenericException;
import uk.co.whitbread.hotel.card.exceptions.WorldlineNotFoundException;
import uk.co.whitbread.hotel.card.model.adapter.WorldlineResponseAdapter;
import uk.co.whitbread.piba.api.validation.WorldLineResponseValidator;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.ProcessingMetaInfoType;

@Component
public class WorldlineResponseValidator extends WorldLineResponseValidator {

  public static final String ERROR_EMPTY_RESPONSE = "Worldline returned empty response.";


  /**
   * First validates the given parameter is not null then get the response from it and validates it is a successful one.
   * If not a PibaException is thrown with the Worldline message wrapped in it.
   */
  public void validate(final WorldlineResponseAdapter response) {
    validate(Optional.ofNullable(response)
        .filter(res -> !hasErrors(res.getResponse().getResultCode(),
            res.getResponse().getErrors(), res.getResponse().getMetaInfo()))
        .filter(res -> !isResponseBodyEmpty(res.getResponse()))
        .map(WorldlineResponseAdapter::getResponse)
        .orElseThrow(() -> new RuntimeException(ERROR_EMPTY_RESPONSE)));
  }

  private boolean hasErrors(final String resultCode, List<ErrorInfoType> errors, ProcessingMetaInfoType metaInfo) {
    if (resultCode != null && !"OK".equals(resultCode) && !errors.isEmpty()) {
      var errorCode = ErrorCodes.valueOf(convertToUpperSnakeCase(resultCode)).getCode();
      throw new WorldlineGenericException(errorCode, "WorldLine errorCode: " + errors.get(0).getErrorCode() +
          ", WorldLine error info: " + errors.get(0).getInfo() + ", WorldLine messageGuid: " + metaInfo.getMessageGuid());
    }
    return false;
  }

  private <T> boolean isResponseBodyEmpty(final T object) {
    if (object == null ){
      throw new WorldlineNotFoundException(ERROR_EMPTY_RESPONSE);
    }

    if (object instanceof CustomerAccountCardListResponseType res) {
      isResponseBodyEmpty(res.getCustomerAccountCardListItem().isEmpty()?null:this);
    }
    return false;
  }

  private static String convertToUpperSnakeCase(String input) {
    String[] words = input.split("(?<=.)(?=[A-Z])");
    return StringUtils.join(words, '_').toUpperCase();
  }
}
