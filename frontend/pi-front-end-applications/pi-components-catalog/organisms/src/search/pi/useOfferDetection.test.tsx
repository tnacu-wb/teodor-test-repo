import { renderHook } from '@testing-library/react';
import type { PromotionOffer } from '@whitbread-eos/api';
import { useRouter } from 'next/router';

import {
  useOfferDetection,
  isOfferActive,
  isPromotionActive,
  Promotion,
} from './useOfferDetection';

// Mock next/router
jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

const mockUseRouter = useRouter as jest.MockedFunction<typeof useRouter>;

// Helper to create mock PromotionOffer objects
const createMockOffer = (overrides: Partial<PromotionOffer>): PromotionOffer => ({
  page: '',
  cellCode: '',
  corpId: '',
  maxRooms: 1,
  numberOfNights: 1,
  ratePlanCode: '',
  ...overrides,
});

describe('useOfferDetection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Page-based offer detection', () => {
    it('should detect offer when offer.page matches current page URL on /offers/ pages', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
          corpId: '12345',
          maxRooms: 3,
          numberOfNights: 7,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('SUMMER2024');
      expect(result.current.corpId).toBe('12345');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should detect offer with partial path match on /offers/ pages', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/black-friday/hotels.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'black-friday',
          cellCode: 'BLACKFRI2024',
          maxRooms: 5,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('BLACKFRI2024');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should detect offer with full path in offer.page on /offers/ pages', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/winter-sale.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'offers/winter-sale',
          cellCode: 'WINTER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('WINTER2024');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should detect offer on DE /angebote/ pages', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/de/de/angebote/rabatt-travel-industry.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'rabatt-travel-industry',
          cellCode: 'TRAVEL2024',
          corpId: '12345',
          maxRooms: 3,
          numberOfNights: 5,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('TRAVEL2024');
      expect(result.current.corpId).toBe('12345');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should not match offer when page value is not in URL', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/hotels/london-tower-bridge',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should NOT match offer by page on non-/offers/ pages (optimization)', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/summer-sale.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      // Page matching is skipped on non-/offers/ pages for performance
      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should still match by URL params on non-/offers/ pages', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/some-page.html',
        query: { CELLCODES: 'PROMO2024' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'irrelevant',
          cellCode: 'PROMO2024',
          maxRooms: 3,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      // URL param matching still works everywhere
      expect(result.current.cellCodes).toBe('PROMO2024');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });
  });

  describe('URL parameter-based offer detection', () => {
    it('should detect offer by cellCode from URL query params', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { CELLCODES: 'PROMO2024' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'some-promo',
          cellCode: 'PROMO2024',
          maxRooms: 3,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('PROMO2024');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should detect offer by corpId from URL query params', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { CORPID: '99999' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'corporate',
          cellCode: 'CORP2024',
          corpId: '99999',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('CORP2024');
      expect(result.current.corpId).toBe('99999');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should NOT match offer with null corpId when URL has CORPID', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { CORPID: '99999' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'standard-offer',
          cellCode: 'STANDARD2024',
          corpId: null as any, // null corpId should not match
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      // Should not match because offer.corpId is null
      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBe('99999');
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should use URL params when no offer matches', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { CELLCODES: 'MANUAL123', CORPID: '88888' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'some-other-promo',
          cellCode: 'DIFFERENT',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('MANUAL123');
      expect(result.current.corpId).toBe('88888');
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should return null when no offer matches and no URL params', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'some-promo',
          cellCode: 'PROMO2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });
  });

  describe('CorpId handling', () => {
    it('should include corpId from matched offer', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/corporate-deal.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'corporate-deal',
          cellCode: 'CORP2024',
          corpId: '12345',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('CORP2024');
      expect(result.current.corpId).toBe('12345');
    });

    it('should handle null corpId values', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/regular-offer.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'regular-offer',
          cellCode: 'REGULAR2024',
          corpId: null as any,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('REGULAR2024');
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should handle undefined corpId values', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/basic-offer.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'basic-offer',
          cellCode: 'BASIC2024',
          corpId: undefined as any,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('BASIC2024');
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should use corpId from URL when no offer matches', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { CORPID: '99999' },
      } as any);

      const offers: any[] = [];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.corpId).toBe('99999');
    });
  });

  describe('Priority: Page match vs URL params', () => {
    it('should use offer cellCode when matched by page (priority)', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html',
        query: { CELLCODES: 'MANUAL' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('SUMMER2024');
    });

    it('should fallback to URL cellCode if offer has no cellCode (nullish coalescing)', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/test-offer.html',
        query: { CELLCODES: 'FALLBACK' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'test-offer',
          cellCode: undefined as any,
          maxRooms: 3,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('FALLBACK');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });

    it('should preserve empty string cellCode with nullish coalescing', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/empty-code.html',
        query: { CELLCODES: 'FALLBACK' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'empty-code',
          cellCode: '', // Empty string should be preserved, not replaced
          maxRooms: 3,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      // With nullish coalescing (??), empty string is preserved
      expect(result.current.cellCodes).toBe('');
      expect(result.current.matchedOffer).toEqual(offers[0]);
    });
  });

  describe('Edge cases', () => {
    it('should handle empty offers array', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html',
        query: {},
      } as any);

      const { result } = renderHook(() => useOfferDetection([]));

      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should handle undefined offers', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: {},
      } as any);

      const { result } = renderHook(() => useOfferDetection(undefined as any));

      expect(result.current.cellCodes).toBeNull();
      expect(result.current.corpId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should handle URL with query parameters', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html?foo=bar&baz=qux',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('SUMMER2024');
    });

    it('should handle URL with hash fragment', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html#section',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('SUMMER2024');
    });

    it('should return first matching offer when multiple matches exist', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/sale.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'sale',
          cellCode: 'FIRST',
          maxRooms: 3,
        }),
        createMockOffer({
          page: 'sale',
          cellCode: 'SECOND',
          maxRooms: 5,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.cellCodes).toBe('FIRST');
      expect(result.current.matchedOffer?.maxRooms).toBe(3);
    });
  });

  describe('Offer properties', () => {
    it('should return complete matched offer with all properties', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/employee-offer.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'employee-offer',
          cellCode: 'EMP01',
          corpId: '88888',
          maxRooms: 2,
          numberOfNights: 9,
          ratePlanCode: 'EMP01',
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.matchedOffer).toMatchObject({
        page: 'employee-offer',
        cellCode: 'EMP01',
        corpId: '88888',
        maxRooms: 2,
        numberOfNights: 9,
        ratePlanCode: 'EMP01',
      });
    });

    it('should handle offer with null corpId and ratePlanCode', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/standard-offer.html',
        query: {},
      } as any);

      const offers = [
        createMockOffer({
          page: 'standard-offer',
          cellCode: 'STANDARD2024',
          corpId: null as any,
          maxRooms: 5,
          numberOfNights: 3,
          ratePlanCode: null as any,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers));

      expect(result.current.matchedOffer).toMatchObject({
        page: 'standard-offer',
        cellCode: 'STANDARD2024',
        corpId: null,
        maxRooms: 5,
        numberOfNights: 3,
        ratePlanCode: null,
      });
      expect(result.current.corpId).toBeNull();
    });
  });
});

