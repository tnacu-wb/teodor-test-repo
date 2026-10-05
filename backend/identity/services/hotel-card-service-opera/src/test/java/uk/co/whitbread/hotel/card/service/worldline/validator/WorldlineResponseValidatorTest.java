package uk.co.whitbread.hotel.card.service.worldline.validator;



import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.card.exceptions.WorldlineGenericException;
import uk.co.whitbread.hotel.card.exceptions.WorldlineNotFoundException;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardListResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardViewResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountRegisteredUserListResponse;
import uk.co.whitbread.hotel.card.model.adapter.WorldlineResponseAdapter;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAllDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserType;
import worldline.mst.bsm.api.b2b.pi.data.ErrorInfoType;
import worldline.mst.bsm.api.b2b.pi.data.ProcessingMetaInfoType;


@ExtendWith(MockitoExtension.class)
class WorldlineResponseValidatorTest {

  public static final String RESULT_CODE = "OK";
  public static final String MESSAGE_GUID_TEST = "messageGuidTest";
  private final WorldlineResponseValidator worldlineResponseValidator = new WorldlineResponseValidator();

  @Test
  void validate() {
    worldlineResponseValidator.validate(getRegisteredUserListResponse());
  }

  @Test
  void validate_EmptyCustomerAccountCardListResponseType() {
    assertThrows(WorldlineNotFoundException.class,
        () -> worldlineResponseValidator.validate(getCustomerAccountCardEmptyListResponseType()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"ValidationError", "ContextActionError", "AuthorisationError",
      "NotFoundError", "AuthenticationError", "NotImplemented", "AccessDeniedNotWhitelisted",
      "SystemError", "SchemaError"})
  void validate_ErrorCodes(String errorCode) {
    assertThrows(WorldlineGenericException.class,
        () -> worldlineResponseValidator.validate(
            getCustomerAccountCardListResponseTypeContainsError(errorCode)));
  }

  @Test
  void validateAdapter() {
    worldlineResponseValidator.validate(getWorldlineResponseAdapter());
  }


  @Test
  void validate__emptyResponse() {
    assertThrows(RuntimeException.class,
        () -> worldlineResponseValidator.validate(
            new CustomerAccountRegisteredUserListResponse().getResponse()));
  }


  private WorldlineResponseAdapter getWorldlineResponseAdapter() {
    var responseType = new CustomerAccountCardViewResponseType();
    responseType.setCard(new CustomerAccountCardAllDetailsType());
    var response = new CustomerAccountCardViewResponse();
    response.setResponse(responseType);
    return new InnBCustomerAccountCardViewResponse(response);
  }

  private WorldlineResponseAdapter getRegisteredUserListResponse() {
    var responseType = new CustomerAccountRegisteredUserListResponseType();
    responseType.getRegisteredUsers().addAll(getCustomerAccountRegisteredUserType());
    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    CustomerAccountRegisteredUserListResponse response = new CustomerAccountRegisteredUserListResponse();
    response.setResponse(responseType);
    return new InnBCustomerAccountRegisteredUserListResponse(response);
  }

  private WorldlineResponseAdapter getCustomerAccountCardEmptyListResponseType() {
    var responseType = new CustomerAccountCardListResponseType();
    responseType.getCustomerAccountCardListItem().addAll(Lists.newArrayList());
    responseType.setResultCode(RESULT_CODE);
    responseType.setMetaInfo(createProcessingMetaInfoType());

    CustomerAccountCardListResponse response = new CustomerAccountCardListResponse();
    response.setResponse(responseType);
    return new InnBCustomerAccountCardListResponse(response);
  }
  private WorldlineResponseAdapter getCustomerAccountCardListResponseTypeContainsError(String errorCode) {
    var error = new ErrorInfoType();
    error.setErrorCode("dummyErrorCode");
    error.setInfo("dummyErrorInfo");
    var errors = List.of(error);
    var responseType = new CustomerAccountCardListResponseType();
    responseType.getErrors().addAll(errors);
    responseType.getCustomerAccountCardListItem().addAll(Lists.newArrayList());
    responseType.setResultCode(errorCode);
    responseType.setMetaInfo(createProcessingMetaInfoType());
    CustomerAccountCardListResponse response = new CustomerAccountCardListResponse();
    response.setResponse(responseType);
    return new InnBCustomerAccountCardListResponse(response);
  }

  private List<CustomerAccountRegisteredUserType> getCustomerAccountRegisteredUserType() {
    CustomerAccountRegisteredUserType mock1 = new CustomerAccountRegisteredUserType();
    mock1.setAPIUserGuid("9B1B3ED5-F61C-403E-8A56-59F0B625F856");
    mock1.setDisplayName("mock1");
    mock1.setHasAddress(false);
    CustomerAccountRegisteredUserType mock2 = new CustomerAccountRegisteredUserType();
    mock1.setAPIUserGuid("421CE8E5-4E87-4829-B78D-18EACE235A56");
    mock1.setDisplayName("mock2");
    mock1.setHasAddress(true);
    List<CustomerAccountRegisteredUserType> customerAccountRegisteredUserTypes = new ArrayList<>();
    customerAccountRegisteredUserTypes.add(mock1);
    customerAccountRegisteredUserTypes.add(mock2);
    return customerAccountRegisteredUserTypes;
  }

  private ProcessingMetaInfoType createProcessingMetaInfoType() {
    var processingMetaInfoType = new ProcessingMetaInfoType();
    processingMetaInfoType.setMessageGuid(MESSAGE_GUID_TEST);
    return processingMetaInfoType;
  }
}
