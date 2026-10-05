import axios from 'axios';
import { getAccessToken, getConfigForEnv } from './auth';
import { Environment } from './environments';
import { validateHotelId, validatePackageCode } from './validation';

export interface PackageSchedule {
  newTimeSpan: { startDate: string; endDate: string };
  schedulePrices: Array<{ unitPrice?: string; bucket?: string }>;
  newMinNights?: string;
  newMaxNights?: string;
  newMinPersons?: string;
  newMaxPersons?: string;
}

export interface PackageUpdatePayload {
  packageCode: {
    header: {
      primaryDetails: { description: string; shortDescription: string };
      transactionDetails: {
        allowance: boolean;
        packagePostingRules: {
          transactionCode: { code: string; type: string };
        };
      };
      postingAttributes: {
        inventoryItems: Array<unknown>;
        addToRate: boolean;
        printSeparateLine: boolean;
        sellSeparate: boolean;
        postNextDay: boolean;
        forecastNextDay: boolean;
        webBookable: boolean;
        formulaFunctionArguments: Array<unknown>;
        catering: boolean;
        postingRhythm: { type: string };
        priceCalculationRule: string;
      };
    };
    schedules: PackageSchedule[];
    hotelId: string;
    code: string;
    adjustOverlappingRange: boolean;
  };
}

export async function updatePackage(
  hotelId: string,
  packageCode: string,
  payload: PackageUpdatePayload,
  env: Environment
) {
  const safeHotelId = validateHotelId(hotelId);
  const safePackageCode = validatePackageCode(packageCode);
  const token = await getAccessToken(env);
  const config = getConfigForEnv(env);
  const url = `${config.baseUrl}/rtp/v0/hotels/${safeHotelId}/packages/${encodeURIComponent(safePackageCode)}`;

  const response = await axios.put(url, payload, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      'x-app-key': config.appKey,
      'x-hotelid': safeHotelId,
      'x-enterprise-id': config.enterpriseId,
    },
    timeout: 120000,
  });
  return response.data;
}