describe('isOfferActive', () => {
  it('should return true for valid offer object', () => {
    const offer = createMockOffer({
      page: 'test',
      cellCode: 'TEST',
    });

    expect(isOfferActive(offer)).toBe(true);
  });

  it('should return false for null', () => {
    expect(isOfferActive(null)).toBe(false);
  });

  it('should return false for undefined', () => {
    expect(isOfferActive(undefined as any)).toBe(false);
  });

  it('should return false for empty object', () => {
    expect(isOfferActive({} as any)).toBe(false);
  });

  it('should return true for offer with only cellCode property', () => {
    const offer = createMockOffer({
      cellCode: 'TEST',
    });

    expect(isOfferActive(offer)).toBe(true);
  });
});

// Helper to create mock Promotion objects
const createMockPromotion = (overrides: Partial<Promotion>): Promotion => ({
  numberOfNights: 7,
  maxRooms: 3,
  maxRoomsAmend: 2,
  promoCode: 'TESTPROMO',
  page: 'test-promo',
  enabled: true,
  ...overrides,
});

describe('isPromotionActive', () => {
  it('should return true for valid enabled promotion', () => {
    const promotion = createMockPromotion({
      page: 'summer-promo',
      promoCode: 'SUMMER10',
      enabled: true,
    });

    expect(isPromotionActive(promotion)).toBe(true);
  });

  it('should return false for null', () => {
    expect(isPromotionActive(null)).toBe(false);
  });

  it('should return false for undefined', () => {
    expect(isPromotionActive(undefined as any)).toBe(false);
  });

  it('should return false for disabled promotion', () => {
    const promotion = createMockPromotion({
      page: 'disabled-promo',
      promoCode: 'DISABLED',
      enabled: false,
    });

    expect(isPromotionActive(promotion)).toBe(false);
  });

  it('should return falsy for empty object', () => {
    expect(isPromotionActive({} as any)).toBeFalsy();
  });

  it('should return true for promotion with minimal properties', () => {
    const promotion = createMockPromotion({
      promoCode: 'MINIMAL',
      enabled: true,
    });

    expect(isPromotionActive(promotion)).toBe(true);
  });
});

