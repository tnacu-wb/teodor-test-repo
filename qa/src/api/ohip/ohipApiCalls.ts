import type { APIRequestContext, APIResponse } from '@playwright/test';
import { Bookers } from '../../test-data/booker';

type JsonRecord = Record<string, unknown>;

interface OhipOAuthTokenResponse extends JsonRecord {
  access_token: string;
  token_type?: string;
  expires_in?: number;
}

interface OhipReservationIdentifier extends JsonRecord {
  id?: string;
  type?: string;
}

interface OhipReservationSummary extends JsonRecord {
  lastModifyDateTime?: string;
  reservationIdList?: OhipReservationIdentifier[];
  externalReferences?: Array<{ id?: string } & JsonRecord>;
}

interface OhipReservationsListResponse extends JsonRecord {
  reservations?: {
    reservationInfo?: OhipReservationSummary[];
  };
}

interface OhipInventoryCount extends JsonRecord {
  availableCount?: number;
  startDate?: string;
}

interface OhipRoomTypeInventory extends JsonRecord {
  inventoryCounts?: OhipInventoryCount[];
}

interface OhipHotelInventoryResponse extends JsonRecord {
  hotelInventories?: Array<{
    roomTypeInventories?: OhipRoomTypeInventory[];
  }>;
}

interface OhipReservationDetailsResponse extends JsonRecord {}
interface OhipProfileResponse extends JsonRecord {}
interface OhipPaymentMethodsResponse extends JsonRecord {}
interface OhipRoutingInstructionsResponse extends JsonRecord {}
interface OhipRateInfoResponse extends JsonRecord {}
interface OhipDepositFoliosResponse extends JsonRecord {}

interface OhipRuntimeConfig {
  ohipApiBaseUrl: string;
  ohipClientId: string;
  ohipClientSecret: string;
  ohipXApiKey: string;
}

/**
 * Methods for calling Oracle OHIP API.
 */
export class OhipApiCalls {
  [key: string]: unknown;

