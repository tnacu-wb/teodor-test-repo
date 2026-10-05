import { config } from '@WB-playwright/config';
import { Locations, Constants } from '@WB-playwright/constants';

export const Hotels = {
  LONDON_KING_CROSS: {
    id: 'LONKIN',
    threeLetterId: 'MAH',
    name: 'London Kings Cross',
    slug: '/hotels/england/greater-london/london/hub-london-kings-cross.html',
    location: '51.533674,-0.122153',
    type: Constants.HUB.name,
    city: Locations.LONDON.name,
  },
  LONDON_EUSTON: {
    id: 'LONEUS',
    threeLetterId: 'AKU',
    name: 'London Euston',
    slug: '/hotels/england/greater-london/london/london-euston.html',
    location: '51.527736,-0.129068',
    type: Constants.PI.name,
    city: Locations.LONDON.name,
  },
  LONDON_GATWICK_AIRPORT: {
    id: 'GATGAT',
    threeLetterId: 'AJK',
    name: 'London Gatwick Airport South (London Road)',
    slug: '/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html',
    location: '51.139502,-0.183366',
    type: Constants.PI.name,
    city: Locations.LONDON.name,
  },

  DRESDEN_CITY_CENTER: {
    id: 'DRECIT',
    threeLetterId: 'GAG',
    name: 'Dresden City Centre',
    slug:
      config.LANGUAGE === 'en'
        ? '/hotels/germany/saxony/dresden/dresden-city-centre.html'
        : '/hotels/deutschland/sachsen/dresden/dresden-city-zentrum.html',
    location: '51.047706,13.738102',
    type: Constants.PID.name,
    countryCode: Locations.DRESDEN.countryCode,
    city: Locations.DRESDEN.name,
  },
  DUBLIN_CITY_CENTER: {
    id: 'DUBSOU',
    threeLetterId: 'BKQ',
    name: 'Dublin City Centre (Temple Bar)',
    slug:
      config.LANGUAGE === 'en'
        ? '/hotels/republic-of-ireland/dublin/dublin/dublin-city-centre-temple-bar.html'
        : '/hotels/republik-irland/dublin/dublin/dublin-city-centre-temple-bar.html',
    location: '53.34148,-6.2654',
    type: Constants.PI.name,
    countryCode: Locations.DUBLIN.countryCode,
    city: Locations.DUBLIN.name,
  },
  FREIBURG_CITY_SUD: {
    id: 'FRESUD',
    threeLetterId: 'GAN',
    name: 'Freiburg City Sud',
    hotelName: 'Freiburg City Sued',
    slug:
      config.LANGUAGE === 'en'
        ? '/hotels/germany/baden-wurttemberg/freiburg/freiburg-city-sud.html'
        : '/hotels/deutschland/baden-wuerttemberg/freiburg/freiburg-city-sued.html',
    location: '47.989964,7.836339',
    type: Constants.PID.name,
    countryCode: Locations.FREIBURG.countryCode,
    city: Locations.FREIBURG.name,
  },
  FRANKFURT_MESSE: {
    id: 'FRAMTI',
    threeLetterId: 'GAA',
    name: 'Frankfurt Messe',
    slug:
      config.LANGUAGE === 'en'
        ? '/hotels/germany/hesse/frankfurt/frankfurt-messe.html'
        : '/hotels/deutschland/hessen/frankfurt/frankfurt-messe.html',
    location: '50.108715,8.647915',
    type: Constants.PID.name,
    countryCode: Locations.FRANKFURT.countryCode,
    city: Locations.FRANKFURT,
  },
};
