import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { PromotionsInformation } from '@whitbread-eos/utils';

import PromotionsNotification from './PromotionsNotification.component';

function mockLocation(search: string) {
  Object.defineProperty(window, 'location', {
    value: { search },
    writable: true,
  });
}

const roomRates = [
  {
    ratePlanCode: 'FLEXRATE',
    promotionCode: null,
    cellCode: null,
    roomTypes: [
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'PPLDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PP',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 216,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 72 },
                { date: '2025-12-19', netPrice: 66 },
                { date: '2025-12-20', netPrice: 78 },
              ],
            },
            numberOfRoomsAvailable: 13,
          },
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 216,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 72 },
                { date: '2025-12-19', netPrice: 66 },
                { date: '2025-12-20', netPrice: 78 },
              ],
            },
            numberOfRoomsAvailable: 167,
          },
          {
            pmsRoomType: 'VPPDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PV',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 216,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 72 },
                { date: '2025-12-19', netPrice: 66 },
                { date: '2025-12-20', netPrice: 78 },
              ],
            },
            numberOfRoomsAvailable: 10,
          },
        ],
      },
    ],
  },
  {
    ratePlanCode: 'ADVANCE',
    promotionCode: null,
    cellCode: null,
    roomTypes: [
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'PPLDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PP',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 176,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 60 },
                { date: '2025-12-19', netPrice: 53 },
                { date: '2025-12-20', netPrice: 63 },
              ],
            },
            numberOfRoomsAvailable: 13,
          },
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 176,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 60 },
                { date: '2025-12-19', netPrice: 53 },
                { date: '2025-12-20', netPrice: 63 },
              ],
            },
            numberOfRoomsAvailable: 167,
          },
          {
            pmsRoomType: 'VPPDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PV',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 176,
              baseRateAmount: null,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 60 },
                { date: '2025-12-19', netPrice: 53 },
                { date: '2025-12-20', netPrice: 63 },
              ],
            },
            numberOfRoomsAvailable: 10,
          },
        ],
      },
    ],
  },
  {
    ratePlanCode: 'STDDIS20',
    promotionCode: 'ST20RU',
    cellCode: null,
    roomTypes: [
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'PPLDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PP',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 105.6,
              baseRateAmount: 132,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 36.8 },
                { date: '2025-12-19', netPrice: 31.2 },
                { date: '2025-12-20', netPrice: 37.6 },
              ],
            },
            numberOfRoomsAvailable: 13,
          },
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 105.6,
              baseRateAmount: 132,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 36.8 },
                { date: '2025-12-19', netPrice: 31.2 },
                { date: '2025-12-20', netPrice: 37.6 },
              ],
            },
            numberOfRoomsAvailable: 167,
          },
          {
            pmsRoomType: 'VPPDBL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'PV',
            specialRequests: ['SING'],
            roomPriceBreakdown: {
              totalNetAmount: 105.6,
              baseRateAmount: 132,
              currencyCode: 'GBP',
              packageCode: null,
              packageAmount: null,
              dailyPrices: [
                { date: '2025-12-18', netPrice: 36.8 },
                { date: '2025-12-19', netPrice: 31.2 },
                { date: '2025-12-20', netPrice: 37.6 },
              ],
            },
            numberOfRoomsAvailable: 10,
          },
        ],
      },
    ],
  },
];

const basePromo: PromotionsInformation = {
  showPromo: true,
  isWithinPromoWindow: true,
  promoBannerColour: 'red',
  promoBannerIcon: '/test/icon.svg',
  promoBannerTitle: '<span>Promo Title</span>',
  promoBannerSubtitle: '<span>Promo Subtitle</span>',
  promoInvalidMessage: null,
  promoExpiredMessage: null,
  promotionCode: 'ST20RU',
  landingPage: '/promo-landing',
  promoAmendMessage: null,
  promoBookingInfo: {
    promotionCode: null,
    ratePlanCode: null,
  },
  promoBannerVisibility: ['HDP', 'SRP'],
};