  private static oAuthToken: OhipOAuthTokenResponse | null = null;
  private static oAuthTokenTime: number | null = null;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): OhipApiCalls {
    return new OhipApiCalls(data);
  }

  private static getRequestContext(request?: APIRequestContext): APIRequestContext {
    if (request) {
      return request;
    }

    if (!global.page) {
      throw new Error('global.page is not available. Initialize Playwright page before using OhipApiCalls methods.');
    }

    return global.page.context().request;
  }

  private static getRuntimeOption(name: string): string | undefined {
    const options = global.browser?.options as Record<string, unknown> | undefined;
    const value = options?.[name];
    return typeof value === 'string' && value.length > 0 ? value : undefined;
  }

  private static getConfig() {
    const ohipApiBaseUrl = OhipApiCalls.getRuntimeOption('ohipApiBaseUrl');
    const ohipClientId = OhipApiCalls.getRuntimeOption('ohipClientId');
    const ohipClientSecret = OhipApiCalls.getRuntimeOption('ohipClientSecret');
    const ohipXApiKey = OhipApiCalls.getRuntimeOption('ohipXApiKey');

    if (!ohipApiBaseUrl || !ohipClientId || !ohipClientSecret || !ohipXApiKey) {
      throw new Error('Missing OHIP configuration. Ensure OHIP_API_BASE_URL, OHIP_CLIENT_ID, OHIP_CLIENT_SECRET and OHIP_X_API_KEY are set.');
    }

    return {
      ohipApiBaseUrl,
      ohipClientId,
      ohipClientSecret,
      ohipXApiKey,
    };
  }

  private static toIsoDayDate(input: Date | string): string {
    const date = typeof input === 'string' ? new Date(input) : input;
    const year = date.getUTCFullYear();
    const month = `${date.getUTCMonth() + 1}`.padStart(2, '0');
    const day = `${date.getUTCDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private static async sleep(milliseconds: number): Promise<void> {
    await new Promise((resolve) => setTimeout(resolve, milliseconds));
  }

  private static async parseJsonResponse(response: APIResponse): Promise<JsonRecord> {
    const text = await response.text();
    return text ? JSON.parse(text) as JsonRecord : {};
  }

  private static async executeWithRetry(
    callName: string,
    retries: number,
    action: () => Promise<APIResponse>,
  ): Promise<APIResponse> {
    let lastResponse: APIResponse | null = null;

    for (let retry = 0; retry < retries; retry++) {
      const response = await action();
      lastResponse = response;

      if (response.status() < 500) {
        return response;
      }

      console.log(`Retrying ${callName} call: [ ${retry + 1}/${retries} retry ]`);
      await OhipApiCalls.sleep(2000 * (retry + 1));
    }

    if (!lastResponse) {
      throw new Error(`${callName} call did not produce a response.`);
    }

    return lastResponse;
  }

  private static async authHeaders(hotelId?: string): Promise<Record<string, string>> {
    const token = await OhipApiCalls.getOhipOAuthToken({ useRetry: false });
    const { ohipXApiKey } = OhipApiCalls.getConfig();

    const headers: Record<string, string> = {
      Accept: 'application/json',
      'x-app-key': ohipXApiKey,
      Authorization: `Bearer ${token.access_token}`,
    };

    if (hotelId) {
      headers['x-hotelId'] = hotelId;
    }

    return headers;
  }

  /**
   * Generate the OHIP Bearer token
   * @param {Object} data the data object
   * @param {Boolean} data.useRetry true if it will retry the call
  * @returns {OhipOAuthTokenResponse} OAuth token response, cached for 30 minutes
   */
  static async getOhipOAuthToken({ useRetry = true }: { useRetry?: boolean } = {}): Promise<OhipOAuthTokenResponse> {
    const now = Date.now();
    if (OhipApiCalls.oAuthToken && OhipApiCalls.oAuthTokenTime && now <= OhipApiCalls.oAuthTokenTime) {
      return OhipApiCalls.oAuthToken;
    }

    const { ohipApiBaseUrl, ohipClientId, ohipClientSecret, ohipXApiKey } = OhipApiCalls.getConfig();
    const requestContext = OhipApiCalls.getRequestContext();
    const apiUrl = `${ohipApiBaseUrl}/oauth/v1/tokens`;
    const maxRetries = useRetry ? 10 : 1;
    const basicAuth = Buffer.from(`${ohipClientId}:${ohipClientSecret}`).toString('base64');

    const response = await OhipApiCalls.executeWithRetry('getOhipOAuthToken', maxRetries, () =>
      requestContext.post(apiUrl, {
        headers: {
          Accept: 'application/json',
          Authorization: `Basic ${basicAuth}`,
          'x-app-key': ohipXApiKey,
          enterpriseId: 'WHBPI',
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        form: {
          scope: 'urn:opc:hgbu:ws:__myscopes__',
          grant_type: 'client_credentials',
        },
      }),
    );

    if (!response.ok()) {
      const responseText = await response.text();
      console.error(`OHIP token request error. Status: ${response.status()}, URL: ${apiUrl}, Response: ${responseText}`);
      throw new Error(`OHIP token request failed with status ${response.status()} at ${apiUrl}`);
    }

    const body = await OhipApiCalls.parseJsonResponse(response) as OhipOAuthTokenResponse;
    if (!body.access_token) {
      throw new Error('OHIP token response did not contain access_token.');
    }
    OhipApiCalls.oAuthToken = body;
    OhipApiCalls.oAuthTokenTime = Date.now() + 30 * 60 * 1000;
    return body;
  }

  /**
   * Generate the OHIP Bearer token before running all the specs in parallel
   * @param {Object} data the data object
   * @param {OhipRuntimeConfig} data.config Playwright runtime config
   * @returns {OhipOAuthTokenResponse} OAuth token response
   */
  static async getOhipOAuthTokenAsync({ config }: { config: OhipRuntimeConfig }): Promise<OhipOAuthTokenResponse> {
    const { ohipApiBaseUrl, ohipClientId, ohipClientSecret, ohipXApiKey } = config;

    const requestContext = OhipApiCalls.getRequestContext();
    const apiUrl = `${ohipApiBaseUrl}/oauth/v1/tokens`;
    const basicAuth = Buffer.from(`${ohipClientId}:${ohipClientSecret}`).toString('base64');

    const response = await requestContext.post(apiUrl, {
      headers: {
        Accept: 'application/json',
        Authorization: `Basic ${basicAuth}`,
        'x-app-key': ohipXApiKey,
        enterpriseId: 'WHBPI',
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      form: {
        scope: 'urn:opc:hgbu:ws:__myscopes__',
        grant_type: 'client_credentials',
      },
    });

    if (!response.ok()) {
      const responseText = await response.text();
      console.error(`OHIP async token request error. Status: ${response.status()}, URL: ${apiUrl}, Response: ${responseText}`);
      throw new Error(`OHIP async token request failed with status ${response.status()} at ${apiUrl}`);
    }

    const body = await OhipApiCalls.parseJsonResponse(response) as OhipOAuthTokenResponse;
    if (!body.access_token) {
      throw new Error('OHIP async token response did not contain access_token.');
    }
    return body;
  }

  /**
   * Clear all the restrictions for a hotel
   * @param {String} hotelId the hotel id for which to clear the restrictions
   * @param {Date} date start date from when to start clearing the restrictions
  * @returns {Record<string, unknown>} restrictions response
   */
  static async clearAllRestrictionsForHotel(hotelId: string, date: Date = new Date()): Promise<JsonRecord> {
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/par/v1/hotels/${hotelId}/restrictions`;
    const dayDate = OhipApiCalls.toIsoDayDate(date);
    const payload = { hotelId, date: dayDate };

    const response = await requestContext.put(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
      data: payload,
    });

    if (!response.ok()) {
      throw new Error(`Failed to clear restrictions for ${hotelId}. Status: ${response.status()}`);
    }

    console.log(`Cleared all the restrictions for date "${dayDate}" for "${hotelId}" hotel id.`);
    return OhipApiCalls.parseJsonResponse(response);
  }

  /**
   * Clear all the restrictions for a hotel for a certain period
   * @param {String} hotelId the hotel id for which to clear the restrictions
   * @param {Date} startDate start date from when to start clearing the restrictions
   * @param {Number} daysPeriod the number of days for which to clear the restrictions
   */
  static async clearAllRestrictionsForHotelPeriod(hotelId: string, startDate: Date = new Date(), daysPeriod = 90): Promise<void> {
    console.log(`Clear all the restrictions from "${startDate}" for ${daysPeriod} days for "${hotelId}" hotel id.`);
    let date = new Date(startDate);
    let remainingDays = daysPeriod;

    while (remainingDays > 0) {
      try {
        await OhipApiCalls.clearAllRestrictionsForHotel(hotelId, date);
      } catch (error) {
        console.log('Error when clearing the restrictions:');
        console.log(error);
      }

      date = new Date(date.getTime() + 24 * 60 * 60 * 1000);
      remainingDays -= 1;
    }
  }

  /**
   * Cancel a hotel reservation
   * @param {String} hotelId the hotel id for which to cancel the reservation
   * @param {String} reservationId the reservation id used in OHIP to identify the reservation
   * @param {String} basketReferenceId the basket reference id, that will be used only for log
  * @returns {Record<string, unknown>} cancellation response
   */
  static async cancelHotelReservation(hotelId: string, reservationId: string, basketReferenceId = ''): Promise<JsonRecord> {
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations/${reservationId}/cancellations`;

    const payload = {
      reason: {
        code: 'CXL',
        description: 'Trip Cancelled',
      },
      reservations: {
        reservationIdList: {
          id: reservationId,
          type: 'Reservation',
        },
        hotelId,
      },
    };

    const response = await requestContext.post(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
      data: payload,
    });

    if (!response.ok()) {
      throw new Error(`Failed to cancel reservation ${reservationId} for ${hotelId}. Status: ${response.status()}`);
    }

    console.log(`Cancelled reservation with id "${reservationId}" and basket reference "${basketReferenceId}" for "${hotelId}" hotel id.`);
    return OhipApiCalls.parseJsonResponse(response);
  }

  /**
   * Cancel all the hotel reservations from Opera created more than 1 hour ago.
   * @param {String} hotelId the hotel id
   * @param {String} hotelName the hotel name
   * @param {String} bookerLastName the reservation booker last name for filtering the reservations
   * @param {Number} lastUpdatedReservationLimitInHours the hours number from when the reservations were last updated
   */
  static async cancelHotelReservationsForHotel(
    hotelId: string,
    hotelName = hotelId,
    bookerLastName = Bookers.DEFAULT_BOOKER.lastName,
    lastUpdatedReservationLimitInHours = 1,
  ): Promise<void> {
    let reservationsToCancel: JsonRecord;
    try {
      reservationsToCancel = await OhipApiCalls.getHotelReservations({
        hotelId,
        surname: bookerLastName,
        arrivalStartDate: new Date(),
        useRetry: false,
      });
    } catch {
      console.log(`Hotel "${hotelName}" might not be onboarded to Opera.`);
      return;
    }

    const reservations = ((reservationsToCancel.reservations as JsonRecord | undefined)?.reservationInfo as Array<JsonRecord> | undefined) ?? [];
    if (reservations.length === 0) {
      return;
    }

    const thresholdTime = Date.now() - lastUpdatedReservationLimitInHours * 60 * 60 * 1000;

    for (const reservation of reservations) {
      const lastModifyDateTime = new Date(String(reservation.lastModifyDateTime ?? '')).getTime();
      if (!Number.isFinite(lastModifyDateTime) || lastModifyDateTime >= thresholdTime) {
        continue;
      }

      const reservationIdList = (reservation.reservationIdList as Array<JsonRecord> | undefined) ?? [];
      for (const reservationId of reservationIdList) {
        if (reservationId.type !== 'Reservation') {
          continue;
        }

        try {
          const externalReferences = (reservation.externalReferences as Array<JsonRecord> | undefined) ?? [];
          const externalReferenceId = String(externalReferences[0]?.id ?? '');
          if (!externalReferenceId.includes('-')) {
            await OhipApiCalls.cancelHotelReservation(hotelId, String(reservationId.id ?? ''), externalReferenceId);
          }
        } catch (error) {
          console.log('Error when cancelling reservation: ');
          console.log(error);
        }
      }
    }
  }

  /**
   * Get the hotel reservations that can be cancelled.
   * @param {Object} data object
   * @param {String} data.hotelId the hotel id for which to cancel the reservation
   * @param {String} data.surname the booker last name
   * @param {Date|String} data.arrivalStartDate the arrival date of the reservation
   * @param {Number} data.limit the number of reservations to return
   * @param {Number} data.offset the index from total results from where to start returning the reservations
   * @param {Boolean} data.useRetry true if it will retry getOhipOAuthToken call
  * @returns {OhipReservationsListResponse} reservations eligible for cancellation
   */
  static async getHotelReservations({
    hotelId,
    surname,
    arrivalStartDate,
    limit = 1000,
    offset = 0,
    useRetry = true,
  }: {
    hotelId: string;
    surname: string;
    arrivalStartDate: Date | string;
    limit?: number;
    offset?: number;
    useRetry?: boolean;
  }): Promise<OhipReservationsListResponse> {
    console.log(`Get reservations for hotel id "${hotelId}", surname "${surname}", arrivalStartDate "${arrivalStartDate}", limit "${limit}", offset "${offset}".`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations`;

    await OhipApiCalls.getOhipOAuthToken({ useRetry });
    const query = new URLSearchParams({
      surname,
      arrivalStartDate: OhipApiCalls.toIsoDayDate(arrivalStartDate),
      limit: String(limit),
      offset: String(offset),
      reservationStatuses: 'DueIn',
    });

    const response = await requestContext.get(`${apiUrl}?${query.toString()}`, {
      headers: await OhipApiCalls.authHeaders(hotelId),
    });
    if (!response.ok()) {
      throw new Error(`Failed to get hotel reservations for ${hotelId}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipReservationsListResponse>;
  }

  /**
   * Get Reservations with lots of information (Staying guest, booker)
   * @param {Object} data object
   * @param {String} data.hotelId hotelId
   * @param {String} data.reservationId reservationId
  * @returns {OhipReservationDetailsResponse} reservation details from OHIP
   */
  static async getHotelReservationById({ hotelId, reservationId }: { hotelId: string; reservationId: string }): Promise<OhipReservationDetailsResponse> {
    console.log(`Get reservations ${reservationId} for hotel id "${hotelId}".`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations/${reservationId}?fetchInstructions=Reservation&fetchInstructions=Comments&fetchInstructions=Packages&fetchInstructions=InventoryItems&fetchInstructions=ReservationPaymentMethods&fetchInstructions=RoutingInstructions&fetchInstructions=ReservationPolicies`;

    const response = await OhipApiCalls.executeWithRetry('getHotelReservationById', 10, async () =>
      requestContext.get(apiUrl, { headers: await OhipApiCalls.authHeaders(hotelId) }),
    );

    if (!response.ok()) {
      throw new Error(`Failed to get reservation ${reservationId} for ${hotelId}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipReservationDetailsResponse>;
  }

  /**
   * Get Profile
   * @param {Object} data object
   * @param {String} data.hotelId hotelId
   * @param {String} data.profileId profileId
  * @returns {OhipProfileResponse} customer profile response from OHIP
   */
  static async getProfile({ profileId, hotelId }: { profileId: string; hotelId: string }): Promise<OhipProfileResponse> {
    console.log(`Get profile for ProfileId ${profileId}.`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/crm/v1/profiles/${profileId}?fetchInstructions=Communication&fetchInstructions=Profile`;

    const response = await requestContext.get(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
    });
    if (!response.ok()) {
      throw new Error(`Failed to get profile ${profileId}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipProfileResponse>;
  }

  /**
   * Retrieve payment method for specific hotelId and reservation code
   * @param {String} hotelId hotelId
   * @param {String} reservationCode ohip reservation code
  * @returns {OhipPaymentMethodsResponse} reservation payment methods from OHIP
   */
  static async getPaymentMethodForReservation(hotelId: string, reservationCode: string): Promise<OhipPaymentMethodsResponse> {
    console.log(`Get ohip payment method for hotelId: ${hotelId} and ohip reservation sourceId: ${reservationCode}`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations/${reservationCode}/paymentMethods?includeAmounts=true`;

    const response = await requestContext.get(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
    });
    if (!response.ok()) {
      throw new Error(`Failed to get payment methods for reservation ${reservationCode}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipPaymentMethodsResponse>;
  }

  /**
   * Retrieve routing instructions for specific hotel id and reservation code
   * @param {String} hotelId hotelId
   * @param {String} reservationCode reservation code
  * @returns {OhipRoutingInstructionsResponse} reservation routing instructions
   */
  static async getRoutingInstructions(hotelId: string, reservationCode: string): Promise<OhipRoutingInstructionsResponse> {
    console.log(`Get Routing Instructions for: ${hotelId} and ohip reservation sourceId: ${reservationCode}`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations/${reservationCode}/routingInstructions`;

    const response = await OhipApiCalls.executeWithRetry('getRoutingInstructions', 10, async () =>
      requestContext.get(apiUrl, {
        headers: {
          ...(await OhipApiCalls.authHeaders(hotelId)),
          id: reservationCode,
        },
      }),
    );

    if (!response.ok()) {
      throw new Error(`Failed to get routing instructions for reservation ${reservationCode}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipRoutingInstructionsResponse>;
  }

  /**
   * Retrieve payment details for specific hotelId and reservation code
   * @param {String} hotelId hotelId
   * @param {String} reservationCode ohip reservation code
   * @param {String} arrivalDate arrival date
  * @returns {OhipRateInfoResponse} reservation rate and payment information
   */
  static async getHotelReservationPaymentInfo(hotelId: string, reservationCode: string, arrivalDate: string): Promise<OhipRateInfoResponse> {
    console.log(`Get ohip payment details for: ${hotelId} and ohip reservation sourceId: ${reservationCode}, arrival date: ${arrivalDate}`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/rsv/v1/hotels/${hotelId}/reservations/rateInfo?summaryInfo=false&type=Reservation&id=${reservationCode}&detailDate=${arrivalDate}`;

    const response = await requestContext.get(apiUrl, {
      headers: {
        ...(await OhipApiCalls.authHeaders(hotelId)),
        summaryInfo: 'false',
        type: 'Reservation',
        detailDate: arrivalDate,
        id: reservationCode,
      },
    });
    if (!response.ok()) {
      throw new Error(`Failed to get reservation payment info for ${reservationCode}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipRateInfoResponse>;
  }

  /**
   * Retrieve hotel inventory for specific hotel id between specific dates for desired roomTypes
   * @param {String} hotelId hotelId
   * @param {String} dateRangeStart start date
   * @param {String} dateRangeEnd end date
   * @param {Array<String>} pmsRoomTypes array of room types
   * @param {Number} numberOfRooms number of rooms needed
  * @returns {OhipHotelInventoryResponse} daily inventory for requested room types
   */
  static async getHotelInventorySingleRoomType(
    hotelId: string,
    dateRangeStart: string,
    dateRangeEnd: string,
    pmsRoomTypes: string[],
    numberOfRooms: number,
  ): Promise<OhipHotelInventoryResponse> {
    console.log(`Get ohip hotel inventory for ${hotelId} between ${dateRangeStart} and ${dateRangeEnd} for ${pmsRoomTypes} room types.`);
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();

    const query = new URLSearchParams({
      dateRangeStart,
      dateRangeEnd,
      dailyInventory: 'True',
      includeTentativeInventory: 'false',
      roomCountRequested: String(numberOfRooms),
    });
    for (const roomType of pmsRoomTypes) {
      if (roomType) {
        query.append('roomTypes', roomType);
      }
    }

    const apiUrl = `${ohipApiBaseUrl}/inv/v1/hotels/${hotelId}/hotelInventory?${query.toString()}`;
    const response = await requestContext.get(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
    });
    if (!response.ok()) {
      throw new Error(`Failed to get hotel inventory for ${hotelId}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipHotelInventoryResponse>;
  }

  /**
   * Get deposit folios
   * @param {String} hotelId hotel id
   * @param {String} reservationCode opera reservation code
  * @returns {OhipDepositFoliosResponse} deposit folios response
   */
  static async getDepositFolios(hotelId: string, reservationCode: string): Promise<OhipDepositFoliosResponse> {
    const requestContext = OhipApiCalls.getRequestContext();
    const { ohipApiBaseUrl } = OhipApiCalls.getConfig();
    const apiUrl = `${ohipApiBaseUrl}/csh/v1/hotels/${hotelId}/depositFolio?id=${reservationCode}`;

    const response = await requestContext.get(apiUrl, {
      headers: await OhipApiCalls.authHeaders(hotelId),
    });
    if (!response.ok()) {
      throw new Error(`Failed to get deposit folios for ${reservationCode}. Status: ${response.status()}`);
    }

    return OhipApiCalls.parseJsonResponse(response) as Promise<OhipDepositFoliosResponse>;
  }
}