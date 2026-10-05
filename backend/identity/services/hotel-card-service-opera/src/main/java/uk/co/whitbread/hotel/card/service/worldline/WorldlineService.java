package uk.co.whitbread.hotel.card.service.worldline;

import feign.FeignException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.hotel.card.client.worldline.WorldlineClient;
import uk.co.whitbread.hotel.card.client.worldline.model.CostCentreData;
import uk.co.whitbread.hotel.card.client.worldline.model.TrustedPartnerCredentials;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserRequest;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineCardHolderUserResponse;
import uk.co.whitbread.hotel.card.client.worldline.model.WorldlineReplaceCardRequest;
import uk.co.whitbread.hotel.card.exceptions.CardHolderInvalidMobileException;
import uk.co.whitbread.hotel.card.exceptions.CardHolderNotCreatedException;
import uk.co.whitbread.hotel.card.mapper.worldline.WorldlineMapper;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardAddRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceResponse;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardInviteRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardUpdateRequest;
import uk.co.whitbread.hotel.card.model.WorldlineCardDetails;
import uk.co.whitbread.hotel.card.model.WorldlineCardHolderUserDetails;
import uk.co.whitbread.hotel.card.model.WorldlineRegisteredUser;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardActivateResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardAddResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardCancelResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardInviteResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardListResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardUpdateResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardViewResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountRegisteredUserListResponse;
import uk.co.whitbread.hotel.card.model.adapter.InnBTetheredUserDetailsGetResponse;
import uk.co.whitbread.hotel.card.properties.WorldlineRestProperties;
import uk.co.whitbread.hotel.card.service.worldline.converter.IWorldlineSoapTransformer;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.service.worldline.validator.WorldlineResponseValidator;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancelResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListResponseType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardViewResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponseType;

@Service
@Slf4j
@AllArgsConstructor
public class WorldlineService {

  private final WebServiceTemplate worldlineWebServiceTemplate;
  private final IWorldlineSoapTransformer worldlineSoapTransformer;
  private final WorldLineProperties worldLineProperties;
  private final WorldlineResponseValidator worldlineResponseValidator;
  private final WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
  private final WorldlineMapper worldlineMapper;
  private final WorldlineUtils worldlineUtils;
  private final WorldlineClient worldlineClient;
  private final WorldlineRestProperties worldlineRestProperties;

  public List<WorldlineRegisteredUser> getAccountRegisteredUsers(final String tetheredUserId, final String countryCode) throws SoapFaultClientException {

    var soapTetheredUserId = worldlineUtils.formatId(tetheredUserId);
    int tetheredUserSchemeCustomerId = getTetheredUserDetails(soapTetheredUserId, countryCode)
        .getTetheredUserDetails()
        .getCustomerAccountOverview()
        .getSchemeCustomerId();

    final CustomerAccountRegisteredUserListResponse response = fireWorldlineRequest(worldlineSoapTransformer
        .toGetAccountRegisteredUserListSoapRequest(soapTetheredUserId, countryCode ,
            tetheredUserSchemeCustomerId));

    worldlineResponseValidator.validate(new InnBCustomerAccountRegisteredUserListResponse(response));

    return worldlineMapper.toWorldlineRegisteredUser(response.getResponse().getRegisteredUsers());
  }

  /**
   * Returns card details for the card identified with given id and for the user identified by
   * tetheredUserId
   *
   * @return card details
   */
  public WorldlineCardDetails getPIBACard(final String tetheredUserId, final int cardId, final String countryCode) {

    var customerAccountCardView = worldlineSoapTransformer.toCustomerAccountCardView(
        tetheredUserId, cardId, countryCode);
    CustomerAccountCardViewResponse response = fireWorldlineRequest(customerAccountCardView);

    worldlineResponseValidator.validate(new InnBCustomerAccountCardViewResponse(response));
    return worldlineMapper.toWorldlineCard(response.getResponse());
  }

  /**
   * Add a card for the provided tetheredUserId.
   *
   * @return the new card details
   */
  public WorldlineCardDetails addPIBACard(final String tetheredUserId, final String countryCode, final
  WorldlineAccountCardAddRequest worldlineAccountCardAddRequest) {

    var customerAccountCardAddType = worldlineMapper.toCustomerAccountCardAddType(worldlineAccountCardAddRequest);
    var customerAccountCardAdd = worldlineSoapTransformer.toCustomerAccountCardAdd(
        tetheredUserId, countryCode, customerAccountCardAddType);
    CustomerAccountCardAddResponse response = fireWorldlineRequest(customerAccountCardAdd);

    var innBWrappedResponse = new InnBCustomerAccountCardAddResponse(response);
    worldlineResponseValidator.validate(innBWrappedResponse);
    return worldlineMapper.toWorldlineCard(response.getResponse());
  }

