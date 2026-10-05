package uk.co.whitbread.hotel.card.service.worldline.converter;

import uk.co.whitbread.hotel.card.model.WorldlineAccountCardCancelAndReplaceRequest;
import uk.co.whitbread.hotel.card.model.WorldlineAccountCardRequest;
import uk.co.whitbread.hotel.card.service.worldline.model.Scheme;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardActivate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAdd;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardAddType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardCancel;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardDespatchChoiceType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInvite;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardInviteDetailsType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardList;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdate;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardUpdateType;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountCardView;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountRegisteredUserList;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;

public interface IWorldlineSoapTransformer {

    CustomerAccountRegisteredUserList toGetAccountRegisteredUserListSoapRequest(String accountId, String countryCode,
        int tetheredUserSchemeCustomerId);

    CustomerAccountCardList toGetCustomerAccountCardListSoapRequest(
        WorldlineAccountCardRequest worldlineAccountCardRequest,
        String countryCode, int tetheredUserSchemeCustomerId);

    CustomerAccountCardView toCustomerAccountCardView(String accountId, int cardId, String countryCode);

    CustomerAccountCardUpdate toCustomerAccountCardUpdate(String accountId, String countryCode,
        CustomerAccountCardUpdateType customerAccountCardUpdateType);

    CustomerAccountCardActivate toCustomerAccountCardActivate(String accountId, int cardId, String countryCode);

    CustomerAccountCardAdd toCustomerAccountCardAdd(String accountId, String countryCode,
        CustomerAccountCardAddType customerAccountCardAddType);

    TetheredUserDetailsGet toGetTetheredUserDetailsSoapRequest(String accountId, String countryCode);

    CustomerAccountCardCancel toCustomerAccountCardCancelAndReplace(String tetheredUserId, int cardId,
        WorldlineAccountCardCancelAndReplaceRequest worldlineAccountCardCancelAndReplaceRequest,
        CustomerAccountCardDespatchChoiceType customerAccountCarCancelType);

    CustomerAccountCardInvite toCustomerAccountCardInvite(String tetheredUserGuid, int cardId,
        Scheme scheme, CustomerAccountCardInviteDetailsType customerAccountCardInviteDetailsType);
}
