import { randomUUID } from 'crypto';
import type { APIRequestContext, APIResponse } from '@playwright/test';
import { asObject, getBrowserOptions, isApiRequestContext } from './apiValueUtils';
import { getEnvironmentConfig } from '../../config/environments';
import { EncryptionUtils } from '../utils/encryptionUtils';
import { Cards, type CardDetails } from '../test-data/cards';
import { Hotels } from '../test-data/hotels';
import { Locales } from '../test-data/locales';
import { BillingDetails } from './response/billingDetails';
import { PlanetPaymentDetails } from './response/planetPaymentDetails';
import { Reservations } from './response/reservations';

type EntityApiResponse = Record<string, unknown>;

interface TokeniseCardParams {
  card?: CardDetails;
  cardNumber?: string;
  expiryYear?: string;
  expiryMonth?: string;
  requestId?: string;
  transactionReference?: string;
}

interface BookingGuest {
  givenName?: string;
  surName?: string;
  email?: string;
  nameTitle?: string;
  profileId?: string;
}

interface Booker {
  firstName?: string;
  lastName?: string;
  emailAddress?: string;
  title?: string;
  profileId?: string;
}

/**
 * Methods for accessing Premier Inn REST API resources.
 */
export class EntityApiCalls {
  [key: string]: unknown;

  private static readonly asObject = asObject;

  private static readonly isApiRequestContext = isApiRequestContext;

  private static getRequestContext(request?: APIRequestContext): APIRequestContext {
    if (request) {
      return request;
    }

    if (!global.page) {
      throw new Error('global.page is not available. Initialize Playwright page before using EntityApiCalls methods.');
    }

    return global.page.context().request;
  }

  private static readonly getBrowserOptions = getBrowserOptions;

  private static getLocaleData() {
    const localeString = String((global.browser?.options as Record<string, unknown> | undefined)?.locale ?? 'gb-en');
    return Locales.getLocaleByString(localeString);
  }

  private static getEntityApiBaseUrl(): string {
    const options = EntityApiCalls.getBrowserOptions();
    const configured = options.entityApiBaseUrl;
    const baseUrl = typeof configured === 'string' && configured.length > 0
      ? configured
      : getEnvironmentConfig(global.browser?.options).apiBaseUrl;
    return baseUrl.replace(/\/graphql\/?$/, '');
  }

  private static getApiKeyHeaders(headerName = 'x-api-key'): Record<string, string> {
    void headerName;
    return {};
  }

  private static withContext(args: unknown[]): { request: APIRequestContext; data: Record<string, unknown> } {
    const { request, values } = EntityApiCalls.splitArgs(args);
    return {
      request,
      data: EntityApiCalls.asObject(values[0]),
    };
  }

  private static splitArgs(args: unknown[]): { request: APIRequestContext; values: unknown[] } {
    const request = EntityApiCalls.isApiRequestContext(args[0]) ? args[0] : undefined;
    const values = EntityApiCalls.isApiRequestContext(args[0]) ? args.slice(1) : args;
    return { request: EntityApiCalls.getRequestContext(request), values };
  }

  private static async sleep(ms: number): Promise<void> {
    await new Promise((resolve) => setTimeout(resolve, ms));
  }

  private static async parseOptionalJsonResponse(response: APIResponse): Promise<EntityApiResponse> {
    const responseText = await response.text();
    return responseText.trim() ? EntityApiCalls.asObject(JSON.parse(responseText)) : {};
  }