describe('Promotion detection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Page-based promotion detection', () => {
    it('should detect promotion on GB promotion landing page', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          maxRooms: 3,
          numberOfNights: 9,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('ST10R');
      expect(result.current.hasActiveMatch).toBe(true);
      // Promotion is converted to PromotionOffer format
      expect(result.current.matchedOffer?.maxRooms).toBe(3);
      expect(result.current.matchedOffer?.numberOfNights).toBe(9);
      expect(result.current.matchedOffer?.page).toBe('10-off');
    });

    it('should detect promotion on DE promotion landing page (aktionen)', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/de/de/aktionen/sommer-angebot.html',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: 'sommer-angebot',
          promoCode: 'SUMMER2024',
          maxRooms: 5,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('SUMMER2024');
      expect(result.current.hasActiveMatch).toBe(true);
      expect(result.current.matchedOffer?.maxRooms).toBe(5);
    });

    it('should NOT detect promotion when not on promotion landing page', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBeNull();
      expect(result.current.hasActiveMatch).toBe(false);
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should NOT detect disabled promotion', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/disabled-promo.html',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: 'disabled-promo',
          promoCode: 'DISABLED',
          enabled: false,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });
  });

  describe('URL parameter-based promotion detection', () => {
    it('should detect promotion by PROMOID from URL query params', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { PROMOID: 'ST10R' },
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          maxRooms: 3,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('ST10R');
      expect(result.current.hasActiveMatch).toBe(true);
      expect(result.current.matchedOffer?.maxRooms).toBe(3);
    });

    it('should use URL PROMOID when no promotion matches', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: { PROMOID: 'MANUAL123' },
      } as any);

      const promotions = [
        createMockPromotion({
          page: 'some-promo',
          promoCode: 'DIFFERENT',
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('MANUAL123');
      expect(result.current.matchedOffer).toBeNull();
    });
  });

  describe('Priority: Page match vs URL params for promotions', () => {
    it('should use promotion promoCode when matched by page (priority)', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html',
        query: { PROMOID: 'MANUAL' },
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('ST10R');
    });

    it('should return first matching promotion when multiple match', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/sale.html',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: 'sale',
          promoCode: 'FIRST',
          maxRooms: 3,
          enabled: true,
        }),
        createMockPromotion({
          page: 'sale',
          promoCode: 'SECOND',
          maxRooms: 5,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('FIRST');
      expect(result.current.matchedOffer?.maxRooms).toBe(3);
    });
  });

  describe('Combined offers and promotions', () => {
    it('should detect both offer and promotion when on offers page with PROMOID', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/offers/summer-sale.html',
        query: { PROMOID: 'ST10R' },
      } as any);

      const offers = [
        createMockOffer({
          page: 'summer-sale',
          cellCode: 'SUMMER2024',
          maxRooms: 5,
        }),
      ];

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          maxRooms: 3,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection(offers, promotions));

      // Offer takes priority - matchedOffer contains offer data
      expect(result.current.cellCodes).toBe('SUMMER2024');
      expect(result.current.matchedOffer?.maxRooms).toBe(5);
      expect(result.current.hasActiveMatch).toBe(true);

      // promoId is still populated from promotion match
      expect(result.current.promoId).toBe('ST10R');
    });

    it('should return promotion converted to PromotionOffer format', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          maxRooms: 3,
          maxRoomsAmend: 2,
          numberOfNights: 9,
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      // Promotion is converted to PromotionOffer format
      expect(result.current.matchedOffer?.page).toBe('10-off');
      expect(result.current.matchedOffer?.maxRooms).toBe(3);
      expect(result.current.matchedOffer?.numberOfNights).toBe(9);
      // Empty strings for offer-specific fields
      expect(result.current.matchedOffer?.cellCode).toBe('');
      expect(result.current.matchedOffer?.corpId).toBe('');
    });
  });

  describe('Edge cases for promotions', () => {
    it('should handle empty promotions array', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html',
        query: {},
      } as any);

      const { result } = renderHook(() => useOfferDetection([], []));

      expect(result.current.promoId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should handle undefined promotions', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/search',
        query: {},
      } as any);

      const { result } = renderHook(() => useOfferDetection([], undefined as any));

      expect(result.current.promoId).toBeNull();
      expect(result.current.matchedOffer).toBeNull();
    });

    it('should handle URL with query parameters on promotion page', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html?foo=bar&baz=qux',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('ST10R');
    });

    it('should handle URL with hash fragment on promotion page', () => {
      mockUseRouter.mockReturnValue({
        asPath: '/gb/en/promotions/10-off.html#section',
        query: {},
      } as any);

      const promotions = [
        createMockPromotion({
          page: '10-off',
          promoCode: 'ST10R',
          enabled: true,
        }),
      ];

      const { result } = renderHook(() => useOfferDetection([], promotions));

      expect(result.current.promoId).toBe('ST10R');
    });
  });
});

