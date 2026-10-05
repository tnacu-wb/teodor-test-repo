import { ROOM_TYPE } from '@whitbread-eos/api';

import {
  getBookingInformationData,
  getBookingInformationDataForGermanHotel,
  singleBookingInformationData,
  getHotelInformation,
  gbHotelInfo,
  getCountriesData,
  getPostCodeAddresesData,
  getPostCodeAddresesInfoData,
  getRoomSelectionData,
  defaultValuesWithGuests,
  defaultValues,
} from './guestDetails';

describe('guestDetails mock data', () => {
  describe('getBookingInformationData', () => {
    it('should have correct structure', () => {
      expect(getBookingInformationData).toBeDefined();
      expect(getBookingInformationData.isLoading).toBe(false);
      expect(getBookingInformationData.isError).toBe(false);
      expect(getBookingInformationData.data).toBeDefined();
      expect(getBookingInformationData.data.bookingInformation).toBeDefined();
    });

    it('should have 2 rooms in reservationByIdList', () => {
      const rooms = getBookingInformationData.data.bookingInformation.reservationByIdList;
      expect(rooms).toHaveLength(2);
      expect(rooms[0].roomStay.adultsNumber).toBe(2);
      expect(rooms[1].roomStay.adultsNumber).toBe(1);
    });

    it('should have correct room types', () => {
      const rooms = getBookingInformationData.data.bookingInformation.reservationByIdList;
      expect(rooms[0].roomStay.roomExtraInfo.roomType).toBe(ROOM_TYPE.PREMIER_PLUS);
      expect(rooms[1].roomStay.roomExtraInfo.roomType).toBe(ROOM_TYPE.PREMIER_PLUS);
    });

    it('should have hotelId', () => {
      expect(getBookingInformationData.data.bookingInformation.hotelId).toBe('MANOLD');
    });
  });

  describe('getBookingInformationDataForGermanHotel', () => {
    it('should have correct structure', () => {
      expect(getBookingInformationDataForGermanHotel).toBeDefined();
      expect(getBookingInformationDataForGermanHotel.isLoading).toBe(false);
      expect(getBookingInformationDataForGermanHotel.isError).toBe(false);
      expect(getBookingInformationDataForGermanHotel.data).toBeDefined();
    });

    it('should have 2 rooms', () => {
      const rooms =
        getBookingInformationDataForGermanHotel.data.bookingInformation.reservationByIdList;
      expect(rooms).toHaveLength(2);
      expect(rooms[0].roomStay.adultsNumber).toBe(1);
      expect(rooms[1].roomStay.adultsNumber).toBe(1);
    });

    it('should have German hotel ID', () => {
      expect(getBookingInformationDataForGermanHotel.data.bookingInformation.hotelId).toBe(
        'FRAMTI'
      );
    });
  });

  describe('singleBookingInformationData', () => {
    it('should have single room', () => {
      expect(singleBookingInformationData.reservationByIdList).toHaveLength(1);
    });

    it('should have 2 adults in room', () => {
      expect(singleBookingInformationData.reservationByIdList[0].roomStay.adultsNumber).toBe(2);
    });

    it('should have correct room type', () => {
      expect(
        singleBookingInformationData.reservationByIdList[0].roomStay.roomExtraInfo.roomType
      ).toBe(ROOM_TYPE.PREMIER_PLUS);
    });
  });

  describe('getHotelInformation', () => {
    it('should have correct structure', () => {
      expect(getHotelInformation).toBeDefined();
      expect(getHotelInformation.data).toBeDefined();
      expect(getHotelInformation.data.hotelInformation).toBeDefined();
    });

    it('should have UK address', () => {
      const address = getHotelInformation.data.hotelInformation.address;
      expect(address.country).toBe('United Kingdom (the)');
      expect(address.addressLine1).toBe('Sir Alex Ferguson Way');
      expect(address.postalCode).toBe('M17 1WS');
    });

    it('should have brand PID', () => {
      expect(getHotelInformation.data.hotelInformation.brand).toBe('PID');
    });

    it('should have hotel name', () => {
      expect(getHotelInformation.data.hotelInformation.name).toBe('Manchester Old Trafford');
    });

    it('should have announcement with escaped apostrophe', () => {
      const announcement = getHotelInformation.data.hotelInformation.announcement;
      expect(announcement.text).toContain("we're");
    });
  });

  describe('gbHotelInfo', () => {
    it('should have correct structure', () => {
      expect(gbHotelInfo).toBeDefined();
      expect(gbHotelInfo.data).toBeDefined();
      expect(gbHotelInfo.data.hotelInformation).toBeDefined();
    });

    it('should have UK address', () => {
      const address = gbHotelInfo.data.hotelInformation.address;
      expect(address.country).toBe('United Kingdom (the)');
      expect(address.addressLine1).toBe('Sir Alex Ferguson Way');
    });

    it('should have brand PI (non-German)', () => {
      expect(gbHotelInfo.data.hotelInformation.brand).toBe('PI');
    });
  });

  describe('getCountriesData', () => {
    it('should have countries array', () => {
      expect(getCountriesData.data.countries.countries).toBeDefined();
      expect(Array.isArray(getCountriesData.data.countries.countries)).toBe(true);
      expect(getCountriesData.data.countries.countries.length).toBeGreaterThan(0);
    });

    it('should include United Kingdom', () => {
      const uk = getCountriesData.data.countries.countries.find(
        (c: any) => c.countryName === 'United Kingdom (the)'
      );
      expect(uk).toBeDefined();
      expect(uk?.countryCode).toBe('GB');
    });

    it('should include Germany', () => {
      const germany = getCountriesData.data.countries.countries.find(
        (c: any) => c.countryName === 'Germany'
      );
      expect(germany).toBeDefined();
      expect(germany?.countryCode).toBe('DE');
    });
  });

  describe('getPostCodeAddresesData', () => {
    it('should have partialAddress array', () => {
      expect(getPostCodeAddresesData.data.partialAddress).toBeDefined();
      expect(Array.isArray(getPostCodeAddresesData.data.partialAddress)).toBe(true);
      expect(getPostCodeAddresesData.data.partialAddress.length).toBeGreaterThan(0);
    });

    it('should have valid address structure', () => {
      const address = getPostCodeAddresesData.data.partialAddress[0];
      expect(address).toHaveProperty('addressText');
      expect(address).toHaveProperty('id');
    });
  });

  describe('getPostCodeAddresesInfoData', () => {
    it('should have formattedAddress', () => {
      expect(getPostCodeAddresesInfoData.data.formattedAddress).toBeDefined();
    });

    it('should have valid UK address structure', () => {
      const addressInfo = getPostCodeAddresesInfoData.data.formattedAddress;
      expect(addressInfo).toHaveProperty('addressLine1');
      expect(addressInfo).toHaveProperty('addressLine4');
      expect(addressInfo).toHaveProperty('postalCode');
      expect(addressInfo.country).toBe('GB');
    });
  });

  describe('getRoomSelectionData', () => {
    it('should have roomSelection array', () => {
      expect(getRoomSelectionData.data.packages.packages.roomSelection).toBeDefined();
      expect(Array.isArray(getRoomSelectionData.data.packages.packages.roomSelection)).toBe(true);
    });

    it('should have room with correct structure', () => {
      const room = getRoomSelectionData.data.packages.packages.roomSelection[0];
      expect(room).toHaveProperty('packagesSelection');
      expect(Array.isArray(room.packagesSelection)).toBe(true);
    });
  });

  describe('defaultValuesWithGuests', () => {
    it('should have form fields', () => {
      expect(defaultValuesWithGuests).toBeDefined();
      expect(defaultValuesWithGuests).toHaveProperty('reasonForStay');
      expect(defaultValuesWithGuests).toHaveProperty('title');
      expect(defaultValuesWithGuests).toHaveProperty('firstName');
      expect(defaultValuesWithGuests).toHaveProperty('lastName');
      expect(defaultValuesWithGuests).toHaveProperty('email');
    });

    it('should have leadGuest array', () => {
      expect(Array.isArray(defaultValuesWithGuests.leadGuest)).toBe(true);
      expect(defaultValuesWithGuests.leadGuest.length).toBeGreaterThan(0);
    });

    it('should have default country GB', () => {
      expect(defaultValuesWithGuests.countryCode).toBe('GB');
    });

    it('should have accept future mailing as en', () => {
      expect(defaultValuesWithGuests.acceptFutureMailing).toBe('en');
    });
  });

  describe('defaultValues', () => {
    it('should have form fields', () => {
      expect(defaultValues).toBeDefined();
      expect(defaultValues).toHaveProperty('reasonForStay');
      expect(defaultValues).toHaveProperty('title');
      expect(defaultValues).toHaveProperty('firstName');
      expect(defaultValues).toHaveProperty('lastName');
      expect(defaultValues).toHaveProperty('email');
    });

    it('should have default country GB', () => {
      expect(defaultValues.countryCode).toBe('GB');
    });

    it('should have accept future mailing as en', () => {
      expect(defaultValues.acceptFutureMailing).toBe('en');
    });

    it('should have address fields', () => {
      expect(defaultValues).toHaveProperty('addressLine1');
      expect(defaultValues).toHaveProperty('postalCode');
      expect(defaultValues).toHaveProperty('cityName');
    });
  });

  describe('immutability', () => {
    it('should not mutate original data when cloned', () => {
      const clone1 = JSON.parse(JSON.stringify(getBookingInformationData));
      const clone2 = JSON.parse(JSON.stringify(getBookingInformationData));

      clone1.data.bookingInformation.hotelId = 'CHANGED';

      expect(clone2.data.bookingInformation.hotelId).toBe('MANOLD');
      expect(getBookingInformationData.data.bookingInformation.hotelId).toBe('MANOLD');
    });
  });

  describe('all exports', () => {
    it('should export all expected mock functions', () => {
      expect(getBookingInformationData).toBeDefined();
      expect(getBookingInformationDataForGermanHotel).toBeDefined();
      expect(singleBookingInformationData).toBeDefined();
      expect(getHotelInformation).toBeDefined();
      expect(gbHotelInfo).toBeDefined();
      expect(getCountriesData).toBeDefined();
      expect(getPostCodeAddresesData).toBeDefined();
      expect(getPostCodeAddresesInfoData).toBeDefined();
      expect(getRoomSelectionData).toBeDefined();
      expect(defaultValuesWithGuests).toBeDefined();
      expect(defaultValues).toBeDefined();
    });
  });
});