describe('PromotionsNotification', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('returns null if CELLCODES or CORPID is present in URL', () => {
    mockLocation('?CELLCODES=123');
    render(<PromotionsNotification promotionBannerData={basePromo} viewType="banner" />);
    expect(screen.queryByTestId('PromoContainer')).not.toBeInTheDocument();
  });

  it('renders banner when no CELLCODES and promo is enabled', async () => {
    mockLocation('?PROMOID=123');
    render(<PromotionsNotification promotionBannerData={basePromo} viewType="banner" page="SRP" />);

    expect(await screen.findByTestId('PromoContainer')).toBeInTheDocument();
    expect(await screen.findByText(/Promo Title/i)).toBeInTheDocument();
    expect(await screen.findByText(/Promo Subtitle/i)).toBeInTheDocument();
  });

  it('renders warning when invalid promo and viewType=warning', async () => {
    mockLocation('?PROMOID=ST20RU');
    const promo = { ...basePromo, promoInvalidMessage: '<span>Invalid!</span>' };

    render(<PromotionsNotification promotionBannerData={promo} viewType="warning" page="SRP" />);

    expect(await screen.findByTestId('PromoContainer')).toBeInTheDocument();
    expect(screen.getByText('Invalid!')).toBeInTheDocument();
  });

  it('renders warning when expired promo and viewType=any', async () => {
    mockLocation('?PROMOID=ST20RU');
    const promo = { ...basePromo, promoExpiredMessage: '<span>Expired!</span>' };

    render(<PromotionsNotification promotionBannerData={promo} viewType="any" page="SRP" />);

    expect(await screen.findByTestId('PromoContainer')).toBeInTheDocument();
    expect(screen.getByText('Expired!')).toBeInTheDocument();
  });

  it('renders both banner + warning when viewType=any and promo has expired', async () => {
    mockLocation('?PROMOID=ST20RU');
    const promo = {
      ...basePromo,
      promoExpiredMessage: '<span>Expired!</span>',
    };

    render(<PromotionsNotification promotionBannerData={promo} viewType="any" page="SRP" />);

    expect(await screen.findByTestId('PromoContainer')).toBeInTheDocument();
    expect(screen.getByText('Expired!')).toBeInTheDocument();
  });

  it('returns null if promo not enabled', () => {
    mockLocation('');
    const promo = { ...basePromo, showPromo: false, withinWindow: true };

    render(<PromotionsNotification promotionBannerData={promo} viewType="banner" />);

    expect(screen.queryByTestId('PromoContainer')).not.toBeInTheDocument();
  });

  it('returns null when promotionBannerData is undefined', () => {
    mockLocation('?PROMOID=ST20RU');
    render(<PromotionsNotification viewType="banner" />);
    expect(screen.queryByTestId('PromoContainer')).not.toBeInTheDocument();
  });

  it('does not render description when page is restricted (HDP)', async () => {
    mockLocation('?PROMOID=ST20RU');
    render(<PromotionsNotification promotionBannerData={basePromo} viewType="banner" page="HDP" />);
    const container = await screen.findByTestId('PromoContainer');
    expect(container).toBeInTheDocument();
    // description text should not appear because page is restricted
    expect(screen.queryByText(/Promo Subtitle/i)).not.toBeInTheDocument();
  });

  it('does not render banner when not within promo window', () => {
    mockLocation('?PROMOID=ST20RU');
    const promo = { ...basePromo, isWithinPromoWindow: false };
    render(<PromotionsNotification promotionBannerData={promo} viewType="banner" page="SRP" />);
    expect(screen.queryByTestId('PromoContainer')).not.toBeInTheDocument();
  });

  it('renders only warning when invalid message present and viewType="any"', async () => {
    mockLocation('?PROMOID=ST20RU');
    const promo = { ...basePromo, promoInvalidMessage: '<span>Invalid!</span>' };
    render(<PromotionsNotification promotionBannerData={promo} viewType="any" page="SRP" />);
    const container = await screen.findByTestId('PromoContainer');
    expect(container).toBeInTheDocument();
    // No banner should show because invalid message suppresses banner
    expect(screen.getByText('Invalid!')).toBeInTheDocument();
    expect(screen.queryByText(/Promo Title/i)).not.toBeInTheDocument();
  });

  it('renders banner when viewType="any" and no warning messages', async () => {
    mockLocation('?PROMOID=ST20RU');
    render(
      <PromotionsNotification
        promotionBannerData={basePromo}
        viewType="any"
        roomRates={roomRates as any}
        page="SRP"
      />
    );
    const container = await screen.findByTestId('PromoContainer');
    expect(container).toBeInTheDocument();
    expect(screen.getByText(/Promo Title/i)).toBeInTheDocument();
    expect(screen.getByText(/Promo Subtitle/i)).toBeInTheDocument();
  });

  it('renders info message when page is amend and showpromo', async () => {
    const promo = {
      ...basePromo,
      showPromo: true,
      isWithinPromoWindow: false,
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="stayDates"
      />
    );
    expect(await screen.findByTestId('stayDatesPromoBanner')).toBeInTheDocument();
  });
  it('Do not render stayDatesPromoBanner when elementName:stayDates, isWithinPromoWindow:true', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render stayDatesPromoBanner when elementName:stayDates, isWithinPromoWindow:true, no page', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page=""
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render stayDatesPromoBanner when no promotionCode', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page=""
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render stayDatesPromoBanner when page:amend no promotionCode', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render roomsAndGuestsPromoBanner when elementName:roomsAndGuests, isWithinPromoWindow:true', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('roomsAndGuestsPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render roomsAndGuestsPromoBanner when elementName:roomsAndGuests, isWithinPromoWindow:true no page', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page=""
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('roomsAndGuestsPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render roomsAndGuestsPromoBanner when no promotionCode', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page=""
        elementName="stayDates"
      />
    );
    expect(screen.queryByTestId('roomsAndGuestsPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render roomsAndGuestsPromoBanner when no promotionCode', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page=""
        elementName="roomsAndGuests"
      />
    );
    expect(screen.queryByTestId('roomsAndGuestsPromoBanner')).not.toBeInTheDocument();
  });

  it('Do not render roomsAndGuestsPromoBanner when page:amend no promotionCode', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="roomsAndGuests"
      />
    );
    expect(screen.queryByTestId('roomsAndGuestsPromoBanner')).not.toBeInTheDocument();
  });

  it('renders rooms and guest info message when page is amend and showpromo', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoBookingInfo: {
        ratePlanCode: 'FLXDIS10',
        promotionCode: 'FX10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="roomsAndGuests"
      />
    );
    expect(await screen.findByTestId('roomsAndGuestsPromoBanner')).toBeInTheDocument();
  });

  it('Should not render info message when page empty and no showpromo', async () => {
    const promo = {
      ...basePromo,
      showPromo: false,
    };
    render(<PromotionsNotification promotionBannerData={promo} viewType="info" page="" />);
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('Should not render info message when page empty and showpromo', async () => {
    const promo = {
      ...basePromo,
      showPromo: true,
    };
    render(<PromotionsNotification promotionBannerData={promo} viewType="info" page="" />);
    expect(screen.queryByTestId('stayDatesPromoBanner')).not.toBeInTheDocument();
  });

  it('renders stayDatesPromoBanner when showPromo and amendMessage is present', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: false,
      promoAmendMessage: 'Your booking includes a promotion. Cancel this booking and rebook.',
      promoBookingInfo: {
        ratePlanCode: 'STDDIS10',
        promotionCode: 'ST10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="stayDates"
      />
    );
    expect(await screen.getByTestId('stayDatesPromoBanner')).toBeInTheDocument();
    expect(
      await screen.getByText('Your booking includes a promotion. Cancel this booking and rebook.')
    ).toBeInTheDocument();
    expect(await screen.getByTestId('stayDatesPromoBanner')).toBeInTheDocument();
  });

  it('renders roomsAndGuests notification when showPromo and amendMessage is present', async () => {
    const promo = {
      ...basePromo,
      isWithinPromoWindow: true,
      promoAmendMessage: 'Your booking includes a promotion.Cancel this booking and rebook.',
      promoBookingInfo: {
        ratePlanCode: 'FLXDIS10',
        promotionCode: 'FX10R',
      },
    };
    render(
      <PromotionsNotification
        promotionBannerData={promo}
        viewType="info"
        page="amend"
        elementName="roomsAndGuests"
      />
    );
    expect(await screen.getByTestId('roomsAndGuestsPromoBanner')).toBeInTheDocument();
    expect(
      await screen.getByText('Your booking includes a promotion.Cancel this booking and rebook.')
    ).toBeInTheDocument();
    expect(await screen.getByTestId('roomsAndGuestsPromoBanner')).toBeInTheDocument();
  });

  it('does render banner when roomRates is empty', async () => {
    mockLocation('?PROMOID=ST20RU');
    render(
      <PromotionsNotification
        promotionBannerData={basePromo}
        viewType="any"
        roomRates={[] as any}
        page="HDP"
      />
    );
    expect(await screen.findByTestId('PromoContainer')).toBeInTheDocument();
  });
});
