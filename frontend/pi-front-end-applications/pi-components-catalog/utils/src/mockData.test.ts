import { bknReservationsMock, MOCK_SOFT_BUNDLES } from './mockData';

describe('mockData', () => {
  describe('bknReservationsMock', () => {
    it('should export an array of reservations', () => {
      expect(Array.isArray(bknReservationsMock)).toBe(true);
      expect(bknReservationsMock.length).toBeGreaterThan(0);
    });

    it('should have valid reservation structure', () => {
      bknReservationsMock.forEach((reservation) => {
        expect(reservation).toHaveProperty('reservationId');
        expect(reservation).toHaveProperty('roomStay');
        expect(reservation).toHaveProperty('reservationGuestList');
        expect(reservation).toHaveProperty('additionalGuestInfo');
      });
    });

    it('should have valid roomStay data', () => {
      bknReservationsMock.forEach((reservation) => {
        expect(reservation.roomStay).toHaveProperty('adultsNumber');
        expect(reservation.roomStay).toHaveProperty('childrenNumber');
        expect(reservation.roomStay).toHaveProperty('arrivalDate');
        expect(reservation.roomStay).toHaveProperty('departureDate');
        expect(reservation.roomStay).toHaveProperty('ratePlanCode');
        expect(reservation.roomStay).toHaveProperty('roomExtraInfo');
      });
    });

    it('should have valid guest list', () => {
      bknReservationsMock.forEach((reservation) => {
        expect(Array.isArray(reservation.reservationGuestList)).toBe(true);
        expect(reservation.reservationGuestList.length).toBeGreaterThan(0);
        reservation.reservationGuestList.forEach((guest) => {
          expect(guest).toHaveProperty('surName');
        });
      });
    });

    it('should have accessible room info', () => {
      bknReservationsMock.forEach((reservation) => {
        expect(reservation.roomStay.accessibleRoom).toHaveProperty('isAccessible');
        expect(reservation.roomStay.accessibleRoom).toHaveProperty('phoneNumber');
      });
    });
  });

  describe('MOCK_SOFT_BUNDLES', () => {
    it('should export an array of soft bundles', () => {
      expect(Array.isArray(MOCK_SOFT_BUNDLES)).toBe(true);
      expect(MOCK_SOFT_BUNDLES.length).toBeGreaterThan(0);
    });

    it('should have valid bundle structure', () => {
      MOCK_SOFT_BUNDLES.forEach((bundle) => {
        expect(bundle).toHaveProperty('packageCodes');
        expect(bundle).toHaveProperty('rate');
        expect(bundle).toHaveProperty('roomClass');
        expect(bundle).toHaveProperty('isOptional');
      });
    });

    it('should have valid package codes', () => {
      MOCK_SOFT_BUNDLES.forEach((bundle) => {
        expect(Array.isArray(bundle.packageCodes)).toBe(true);
        bundle.packageCodes.forEach((pkg) => {
          expect(pkg).toHaveProperty('id');
          expect(pkg).toHaveProperty('description');
          expect(pkg).toHaveProperty('price');
          expect(typeof pkg.id).toBe('string');
          expect(typeof pkg.description).toBe('string');
          expect(typeof pkg.price).toBe('number');
        });
      });
    });

    it('should have valid rate and roomClass arrays', () => {
      MOCK_SOFT_BUNDLES.forEach((bundle) => {
        expect(Array.isArray(bundle.rate)).toBe(true);
        expect(Array.isArray(bundle.roomClass)).toBe(true);
        expect(bundle.rate.length).toBeGreaterThan(0);
        expect(bundle.roomClass.length).toBeGreaterThan(0);
      });
    });

    it('should have boolean isOptional field', () => {
      MOCK_SOFT_BUNDLES.forEach((bundle) => {
        expect(typeof bundle.isOptional).toBe('boolean');
      });
    });

    it('should contain expected package IDs', () => {
      const bundle = MOCK_SOFT_BUNDLES[0];
      const packageIds = bundle.packageCodes.map((pkg) => pkg.id);
      expect(packageIds).toContain('BFADBF');
      expect(packageIds).toContain('FI24HR');
      expect(packageIds).toContain('HSCKIN');
    });

    it('should contain expected rates', () => {
      const bundle = MOCK_SOFT_BUNDLES[0];
      expect(bundle.rate).toContain('FLEXRATE');
      expect(bundle.rate).toContain('SEMIFLEX');
    });

    it('should contain expected room classes', () => {
      const bundle = MOCK_SOFT_BUNDLES[0];
      expect(bundle.roomClass).toContain('BIGWIN');
      expect(bundle.roomClass).toContain('DBLWIN');
      expect(bundle.roomClass).toContain('FMTRPL');
    });
  });
});