describe('Locale-less promotion landing pages', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should detect promotion on /en/promotions/ path', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/en/promotions/spring-deal.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'spring-deal',
        promoCode: 'SPRING10',
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.promoId).toBe('SPRING10');
    expect(result.current.hasActiveMatch).toBe(true);
  });

  it('should detect promotion on /de/aktionen/ path', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/de/aktionen/herbst-angebot.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'herbst-angebot',
        promoCode: 'HERBST20',
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.promoId).toBe('HERBST20');
    expect(result.current.hasActiveMatch).toBe(true);
  });

  it('should detect promotion on bare /promotions/ path', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/promotions/winter-sale.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'winter-sale',
        promoCode: 'WINTER15',
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.promoId).toBe('WINTER15');
    expect(result.current.hasActiveMatch).toBe(true);
  });

  it('should detect promotion on bare /aktionen/ path', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/aktionen/sommer-sale.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'sommer-sale',
        promoCode: 'SOMMER25',
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.promoId).toBe('SOMMER25');
    expect(result.current.hasActiveMatch).toBe(true);
  });
});

describe('UnifiedOffer promotion-specific properties', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should include promoCode, maxRoomsAmend, and enabled on converted promotion', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/promotions/bundle-deal.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'bundle-deal',
        promoCode: 'BUNDLE99',
        maxRooms: 4,
        maxRoomsAmend: 3,
        numberOfNights: 5,
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.matchedOffer).toMatchObject({
      promoCode: 'BUNDLE99',
      maxRoomsAmend: 3,
      enabled: true,
      maxRooms: 4,
      numberOfNights: 5,
      cellCode: '',
      corpId: '',
      ratePlanCode: '',
      page: 'bundle-deal',
    });
  });
});