  public WorldlineCardHolderUserDetails addCardHolder(
      WorldlineCardHolderUserRequest worldlineCardHolderUserRequest, String tetheredUserGuid, Scheme scheme, String clientIp) {
    var prop = Scheme.GB.equals(scheme) ? worldlineRestProperties.getGb()
        : worldlineRestProperties.getDe();

    var trustedPartnerCredentials = new TrustedPartnerCredentials(prop.getUsername(), prop.getPassword());
    var cardHolderUserResponse = new WorldlineCardHolderUserResponse();

    try {
      cardHolderUserResponse = worldlineClient.createCardHolderUser(
          worldlineUtils.serializeHeader(trustedPartnerCredentials),
          tetheredUserGuid, prop.getCultureCode(), prop.getCompanyNumber(),
          clientIp, worldlineCardHolderUserRequest
      );
    } catch (FeignException e) {
      if (e.contentUTF8().contains("InvalidMobile"))
        throw new CardHolderInvalidMobileException("Phone number did not pass WorldLine validation.", e);
      else
        log.error("Error creating card holder user", e);
    }

    return Optional.ofNullable(
            cardHolderUserResponse.getData())
        .filter(worldlineData -> Strings.isNotBlank(worldlineData.getTetheredUserGuid())
            && Strings.isNotBlank(worldlineData.getApiUserGuid()))
        .map(worldlineData -> WorldlineCardHolderUserDetails.builder()
            .tetheredUserGuid(worldlineData.getTetheredUserGuid())
            .apiUserGuid(worldlineData.getApiUserGuid())
            .build())
        .orElseThrow(() -> new CardHolderNotCreatedException("Error creating card holder user"));
  }

  /**
   * Updates a card identified by the given cardId parameter.
   *
   * @return updated card details
   */
  public WorldlineCardDetails updatePIBACard(final String tetheredUserId, final int cardId, final String countryCode, final
      WorldlineAccountCardUpdateRequest worldlineAccountCardUpdateRequest) {

      var customerAccountCardUpdateType = worldlineMapper.toCustomerAccountCardUpdateType(worldlineAccountCardUpdateRequest);
      customerAccountCardUpdateType.setCardId(cardId);
      var customerAccountCardUpdate = worldlineSoapTransformer.toCustomerAccountCardUpdate(
          tetheredUserId, countryCode, customerAccountCardUpdateType);
    CustomerAccountCardUpdateResponse response = fireWorldlineRequest(customerAccountCardUpdate);

    var innBWrappedResponse = new InnBCustomerAccountCardUpdateResponse(response);
    worldlineResponseValidator.validate(innBWrappedResponse);
    return worldlineMapper.toWorldlineCard(response.getResponse());
  }

  /**
   * Activates a card identified by the given cardId parameter.
   *
   * @return OK if the operation was successful, otherwise a PIBA exception will be thrown
   */
  public String activatePIBACard(final String tetheredUserId, final int cardId, final String countryCode) {

    var customerAccountCardActivate = worldlineSoapTransformer.toCustomerAccountCardActivate(
        tetheredUserId, cardId, countryCode);

    final CustomerAccountCardActivateResponse response = fireWorldlineRequest(customerAccountCardActivate);

    worldlineResponseValidator.validate(new InnBCustomerAccountCardActivateResponse(response));

    return response.getResponse().getResultCode();
  }

  /**
   * Cancels and replaces a card identified by the given cardId parameter.
   *
   * @return details of the replacement card
   */
  public WorldlineAccountCardCancelAndReplaceResponse cancelAndReplacePIBACard(
      final String tetheredUserId, final int cardId,
      final WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest) {

    var customerAccountCardCancelType = new CustomerAccountCardCancelRequestType();

    if (Objects.nonNull(
        worldlineAccountCardCancelAndReplaceRequest.getCardCorrespondenceAddress())) {
      customerAccountCardCancelType = worldlineMapper.toCustomerAccountCardDespatchChoiceType(
          worldlineAccountCardCancelAndReplaceRequest);
    }
    var request = worldlineSoapTransformer.toCustomerAccountCardCancelAndReplace(
        worldlineUtils.formatId(tetheredUserId), cardId,
        worldlineAccountCardCancelAndReplaceRequest,
        customerAccountCardCancelType.getDespatchChoice());

    final CustomerAccountCardCancelResponse response = fireWorldlineRequest(request);

    worldlineResponseValidator.validate(new InnBCustomerAccountCardCancelResponse(response));

    return worldlineMapper.toWorldlineCardCancelAndReplace(response.getResponse());
  }

