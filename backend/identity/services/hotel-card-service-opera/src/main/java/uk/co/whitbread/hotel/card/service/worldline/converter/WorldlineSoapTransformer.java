package uk.co.whitbread.hotel.card.service.worldline.converter;

import static java.util.Objects.isNull;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardActivateRequestTypeBuilder;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardAddRequestTypeBuilder;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardCancelRequestTypeBuilder;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardInviteRequestTypeBuilder;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardUpdateRequestTypeBuilder;
import uk.co.whitbread.hotel.card.model.adapter.InnBCustomerAccountCardViewRequestTypeBuilder;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import uk.co.whitbread.hotel.card.utils.WorldlineUtils;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAdd;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancel;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardDespatchChoiceType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInvite;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardListRequestType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardView;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserListRequestType;
import worldline.mst.bsm.api.b2b.pi.data.PagingRequestWithoutSortType;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetRequestType;


@Slf4j
@Component
public final class WorldlineSoapTransformer extends WorldlineConfigLoaderTransformer implements
    IWorldlineSoapTransformer {

  public WorldlineSoapTransformer(WorldLineProperties worldLineProperties, WorldlineUtils worldlineUtils) {
    super(worldLineProperties, worldlineUtils);
  }

  @Override
  public CustomerAccountRegisteredUserList toGetAccountRegisteredUserListSoapRequest(String accountId, String countryCode,
      int tetheredUserSchemeCustomerId) {
    var customerAccount = new CustomerAccountRegisteredUserListRequestType();
    Scheme scheme = extractScheme(countryCode);

    log.info("Inside toGetAccountRegisteredUserListSoapRequest  and scheme- {}", scheme.name());
    log.info("getWorldLineRequestHeader(scheme) - {}", getWorldLineRequestHeader(scheme));

    customerAccount.setHeader(getWorldLineRequestHeader(scheme));
    customerAccount.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
    customerAccount.setTetheredUserGuid(accountId);
    customerAccount.setSchemeCustomerId(tetheredUserSchemeCustomerId);

    var accountRegisteredUserList = new CustomerAccountRegisteredUserList();
    accountRegisteredUserList.setRequest(customerAccount);

    return accountRegisteredUserList;
  }

  @Override
  public CustomerAccountCardList toGetCustomerAccountCardListSoapRequest(
      WorldlineAccountCardRequest worldlineAccountCardRequest,
      String countryCode, int tetheredUserSchemeCustomerId) {
    var customerAccount = new CustomerAccountCardListRequestType();
    Scheme scheme = extractScheme(countryCode);

    log.info("Inside toGetCustomerAccountCardListSoapRequest  and scheme- {}", scheme.name());
    log.info("getWorldLineRequestHeader(scheme) - {}", getWorldLineRequestHeader(scheme));
    log.info("user Name {} and password - {}", getWorldLineCredentialsType(scheme).getUsername(), getWorldLineCredentialsType(scheme).getPassword());

    customerAccount.setHeader(getWorldLineRequestHeader(scheme));
    customerAccount.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
    customerAccount.setTetheredUserGuid(worldlineAccountCardRequest.getUserId());
    customerAccount.setSchemeCustomerId(tetheredUserSchemeCustomerId);
    customerAccount.setShowOnlyMyCards(worldlineAccountCardRequest.isShowMyCards());
    customerAccount.setIncludeCancelledCards(worldlineAccountCardRequest.isIncludeCancelledCards());
    var pagingRequest = new PagingRequestWithoutSortType();
    pagingRequest.setPage(worldlineAccountCardRequest.getPageNumber());
    pagingRequest.setMaximumDisplayRows(worldlineAccountCardRequest.getMaxRows());
    customerAccount.setPagingRequest(pagingRequest);

    var accountRegisteredUserList = new CustomerAccountCardList();
    accountRegisteredUserList.setRequest(customerAccount);

    return accountRegisteredUserList;
  }

  public CustomerAccountCardView toCustomerAccountCardView(String accountId, int cardId, String countryCode) {
    log.info("Inside toCustomerAccountCardView and scheme");

    var cardViewRequestType = new InnBCustomerAccountCardViewRequestTypeBuilder();
    populateRequestHeaders(cardViewRequestType, accountId, countryCode);
    cardViewRequestType.setCardId(cardId);
    var customerAccountCardView = new CustomerAccountCardView();
    customerAccountCardView.setRequest(cardViewRequestType.getRequestType());

    log.info("Created [CustomerAccountCardView] request {} ", getWorldlineUtils().serializeObject(customerAccountCardView));
    return customerAccountCardView;
  }

  @Override
  public CustomerAccountCardActivate toCustomerAccountCardActivate(String accountId, int cardId,
      String countryCode) {

    log.info("Inside toCustomerAccountCardActivate and scheme");

    var requestTypeBuilder = new InnBCustomerAccountCardActivateRequestTypeBuilder();
    populateRequestHeaders(requestTypeBuilder, accountId, countryCode);
    requestTypeBuilder.setCardId(cardId);
    var customerAccountCardActivate = new CustomerAccountCardActivate();
    customerAccountCardActivate.setRequest(requestTypeBuilder.getRequestType());

    log.info("Created [CustomerAccountCardActivate] request {} ", getWorldlineUtils().serializeObject(customerAccountCardActivate));

    return customerAccountCardActivate;
  }

  public CustomerAccountCardUpdate toCustomerAccountCardUpdate(String accountId, String countryCode,
      CustomerAccountCardUpdateType customerAccountCardUpdateType) {

    Scheme scheme = extractScheme(countryCode);
    log.info("Inside toCustomerAccountCardUpdate and scheme- {}", scheme.name());

    // if the scheme is DE, set isUserConsent to 1, otherwise set it to 0
    customerAccountCardUpdateType.setIsUserConsent(Scheme.DE.equals(scheme) ? 1 : 0);

    var cardUpdateRequestType = new InnBCustomerAccountCardUpdateRequestTypeBuilder();
    populateRequestHeaders(cardUpdateRequestType, accountId, countryCode);
    cardUpdateRequestType.setCard(customerAccountCardUpdateType);
    var customerAccountCardUpdate = new CustomerAccountCardUpdate();
    customerAccountCardUpdate.setRequest(cardUpdateRequestType.getRequestType());

    log.info("Created [CustomerAccountCardUpdate] request {} ", getWorldlineUtils().serializeObject(customerAccountCardUpdate));
    return customerAccountCardUpdate;
  }

  public CustomerAccountCardAdd toCustomerAccountCardAdd(String accountId, String countryCode,
      CustomerAccountCardAddType customerAccountCardAddType) {

    Scheme scheme = extractScheme(countryCode);
    log.info("Inside toCustomerAccountCardAdd and scheme- {}", scheme.name());

    // if the scheme is DE, set isUserConsent to 1, otherwise set it to 0
    customerAccountCardAddType.setIsUserConsent(Scheme.DE.equals(scheme) ? 1 : 0);

    var cardAddRequestType = new InnBCustomerAccountCardAddRequestTypeBuilder();
    populateRequestHeaders(cardAddRequestType, accountId, countryCode);
    cardAddRequestType.setCard(customerAccountCardAddType);
    var customerAccountCardAdd = new CustomerAccountCardAdd();
    customerAccountCardAdd.setRequest(cardAddRequestType.getRequestType());

    log.info("Created [CustomerAccountCardAdd] request {} ", getWorldlineUtils().serializeObject(customerAccountCardAdd));
    return customerAccountCardAdd;
  }

  @Override
  public TetheredUserDetailsGet toGetTetheredUserDetailsSoapRequest(String accountId,
      String countryCode) {
    Scheme scheme = extractScheme(countryCode);

    var request = new TetheredUserDetailsGetRequestType();
    request.setHeader(getWorldLineRequestHeader(scheme));
    request.setTrustedPartnerCredentials(getWorldLineCredentialsType(scheme));
    request.setTetheredUserGuid(accountId);
    var tetheredUserDetailsGet = new TetheredUserDetailsGet();
    tetheredUserDetailsGet.setRequest(request);
    return tetheredUserDetailsGet;
  }

  @Override
  public CustomerAccountCardCancel toCustomerAccountCardCancelAndReplace(String tetheredUserId,
      int cardId,
      WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest,
      CustomerAccountCardDespatchChoiceType customerAccountCardCancelType) {

    log.info("Inside toCustomerAccountCardCancelAndReplace and scheme- {}", worldlineAccountCardCancelAndReplaceRequest.getScheme().name());
    var customerAccountCardCancel = new InnBCustomerAccountCardCancelRequestTypeBuilder();
    populateRequestHeaders(customerAccountCardCancel, tetheredUserId, worldlineAccountCardCancelAndReplaceRequest.getScheme().name());
    customerAccountCardCancel.setTetheredUserGuid(tetheredUserId);
    customerAccountCardCancel.setCardId(cardId);
    customerAccountCardCancel.setIssueReplacement(worldlineAccountCardCancelAndReplaceRequest.isIssueReplacement());
    customerAccountCardCancel.setAPIUserGuid(getWorldlineUtils().formatId(worldlineAccountCardCancelAndReplaceRequest.getApiUserGuid()));
    customerAccountCardCancel.setDespatchChoice(customerAccountCardCancelType);

    log.info("Created [CustomerAccountCardCancel] request {} ", getWorldlineUtils().serializeObject(customerAccountCardCancel));
    var customerAccountCardCancelRequest = new CustomerAccountCardCancel();
    customerAccountCardCancelRequest.setRequest(customerAccountCardCancel.getRequestType());
    return customerAccountCardCancelRequest;
  }

  @Override
  public CustomerAccountCardInvite toCustomerAccountCardInvite(String tetheredUserGuid, int cardId,
      Scheme scheme, CustomerAccountCardInviteDetailsType inviteDetails) {

    log.info("Inside toCustomerAccountCardInvite and scheme - {}", scheme.name());

    var requestTypeBuilder = new InnBCustomerAccountCardInviteRequestTypeBuilder();
    populateRequestHeaders(requestTypeBuilder, tetheredUserGuid, scheme.name());
    requestTypeBuilder.setCardId(cardId);
    requestTypeBuilder.setInviteeDetails(inviteDetails);
    var customerAccountCardInvite = new CustomerAccountCardInvite();
    customerAccountCardInvite.setRequest(requestTypeBuilder.getRequestType());

    log.info("Created [CustomerAccountCardInvite] request {} ", getWorldlineUtils().serializeObject(customerAccountCardInvite));

    return customerAccountCardInvite;
  }

  @Override
  public Scheme extractScheme(String languageCode) {
    Scheme scheme = EnumUtils.getEnum(Scheme.class, parseCountryCode(languageCode));
    log.info("Scheme in extractScheme - {}", scheme);
    return isNull(scheme) ? Scheme.GB : scheme;
  }

}