describe('Offer vs promotion fallback priority', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should use promotion when offers exist but none match current page', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/promotions/10-off.html',
      query: {},
    } as any);

    const offers = [
      createMockOffer({
        page: 'different-page',
        cellCode: 'NONMATCHING',
      }),
    ];

    const promotions = [
      createMockPromotion({
        page: '10-off',
        promoCode: 'ST10R',
        maxRooms: 3,
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection(offers, promotions));

    expect(result.current.matchedOffer?.promoCode).toBe('ST10R');
    expect(result.current.hasActiveMatch).toBe(true);
  });

  it('should set promoId to null when active offer wins and no PROMOID in URL', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/offers/corporate.html',
      query: {},
    } as any);

    const offers = [
      createMockOffer({
        page: 'corporate',
        cellCode: 'CORP01',
        corpId: '11111',
      }),
    ];

    const promotions = [
      createMockPromotion({
        page: 'corporate',
        promoCode: 'CORPPROMO',
        enabled: true,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection(offers, promotions));

    expect(result.current.matchedOffer?.cellCode).toBe('CORP01');
    expect(result.current.promoId).toBeNull();
    expect(result.current.hasActiveMatch).toBe(true);
  });

  it('should set hasActiveMatch to false when both offer and promotion are null', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/search',
      query: {},
    } as any);

    const { result } = renderHook(() => useOfferDetection([], []));

    expect(result.current.matchedOffer).toBeNull();
    expect(result.current.hasActiveMatch).toBe(false);
    expect(result.current.cellCodes).toBeNull();
    expect(result.current.corpId).toBeNull();
    expect(result.current.promoId).toBeNull();
  });

  it('should set hasActiveMatch to false when promotion is found but disabled', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/promotions/disabled-sale.html',
      query: {},
    } as any);

    const promotions = [
      createMockPromotion({
        page: 'disabled-sale',
        promoCode: 'DISABLED01',
        enabled: false,
      }),
    ];

    const { result } = renderHook(() => useOfferDetection([], promotions));

    expect(result.current.matchedOffer).toBeNull();
    expect(result.current.hasActiveMatch).toBe(false);
  });
});

describe('CorpId numeric comparison edge cases', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should NOT match when both corpIds are non-numeric strings (NaN !== NaN)', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/search',
      query: { CORPID: 'abc' },
    } as any);

    const offers = [
      createMockOffer({
        page: '',
        cellCode: 'NANTEST',
        corpId: 'abc',
      }),
    ];

    const { result } = renderHook(() => useOfferDetection(offers));

    // +('abc') is NaN, and NaN !== NaN, so no match
    expect(result.current.matchedOffer).toBeNull();
  });

  it('should match when corpIds differ in leading zeros (numeric comparison)', () => {
    mockUseRouter.mockReturnValue({
      asPath: '/gb/en/search',
      query: { CORPID: '099' },
    } as any);

    const offers = [
      createMockOffer({
        page: '',
        cellCode: 'LEADINGZERO',
        corpId: '99',
      }),
    ];

    const { result } = renderHook(() => useOfferDetection(offers));

    // +(099) === +(99) => 99 === 99, so match
    expect(result.current.cellCodes).toBe('LEADINGZERO');
    expect(result.current.matchedOffer).toEqual(offers[0]);
  });
});