  /**
   * Invite someone to be a registered card holder.
   *
   * @return OK if the operation was successful, otherwise a PIBA exception will be thrown
   */
  public String inviteCardHolder(final String tetheredUserGuid, final int cardId,
      final WorldlineAccountCardInviteRequest worldlineAccountCardInviteRequest) {

    var customerAccountCardInviteDetailsType = worldlineMapper.toCustomerAccountCardInviteDetailsType(
        worldlineAccountCardInviteRequest);

    var customerAccountCardInvite = worldlineSoapTransformer.toCustomerAccountCardInvite(tetheredUserGuid,
        cardId, worldlineAccountCardInviteRequest.getScheme(), customerAccountCardInviteDetailsType);

    final CustomerAccountCardInviteResponse response = fireWorldlineRequest(customerAccountCardInvite);

    worldlineResponseValidator.validate(new InnBCustomerAccountCardInviteResponse(response));

    return response.getResponse().getResultCode();
  }

  public CustomerAccountCardListResponseType getAccountCards(String countryCode,
      WorldlineAccountCardRequest worldlineAccountCardRequest) {
    String soapTetheredUserId = worldlineUtils.formatId(worldlineAccountCardRequest.getUserId());
    worldlineAccountCardRequest.setUserId(soapTetheredUserId);
    int tetheredUserSchemeCustomerId = getTetheredUserDetails(soapTetheredUserId, countryCode)
        .getTetheredUserDetails()
        .getCustomerAccountOverview()
        .getSchemeCustomerId();
    CustomerAccountCardListResponse response =
        fireWorldlineRequest(worldlineSoapTransformer.
            toGetCustomerAccountCardListSoapRequest(worldlineAccountCardRequest, countryCode,
                tetheredUserSchemeCustomerId));
    worldlineResponseValidator.validate(new InnBCustomerAccountCardListResponse(response));
    return response.getResponse();
  }

  public TetheredUserDetailsGetResponseType getTetheredUserDetails(final String tetheredUserId, final String countryCode) {
    final TetheredUserDetailsGetResponse response = fireWorldlineRequest(worldlineSoapTransformer
        .toGetTetheredUserDetailsSoapRequest(tetheredUserId, countryCode));
    worldlineResponseValidator.validate(new InnBTetheredUserDetailsGetResponse(response));
    return response.getResponse();
  }

  /**
   * Retrieves a list of details about cost centres belonging to the tetheredUserGuid.
   *
   * @return details of the cost centres
   */
  public List<CostCentreData> getCostCentreDetails(String tetheredUserGuid, String clientIp) {

    // Cost Centre concept is only applicable to PIEZ(DE) scheme
    var prop = worldlineRestProperties.getDe();
    var trustedPartnerCredentials = new TrustedPartnerCredentials(prop.getUsername(), prop.getPassword());

    var costCentreDetailsResponse = worldlineClient.costCentreDetails(
        worldlineUtils.serializeHeader(trustedPartnerCredentials),
        tetheredUserGuid, prop.getCultureCode(), prop.getCompanyNumber(),
        clientIp);

    return costCentreDetailsResponse.getData();
  }

  /**
   * Replaces the damaged/lost card with an identical one.
   *
   * @return details of the operation
   */
  public String replaceCard(String tetheredUserGuid, Scheme scheme, String clientIp,
      Integer cardId, WorldlineReplaceCardRequest worldlineReplaceCardRequest) {

    var prop = Scheme.GB.equals(scheme) ? worldlineRestProperties.getGb()
        : worldlineRestProperties.getDe();
    var trustedPartnerCredentials = new TrustedPartnerCredentials(prop.getUsername(), prop.getPassword());

    worldlineReplaceCardRequest.setUniqueCustomerCardId(cardId);

    return worldlineClient.replaceCard(
        worldlineUtils.serializeHeader(trustedPartnerCredentials),
        tetheredUserGuid, prop.getCultureCode(), prop.getCompanyNumber(),
        clientIp, worldlineReplaceCardRequest
    ).getData();
  }

  @SuppressWarnings("unchecked")
  private <W, R> R fireWorldlineRequest(W request) {
    log.info("Sending WorldlineRequest for {} = {} #####",
        request.getClass().getSimpleName(), worldlineUtils.serializeObject(request));
    var response = worldlineWebServiceTemplate.marshalSendAndReceive(
        worldLineProperties.getPiba().getService().getUrl(), request,
        worldLineWebServiceMessageCallback
    );
    log.info("Received WorldlineResponse for {} = {} #####",
        response.getClass().getSimpleName(), worldlineUtils.serializeObject(response));
    return (R) response;
  }

}
