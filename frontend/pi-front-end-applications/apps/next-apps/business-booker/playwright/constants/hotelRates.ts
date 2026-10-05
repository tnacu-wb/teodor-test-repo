/* eslint-disable prettier/prettier */
import { HotelRates } from '@WB-playwright/types';

export const HotelRate: Record<string, HotelRates> = {
  SEMI_FLEX: { name: 'Semi-Flex', ratePlanCode: 'SEMIFLEX', classification: 'A' },
  NON_FLEX: { name: 'Non-Flex', ratePlanCode: 'NONFLEX', classification: 'A' },
  ADVANCE: { name: 'Advance', ratePlanCode: 'ADVANCE', classification: 'A' },
  STANDARD: { name: 'Standard', ratePlanCode: 'STANDARD', classification: 'S' },
  BUSINESS_FLEX: { name: 'Business Flex', ratePlanCode: 'BUSIFLEX', classification: 'A' },
};

export const createRate = ({ name = 'Business Flex', ratePlanCode = 'BUSIFLEX', classification = 'A',
}: Partial<HotelRates> = {}): HotelRates => {
  return { name, ratePlanCode, classification };
};

export async function getHotelRateByName(name: string) {
  const hotelRates = Object.keys(HotelRate);
  const foundHotelRate = hotelRates.find((hotelRate) => HotelRate[hotelRate].name === name);
  if (!foundHotelRate) {
    throw new Error(`${name} hotel rate name is not found in the list!`);
  }
  return HotelRate[foundHotelRate];
}

export async function getHotelRateByRatePlanCode(ratePlanCode: string) {
  const hotelRates = Object.keys(HotelRate);
  const foundHotelRate = hotelRates.find(
    (hotelRate) => HotelRate[hotelRate].ratePlanCode === ratePlanCode
  );
  if (!foundHotelRate) {
    throw new Error(`${ratePlanCode} hotel rate plan code is not found in the list!`);
  }
  return HotelRate[foundHotelRate];
}