  private static async withRetries<T>(operation: () => Promise<T>, maxRetries: number, isSuccess: (result: T) => boolean): Promise<T> {
    let lastResult: T | undefined;
    for (let retry = 0; retry < maxRetries; retry++) {
      const result = await operation();
      lastResult = result;
      if (isSuccess(result)) {
        return result;
      }
      if (retry < maxRetries - 1) {
        await EntityApiCalls.sleep(2000 * (retry + 1));
      }
    }

    if (lastResult !== undefined) {
      return lastResult;
    }

    throw new Error('Request did not return a result.');
  }

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): EntityApiCalls {
    return new EntityApiCalls(data);
  }

  /**
   * Get hotel payment information
   * @param {Object} data object data
   * @param {String} data.hotelId hotelId
   * @param {String} data.language language
   * @param {String} data.country country
  * @returns {Record<string, unknown>} hotel payment information response
   */
  static async getHotelPaymentInformation(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const locale = EntityApiCalls.getLocaleData();
    const hotelId = String(data.hotelId ?? '');
    const language = String(data.language ?? locale.language);
    const country = String(data.country ?? locale.country);
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/content/hotels/${hotelId}/payment-information?country=${encodeURIComponent(country)}&language=${encodeURIComponent(language)}`;
    const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
    if (!response.ok()) {
      throw new Error(`getHotelPaymentInformation failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Method that tokenises card information
  * @param {CardDetails} card card to be tokenised
  * @returns {string} tokenised card value
   */
  static async tokeniseCard(...args: unknown[]): Promise<string> {
    const { request, values } = EntityApiCalls.splitArgs(args);
    const data = EntityApiCalls.asObject(values[0]);
    const options = EntityApiCalls.getBrowserOptions();
    const endpoint = String(options.eckohWebhookEndpoint ?? '');
    if (!endpoint) {
      throw new Error('eckohWebhookEndpoint is not configured in browser options.');
    }
    const card = data.card
      ? EntityApiCalls.asObject(data.card)
      : (Object.keys(data).length > 0 ? data : EntityApiCalls.asObject(Cards.MASTERCARD));
    const rawNumber = String(card.number ?? data.cardNumber ?? '').replace(/\s/g, '');
    const expiryYear = String(card.expiryYear ?? data.expiryYear ?? '').slice(-2);
    const expiryMonth = String(card.expiryMonth ?? data.expiryMonth ?? '');
    const payload = {
      requestId: String(data.requestId ?? `${Date.now().toString(36)}${Math.random().toString(36).slice(2, 10)}`),
      cardNumber: rawNumber,
      transactionReference: String(data.transactionReference ?? '2222'),
      expiryYear,
      expiryMonth,
    };
    const response = await EntityApiCalls.withRetries(
      () => request.post(`${endpoint}/tokenise`, { headers: { Accept: '*/*' }, data: payload }),
      5,
      (result) => result.status() === 200,
    );
    if (!response.ok()) {
      throw new Error(`tokeniseCard failed with status ${response.status()}`);
    }
    const body = EntityApiCalls.asObject(await response.json());
    return String(body.token ?? '');
  }

  /**
   * Post request for eckoh payment confirmation
   * @param {Object} data object data
   * @param {String} data.paymentID paymentID
  * @param {CardDetails} data.card card
  * @param {String} data.token token
  * @returns {Record<string, unknown>} webhook response body, or an empty object when the endpoint returns no content
   */
  static async postEckohWebhookCallback(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const options = EntityApiCalls.getBrowserOptions();
    const endpoint = String(options.eckohWebhookEndpoint ?? '');
    if (!endpoint) {
      throw new Error('eckohWebhookEndpoint is not configured in browser options.');
    }
    const card = data.card ? EntityApiCalls.asObject(data.card) : EntityApiCalls.asObject(Cards.VISA_CARD);
    const paymentID = String(data.paymentID ?? data.paymentId ?? '');
    const payload = {
      result: 'success',
      result_code: 100,
      payment_id: paymentID,
      masked_pan: String(card.number ?? '').replace(/\s/g, ''),
      expiry: `${String(card.expiryMonth ?? '')}${String(card.expiryYear ?? '').slice(-2)}`,
      scheme: String(card.type ?? '').toLowerCase(),
      type: String(card.type ?? ''),
      reference: paymentID,
      token: data.token ?? card.token,
    };
    const response = await request.post(`${endpoint}/payments/eckoh/webhook`, { headers: { Accept: '*/*' }, data: payload });
    if (!response.ok()) {
      throw new Error(`postEckohWebhookCallback failed with status ${response.status()}`);
    }
    return EntityApiCalls.parseOptionalJsonResponse(response);
  }

  /**
   * Get Planet payment details
   * @param {String} paymentId payment id
   * @returns {PlanetPaymentDetails} payment details
   */
  static async getPlanetPaymentDetails(...args: unknown[]): Promise<PlanetPaymentDetails> {
    const { request, data } = EntityApiCalls.withContext(args);
    const options = EntityApiCalls.getBrowserOptions();
    const endpoint = String(options.eckohWebhookEndpoint ?? '');
    const paymentId = String(data.paymentId ?? data.paymentID ?? '');
    const url = `${endpoint}/payments/${paymentId}`;
    const response = await EntityApiCalls.withRetries(
      () => request.get(url, { headers: { Accept: '*/*' } }),
      5,
      (result) => result.status() === 200,
    );
    if (!response.ok()) {
      throw new Error(`getPlanetPaymentDetails failed with status ${response.status()} for URL ${url}`);
    }
    const body = EntityApiCalls.asObject(await response.json());
    return PlanetPaymentDetails.fromResponse(body);
  }

  /**
   * Get reservation information
   * @param {Object} data object data
   * @param {String} data.basketReference basketReference
  * @returns {Record<string, unknown>} reservation information response
   */
  static async getReservationInfo(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const basketReference = String(data.basketReference ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/reservations/basket/${basketReference}`;
    const response = await request.get(url, { headers: EntityApiCalls.getApiKeyHeaders() });
    if (!response.ok()) {
      throw new Error(`getReservationInfo failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Get reservation info by basket reference and hotel id
   * @param {Object} data object data
   * @param {String} data.basketReference basketReference
   * @param {String} data.hotelId basketReference
   * @returns {Reservations} the api call response body
   */
  static async getReservationInfoByBasketReferenceAndHotelId(...args: unknown[]): Promise<Reservations> {
    const { request, data } = EntityApiCalls.withContext(args);
    const basketReference = String(data.basketReference ?? '');
    const hotelId = String(data.hotelId ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/adp/v1/reservations?hotelId=${encodeURIComponent(hotelId)}&basketReference=${encodeURIComponent(basketReference)}&limit=20&offset=0`;
    const response = await request.get(url, { headers: EntityApiCalls.getApiKeyHeaders() });
    if (!response.ok()) {
      throw new Error(`getReservationInfoByBasketReferenceAndHotelId failed with status ${response.status()} for URL ${url}`);
    }
    const body = EntityApiCalls.asObject(await response.json());
    return Reservations.fromResponse({ reservations: body.reservations });
  }

  /**
   * Get Basket Information from Opera
   * @param {Object} data object data
   * @param {String} data.basketReference basketReference
  * @returns {Record<string, unknown>} Opera basket information response
   */
  static async getBasketInformation(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const basketReference = String(data.basketReference ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/baskets/${basketReference}`;
    const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
    if (!response.ok()) {
      throw new Error(`getBasketInformation failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Get reservation information billing details
   * @param {Object} data object data
   * @param {String} data.basketReference basketReference
   * @returns {BillingDetails} the billing details object
   */
  static async getReservationInfoBillingDetails(...args: unknown[]): Promise<BillingDetails> {
    const reservationInfo = EntityApiCalls.asObject(await EntityApiCalls.getReservationInfo(...args));
    const firstReservation = Array.isArray(reservationInfo.reservationByIdList)
      ? EntityApiCalls.asObject(reservationInfo.reservationByIdList[0])
      : {};
    return BillingDetails.fromResponse({ billingDetails: firstReservation.billing ?? {} });
  }

  /**
   * Retrieve VAT rules for specific country code and Package code
   * @param {String} countryCode country code
   * @param {String} packageCode package code
  * @returns {Record<string, unknown>} VAT rules response
   */
  static async getVatRules(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, values } = EntityApiCalls.splitArgs(args);
    const data = EntityApiCalls.asObject(values[0]);
    const countryCode = String(data.countryCode ?? data.vatRegion ?? values[0] ?? '');
    const packageCode = String(data.packageCode ?? data.pkgCodeArr ?? values[1] ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/rules/vat-codes?vatRegion=${encodeURIComponent(countryCode)}&pkgCodeArr=${encodeURIComponent(packageCode)}`;
    const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
    if (!response.ok()) {
      throw new Error(`getVatRules failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Retrieve VAT rules for specific country code and Package code
   * @param {Room} roomType room type object
  * @returns {Record<string, unknown>} room substitution response
   */
  static async getRoomSubstitutions(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const roomType = EntityApiCalls.asObject(data.roomType ?? data);
    const roomTypeObj = EntityApiCalls.asObject(roomType.roomType);
    const app = String(EntityApiCalls.getBrowserOptions().app ?? '').toLowerCase();
    const channel = app === 'pib' ? 'BB' : app.toUpperCase();
    const adults = String(roomType.adultsNumber ?? roomType.adults ?? '');
    const children = String(roomType.childrenNumber ?? roomType.children ?? '');
    const roomTypeId = String(roomTypeObj.id ?? roomType.roomType ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/rules/room-substitutions?adults=${encodeURIComponent(adults)}&children=${encodeURIComponent(children)}&pms=OP&roomType=${encodeURIComponent(roomTypeId)}&channel=${encodeURIComponent(channel)}`;
    const response = await EntityApiCalls.withRetries(
      () => request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } }),
      5,
      (result) => result.status() === 200,
    );
    if (!response.ok()) {
      throw new Error(`getRoomSubstitutions failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Post payment webhook
   * @param {Object} data data object
   * @param {String} data.basketReference basket reference id
   * @param {String} data.bookingReference booking reference id
   * @param {String} data.countryCode country code
   * @param {String} data.firstName first name of booker
   * @param {String} data.lastName last name of booker
  * @param {CardDetails} data.card card
  * @returns {Record<string, unknown>} payment webhook response
   */
  static async postPaymentWebhook(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const basketReference = String(data.basketReference ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/baskets/${basketReference}/payment-webhook`;
    const options = EntityApiCalls.getBrowserOptions();
    const locale = EntityApiCalls.getLocaleData();
    const card = EntityApiCalls.asObject(data.card);
    const token = card.token;
    const bookingReference = String(data.bookingReference ?? '');
    const countryCode = String(data.countryCode ?? '');
    if (!bookingReference || !countryCode) {
      throw new Error('postPaymentWebhook requires bookingReference and countryCode.');
    }
    const payload = {
      reference: basketReference,
      paymentId: String(data.paymentId ?? '78271787826D'),
      paymentStatus: String(data.paymentStatus ?? 'SUCCESS'),
      bookingReference,
      countryCode,
      language: locale.language,
      firstName: String(data.firstName ?? '').replace(' ', ''),
      lastName: String(data.lastName ?? '').replace(' ', ''),
      bookingChannel: String(data.bookingChannel ?? 'PI'),
      channel: String(data.channel ?? 'WEB'),
      paymentError: data.paymentError ?? { code: '123', description: 'Error desc' },
      last4Digits: String(card.number ?? '').replace(/\s/g, '').slice(-4),
      cardSchemeId: card.cardSchemeId,
      token,
      expiry: `${String(card.expiryMonth ?? '')}/${String(card.expiryYear ?? '').slice(-2)}`,
      fraudCheckDecision: String(data.fraudCheckDecision ?? 'ACCEPT'),
    };
    const headers: Record<string, string> = {
      Accept: '*/*',
      'Content-Type': 'application/json',
      Cookie: '',
    };
    headers['X-WHIT-API-KEY'] = EncryptionUtils.decode(String(options.paymentWebhookXWhitApiKey ?? ''));

    const maxRetries = 7;
    let response;
    for (let retry = 0; retry < maxRetries; retry++) {
      console.log(`[PaymentWebhook] Attempt ${retry + 1}/${maxRetries} - POST to ${url}`);
      console.log(`[PaymentWebhook] Payload keys: ${Object.keys(payload).join(', ')}`);
      response = await request.post(url, { headers, data: payload });
      if (response.status() === 202) {
        console.log(`[PaymentWebhook] Success on attempt ${retry + 1}`);
        break;
      }

      const responseBody = await response.text().catch(() => '<unreadable>');
      console.log(`postPaymentWebhook attempt ${retry + 1}/${maxRetries} returned status ${response.status()}: ${responseBody.slice(0, 300)}`);
      console.log(`Retrying call: [ ${retry + 1}/${maxRetries} retry ]`);
      await EntityApiCalls.sleep(2000 * (retry + 1));
    }

    if (!response || (response.status() !== 202 && !response.ok())) {
      const responseBody = await response?.text().catch(() => '<unreadable>') ?? '<no response>';
      throw new Error(
        `postPaymentWebhook failed with status ${response?.status()} for URL ${url}: ${responseBody.slice(0, 1000)}`
      );
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Post request for the Planet payment gateway webhook callback
   * @param {Object} data object data
   * @param {String} data.paymentId payment id used in the webhook url
   * @param {String} data.bookingReference booking reference id (ref param)
   * @param {String} data.firstName first name of booker
   * @param {String} data.lastName last name of booker
  * @param {CardDetails} data.card card
   * @param {String} data.txState transaction state
   * @param {String} data.returnCode return code
   * @param {String} data.fraudCheckDecision fraud check decision
   * @param {String} data.threeDSIndicator 3DS indicator
   * @param {String} data.authorisationCode authorisation code
   * @param {String} data.txId transaction id
  * @returns {Record<string, unknown>} the api call response body
   */
  static async postPlanetPaymentWebhook(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const options = EntityApiCalls.getBrowserOptions();
    const paymentId = String(data.paymentId ?? '78271787826D');
    const bookingReference = String(data.bookingReference ?? '');
    const card = EntityApiCalls.asObject(data.card);
    console.log(`Call Planet payment webhook for payment id: ${paymentId}`);
    // encode since the Planet session id can contain '/' and '+' which would otherwise break the URL path
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/payments/${encodeURIComponent(paymentId)}/webhook`;
    const payload = {
      ref: bookingReference,
      TxState: String(data.txState ?? 'CQ'),
      ReturnCode: String(data.returnCode ?? '0000'),
      FirstName: String(data.firstName ?? '').replace(' ', ''),
      LastName: String(data.lastName ?? '').replace(' ', ''),
      CardNumberFirst6: String(card.number ?? '').replace(/\s/g, '').slice(0, 6),
      card_pan_last4digits: String(card.number ?? '').replace(/\s/g, '').slice(-4),
      CardType: String(card.cardSchemeId ?? ''),
      CardTypeName: String(card.type ?? ''),
      TokenNo: String(card.token ?? ''),
      CardExpiry: `${String(card.expiryYear ?? '').slice(-2)}${String(card.expiryMonth ?? '')}`,
      fraud_check_decision: String(data.fraudCheckDecision ?? 'ACCEPT'),
      '3DSIndicator': String(data.threeDSIndicator ?? '3'),
      AuthorisationCode: String(data.authorisationCode ?? '110920'),
      TxID: String(data.txId ?? randomUUID()),
    };
    const headers: Record<string, string> = {
      Accept: '*/*',
      'Content-Type': 'application/x-www-form-urlencoded',
    };
    headers['x-whit-api-key'] = EncryptionUtils.decode(String(options.paymentWebhookXWhitApiKey ?? ''));

    // matches reference's superagent `.retry(7)`: retry on network errors and 5xx responses
    const maxRetries = 7;
    let response;
    for (let retry = 0; retry < maxRetries; retry++) {
      try {
        response = await request.post(url, { headers, form: payload });
      } catch (err) {
        console.log(JSON.stringify(err));
        if (retry === maxRetries - 1) throw err;
        continue;
      }
      if (response.status() === 204) break;
    }
    if (!response) {
      throw new Error(`postPlanetPaymentWebhook received no response for URL ${url}`);
    }
    if (!response.ok()) {
      const responseBody = await response.text().catch(() => '<unreadable>');
      throw new Error(`postPlanetPaymentWebhook failed with status ${response.status()} for URL ${url}: ${responseBody.slice(0, 1000)}`);
    }
    return EntityApiCalls.parseOptionalJsonResponse(response);
  }

  /**
   * Get Cancellation Policies
   * @param {Object} data object data
   * @param {String} data.hotelId hotel id
   * @param {String} data.basketReference basketReference
   * @param {String} data.ratePlanCode ratePlanCode
   * @param {String} data.arrivalDate arrival date (YYYY-MM-DD format)
  * @returns {Record<string, unknown>} cancellation policies response
   */
  static async getCancellationPolicies(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const hotelId = String(data.hotelId ?? Hotels.DEFAULT_HOTEL.id);
    const basketReference = String(data.basketReference ?? '');
    const ratePlanCode = String(data.ratePlanCode ?? 'FLEX');
    const arrivalDate = String(data.arrivalDate ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/reservations/cancellationPolicies?hotelId=${encodeURIComponent(hotelId)}&basketReference=${encodeURIComponent(basketReference)}&ratePlanCode=${encodeURIComponent(ratePlanCode)}&arrivalDate=${encodeURIComponent(arrivalDate)}`;
    const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
    if (!response.ok()) {
      throw new Error(`getCancellationPolicies failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Get deposit folios
   * @param {String} reservationCode opera reservation code
  * @returns {Record<string, unknown> | null} deposit folios response, if found
   */
  static async getDepositFolios(...args: unknown[]): Promise<EntityApiResponse | null> {
    const { request, values } = EntityApiCalls.splitArgs(args);
    const data = EntityApiCalls.asObject(values[0]);
    const reservationCode = String(data.reservationCode ?? data.operaReservationCode ?? values[0] ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/v1/baskets/deposit-folios/${reservationCode}`;
    const response = await request.get(url, { headers: { Accept: '*/*' } });
    if (!response.ok()) {
      return null;
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Get Booking Allowances
   * @param {Object} data object data
   * @param {String} data.hotelId hotel id
   * @param {String} data.reservationId opera reservation Id
  * @returns {Record<string, unknown>} booking allowances response
   */
  static async getBookingAllowances(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const hotelId = String(data.hotelId ?? Hotels.DEFAULT_GERMAN_HOTEL.id);
    const reservationId = String(data.reservationId ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/ohip/v1/reservations/bookingAllowances?hotelId=${encodeURIComponent(hotelId)}&reservationId=${encodeURIComponent(reservationId)}`;
    const response = await EntityApiCalls.withRetries(
      () => request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } }),
      5,
      (result) => result.status() === 200,
    );
    if (!response.ok()) {
      throw new Error(`getBookingAllowances failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Get reservations by ids
   * @param {Object} data object data
   * @param {String} data.hotelId hotel id
   * @param {String} data.reservationIds opera reservations Id
   * @param {String} data.rateInfoNeeded opera rate info needed
  * @returns {Record<string, unknown>} reservations response
   */
  static async getReservationsByIds(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, data } = EntityApiCalls.withContext(args);
    const hotelId = String(data.hotelId ?? Hotels.DEFAULT_GERMAN_HOTEL.id);
    const reservationIds = String(data.reservationIds ?? '');
    const rateInfoNeeded = String(data.rateInfoNeeded ?? '');
    const url = `${EntityApiCalls.getEntityApiBaseUrl()}/ohip/v1/reservations/basket?rateInfoNeeded=${encodeURIComponent(rateInfoNeeded)}&reservationIds=${encodeURIComponent(reservationIds)}&hotelId=${encodeURIComponent(hotelId)}`;
    const response = await request.get(url, { headers: { Accept: 'application/json', ...EntityApiCalls.getApiKeyHeaders() } });
    if (!response.ok()) {
      const body = EntityApiCalls.asObject(await response.json().catch(() => ({})));
      const debugMessage = body.debugMessage;
      if (typeof debugMessage === 'string' && debugMessage.length > 0) {
        throw new Error(debugMessage);
      }
      throw new Error(`getReservationsByIds failed with status ${response.status()} for URL ${url}`);
    }

    return EntityApiCalls.asObject(await response.json());
  }

  /**
   * Validate that Guest from reservation guest has the same data as Booker from Guest Details
   * @param {Object} data object data
  * @param {BookingGuest} data.reservationGuest guest from reservation response
  * @param {Booker} data.booker booker from guest details
  * @returns {boolean} whether the guest matches the booker
   */
  static async validateGuestInformationAgainstBooker(...args: unknown[]): Promise<boolean> {
    const data = EntityApiCalls.asObject(EntityApiCalls.isApiRequestContext(args[0]) ? args[1] : args[0]);
    const reservationGuest = EntityApiCalls.asObject(data.reservationGuest);
    const booker = EntityApiCalls.asObject(data.booker);
    const givenName = String(reservationGuest.givenName ?? '').replace(/\s/g, '').toLowerCase();
    const firstName = String((booker.firstName ?? booker.givenName ?? '')).replace(/\s/g, '').toLowerCase();
    const surName = String(reservationGuest.surName ?? '');
    const bookerLastName = String(booker.lastName ?? booker.surName ?? '');
    const email = String(reservationGuest.email ?? '');
    const bookerEmail = String(booker.emailAddress ?? booker.email ?? '');

    if (givenName !== firstName) {
      throw new Error(`Booker first name mismatch: expected ${firstName}, got ${givenName}`);
    }
    if (surName !== bookerLastName) {
      throw new Error(`Booker last name mismatch: expected ${bookerLastName}, got ${surName}`);
    }
    if (email !== bookerEmail) {
      throw new Error(`Booker email mismatch: expected ${bookerEmail}, got ${email}`);
    }

    const reservationProfileId = String(reservationGuest.profileId ?? '');
    const bookerProfileId = String(booker.profileId ?? '');
    if (reservationProfileId !== bookerProfileId && reservationProfileId.length === 0) {
      throw new Error('Booker profileId mismatch and reservation profileId is empty');
    }
    return true;
  }

  /**
   * Validate that Guest from reservation guest has the same data as Booker from Guest Details by index 
   * @param {Object} data object data
  * @param {BookingGuest} data.reservationGuest guest from reservation response
  * @param {Booker} data.booker booker from guest details
   * @param {String} data.replaceTitle Booker from Guest Details
   * @param {String} data.replaceFirstName Booker from Guest Details
   * @param {String} data.replaceLastName Booker from Guest Details
   * @param {String} data.index index
  * @returns {boolean} whether the guest matches the booker after replacements
   */
  static async validateGuestInformationAgainstBookerByIndexWithReplacements(...args: unknown[]): Promise<boolean> {
    const data = EntityApiCalls.asObject(EntityApiCalls.isApiRequestContext(args[0]) ? args[1] : args[0]);
    const reservationGuest = EntityApiCalls.asObject(data.reservationGuest);
    const booker = EntityApiCalls.asObject(data.booker);
    const index = Number(data.index ?? 0);
    const expectedTitle = String(index === 0 ? (booker.title ?? '') : (data.replaceTitle ?? '')).toLowerCase();
    const actualTitle = String(reservationGuest.nameTitle ?? '').replace(/\s/g, '').toLowerCase();
    const expectedFirstName = String(index <= 1 ? (booker.firstName ?? '') : (data.replaceFirstName ?? ''));
    const expectedLastName = String(index <= 2 ? (booker.lastName ?? '') : (data.replaceLastName ?? ''));
    const actualFirstName = String(reservationGuest.givenName ?? '');
    const actualLastName = String(reservationGuest.surName ?? '');

    if (actualTitle !== expectedTitle) {
      throw new Error(`Booker title mismatch at index ${index}: expected ${expectedTitle}, got ${actualTitle}`);
    }
    if (expectedFirstName !== actualFirstName) {
      throw new Error(`Booker first name mismatch at index ${index}: expected ${expectedFirstName}, got ${String(reservationGuest.givenName ?? '')}`);
    }
    if (expectedLastName !== actualLastName) {
      throw new Error(`Booker last name mismatch at index ${index}: expected ${expectedLastName}, got ${actualLastName}`);
    }

    const reservationProfileId = String(reservationGuest.profileId ?? '');
    const bookerProfileId = String(booker.profileId ?? '');
    if (reservationProfileId !== bookerProfileId && reservationProfileId.length === 0) {
      throw new Error('Booker profileId mismatch and reservation profileId is empty');
    }
    return true;
  }

  /**
   * Retrieve hotels from snowdrop
   * @param {number} longitude location longitude
   * @param {number} latitude location latitude
   * @param {string} radius region radius
  * @returns {Record<string, unknown>} Snowdrop hotels response
   */
  static async getHotelsFromSnowdrop(...args: unknown[]): Promise<EntityApiResponse> {
    const { request, values } = EntityApiCalls.splitArgs(args);
    const data = EntityApiCalls.asObject(values[0]);
    const options = EntityApiCalls.getBrowserOptions();
    const longitude = String(data.longitude ?? values[0] ?? '');
    const latitude = String(data.latitude ?? values[1] ?? '');
    const radiusValue = data.radius ?? values[2];
    const radius = radiusValue === undefined ? '' : `&radius=${encodeURIComponent(String(radiusValue))}`;
    const url = `${String(options.snowdropUrl ?? '')}?longitude=${encodeURIComponent(longitude)}&latitude=${encodeURIComponent(latitude)}${radius}`;
    const response = await EntityApiCalls.withRetries(
      () => request.get(url),
      5,
      (result) => result.status() === 200,
    );
    if (!response.ok()) {
      throw new Error(`getHotelsFromSnowdrop failed with status ${response.status()} for URL ${url}`);
    }
    return EntityApiCalls.asObject(await response.json());
  }

}
