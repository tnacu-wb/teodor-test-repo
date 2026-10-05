import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { addFieldIfNotUndefined, addFieldsToMap, getTodayIsoDate } from '../../../utils/base-utils';
import { BookingInformationByBasketPipelineAction } from '../../booking-confirmation-pipeline/actions/BookingInformationByBasketPipelineAction';
import { ActionContextKeys } from '../../booking-confirmation-pipeline/actions/ActionContextKeys';
import { promoKind } from '../../promo-service/services/promo-batch-service';

export const globalConfig = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    addFieldIfNotUndefined(args.channel, 'channelId', finalMap);
    addFieldIfNotUndefined(args.brand, 'brand', finalMap);
    addFieldIfNotUndefined(args.country, 'country', finalMap);
    addFieldIfNotUndefined(args.language, 'language', finalMap);

    return await get(endpoints.GLOBAL_CONFIG, globalConfig, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const promotionsInformation = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const criteria = args?.promotionsInformationCriteria;
    let promoBookingInfo: any | null = null;
    let shouldFetchPromoConfig = true;
    criteria.isAmendRequest = false;
    if (criteria.basketReference) {
      promoBookingInfo = await addPromoBookingInfoFromBasket(criteria, context);
      if (promoBookingInfo && promoBookingInfo.operaPromotionCode) {
        criteria.promotionCode = promoBookingInfo.basketPromotionCode;
        criteria.operaPromoCode = promoBookingInfo.operaPromotionCode;
        criteria.promoKind = promoBookingInfo.basketPromoKind;
        criteria.isAmendRequest = true;
      } else {
        shouldFetchPromoConfig = false;
      }
    }

    //validate promocode from promo service
    // Temporary fallback for backward compatibility with older mobile apps.
    // TODO (CTECH-11143): Remove MOBILE default once app changes are complete.
    if (criteria.isPromoBox && !criteria.isAmendRequest) {
      let promoKindResponse = await promoKind(
        {
          promoCode: criteria.promotionCode,
          country: criteria.country,
          channel: criteria.channel,
          subChannel: criteria.subChannel ?? 'MOBILE'
        },
        context
      );
      criteria.operaPromoCode = promoKindResponse.operaPromoCode;
      criteria.promoKind = promoKindResponse.promoKind;
      criteria.uniquePromoCodeStatus = promoKindResponse.uniquePromoCodeStatus;
    }

    const promoConfigResponse = shouldFetchPromoConfig
      ? await getPromoConfig(criteria, finalMap, context)
      : {};

    // for Amend
    const promoBookingInfoResponse = {
      promotionCode:
        promoBookingInfo?.basketPromotionCode ?? promoBookingInfo?.operaPromotionCode ?? null,
      ratePlanCode: promoBookingInfo?.ratePlanCode ?? null
    };
    return { ...promoConfigResponse, promoBookingInfo: promoBookingInfoResponse };
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

const getPromoConfig = async (
  criteria: any,
  finalMap: { [key: string]: any },
  context: any
): Promise<any> => {
  const fieldsToAdd = [
    { key: 'country', value: criteria.country, required: true },
    { key: 'language', value: criteria.language, required: true },
    { key: 'channelId', value: criteria.channel, required: true },
    { key: 'brand', value: criteria.brand, required: true },
    { key: 'promotionCode', value: criteria.promotionCode, required: false },
    { key: 'bookingDate', value: criteria.bookingDate || getTodayIsoDate(), required: true },
    { key: 'stayStartDate', value: criteria.stayStartDate, required: true },
    { key: 'stayEndDate', value: criteria.stayEndDate, required: true },
    { key: 'promoKind', value: criteria.promoKind, required: false },
    { key: 'isAmendRequest', value: criteria.isAmendRequest, required: false },
    { key: 'isPromoBox', value: criteria.isPromoBox, required: false },
    { key: 'operaPromoCode', value: criteria.operaPromoCode, required: false },
    { key: 'uniquePromoCodeStatus', value: criteria.uniquePromoCodeStatus, required: false },
    { key: 'rateName', value: criteria.rateName, required: false },
    { key: 'roomClass', value: criteria.roomClass, required: false },
    { key: 'noOfRooms', value: criteria.noOfRooms, required: false }
  ];

  addFieldsToMap(fieldsToAdd, finalMap);

  return await get(endpoints.PROMO_CONFIG, 'promotionsInformation', finalMap, context);
};

const addPromoBookingInfoFromBasket = async (criteria: any, context: any): Promise<any | null> => {
  const pipelineManager = new PipelineManager();
  const actions = [new BookingInformationByBasketPipelineAction()];

  const pipelineContext = await pipelineManager.manage(actions, criteria, context);

  const bookingInfo = pipelineContext?.get(ActionContextKeys.BOOKING_INFORMATION_BY_BASKET);
  if (!bookingInfo) return null;

  const reservationList = bookingInfo.reservationByIdList;
  if (!Array.isArray(reservationList) || reservationList.length === 0) return null;

  const roomStayObj = reservationList[0].roomStay;
  if (!roomStayObj) return null;

  const { ratePlanCode, promotionCode } = roomStayObj;

  return {
    operaPromotionCode: promotionCode,
    ratePlanCode,
    basketPromotionCode: bookingInfo.promotionCode,
    basketPromoKind: bookingInfo.promoKind
  };
};
