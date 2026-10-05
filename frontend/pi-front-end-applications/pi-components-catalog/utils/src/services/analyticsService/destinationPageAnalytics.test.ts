import analytics from './analytics';
import updateDestinationPageAnalytics from './destinationPageAnalytics';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

const mockDlps = [
  {
    picture:
      'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
    title: 'Hotels in London Wembley Stadium',
    link: '/hotels/gb/en/london/london-wembley-stadium',
    order: 1,
  },
];

const mockHotels = [
  {
    name: 'Newcastle City Centre (The Gate)',
    brand: 'PI',
    distanceFromReference: 128.29555929579615,
    hotelId: 'NWCTLE',
    topSectionImages: [
      {
        alt: '',
        caption: '',
        iconSrc: '',
        imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/N/NEWTHY/NEWTHY1-min.jpg',
        thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/N/NEWTHY/NEWTHY1-min.jpg',
      },
    ],
    hotelFacilities: [
      {
        code: 'PRR',
        description: '',
        icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/PRR.svg',
        isVisible: true,
        name: 'Premier Plus rooms',
        weight: 0,
      },
    ],
    links: {
      detailsPage: '/england/tyne-and-wear/newcastle/newcastle-city-centre-the-gate',
    },
    tripAdvisorReviews: {
      rating: 4.5,
      numberOfReviews: 100,
    },
    messagingFlag: {
      color: '',
      description: '',
      text: 'New Hotel',
    },
  },
];

describe('destinationPageAnalytics', () => {
  describe('updateDestinationPageAnalytics Method', () => {
    it('should call analytics update with provided data', () => {
      updateDestinationPageAnalytics({
        hotels: mockHotels,
        destinations: mockDlps,
        locationName: 'newcastle',
        hotelDisplayedCount: 12,
        filterType: 'recommended',
        thingsToDo: { items: [{ title: 'Things to do', order: 1 }] },
        promos: [{ title: 'Travel Guides', order: 1 }],
        mapReference: true,
        premierPlusLabel: 'Premier Plus',
      });
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        dlp: {
          hotels: [
            {
              hotelCode: 'NWCTLE',
              hotelName: 'Newcastle City Centre (The Gate)',
              hotelLabels: ['Premier Plus', 'New Hotel'],
              orderPosition: 1,
              tripAdvisorRating: 4.5,
              tripAdvisorReviewsCount: 100,
            },
          ],
          destinations: [
            {
              destinationLocationName: 'Hotels in London Wembley Stadium',
              destinationPosition: 1,
            },
          ],
          locationName: 'newcastle',
          hotelDisplayedCount: 12,
          filterType: 'recommended',
          thingsToDo: [{ tileName: 'Things to do', tilePosition: 1 }],
          travelGuides: [{ tileName: 'Travel Guides', tilePosition: 1 }],
          mapReference: true,
          filterSectionOpened: false,
          filtersCleared: false,
          searchFilter: '',
        },
        pageName: 'premier inn: seo: hotels in newcastle',
      });
    });
  });
});
