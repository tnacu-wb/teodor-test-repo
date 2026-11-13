/**
 * Collection of hotels used for testing.
 */
export interface HotelData {
  id: string;
  name: string;
  slug?: string;
  deSlug?: string;
  location?: string;
  type: string;
  countryCode: string;
  threeLetterId?: string;
  menusCount?: number;
  cityKey?: string;
  hotelName?: string;
  englishName?: string;
  germanName?: string;
  hasCityTax?: boolean;
}
export class Hotels {
  private constructor() {}

  private static get options(): Record<string, unknown> {
    return (global.browser?.options ?? {}) as Record<string, unknown>;
  }

  private static get environment(): string {
    return String(Hotels.options.environment ?? Hotels.options.env ?? '');
  }

  private static get app(): string {
    return String(Hotels.options.app ?? 'pi');
  }

  // For dit and uat environment.
  static readonly BANGOR_GWYNEDD_NORTH_WALES: HotelData = { id: "BANBRI", name: "Bangor (Gwynedd, North Wales)", slug: "/hotels/wales/gwynedd/bangor/bangor-gwynedd-north-wales.html", location: "53.2037345,-4.1783636", type: "PI", countryCode: "gb", threeLetterId: "AEC" };
  static readonly BERLIN_ALEXANDERPLATZ: HotelData = { id: "BERALX", name: "Berlin Alexanderplatz", slug: "/hotels/germany/berlin/berlin/berlin-alexanderplatz.html", deSlug: "/hotels/deutschland/berlin/berlin/berlin-alexanderplatz.html", location: "52.523933,13.417059", type: "PID", countryCode: "de", threeLetterId: "GBM", menusCount: 2 };
  static readonly CARDIFF: HotelData = { id: "CARROA", name: "Cardiff", slug: "/hotels/wales/glamorgan/cardiff/zip-cardiff.html", location: "51.503001,-3.145601", type: "ZIP", countryCode: "gb", threeLetterId: "BAV" };
  static readonly CHRISTCHURCH_HIGHCLIFFE: HotelData = { id: "HIGHCL", name: "Christchurch / Highcliffe", slug: "/hotels/england/dorset/christchurch/christchurch-highcliffe.html", location: "50.74194,-1.70258", type: "PI", countryCode: "gb", threeLetterId: "AZI" };
  static readonly DONCASTER_CENTRAL_HIGH_FISHERGATE: HotelData = { id: "DONTAB", name: "Doncaster Central (High Fishergate)", slug: "/hotels/england/south-yorkshire/doncaster/doncaster-central-high-fishergate.html", location: "53.52561525922521,-1.1333942413330047", type: "PI", countryCode: "gb", threeLetterId: "AYU" };
  static readonly DOUGLAS_ISLE_OF_MAN: HotelData = { id: "IOFMAN", name: "Douglas (Isle of Man)", slug: "/hotels/england/isle-of-man/douglas/douglas-isle-of-man.html", location: "54.151365,-4.479975", type: "PI", countryCode: "iom", threeLetterId: "BIZ" };
  static readonly DRESDEN_CITY_CENTER: HotelData = { id: "DRECIT", name: "Dresden City Centre", slug: "/hotels/germany/saxony/dresden/dresden-city-centre.html", deSlug: "/hotels/deutschland/sachsen/dresden/dresden-city-zentrum.html", location: "51.047706,13.738102", type: "PID", countryCode: "de", threeLetterId: "GAG", menusCount: 2 };
  static readonly DUBLIN_CITY_CENTER_TEMPLE_BAR: HotelData = { id: "DUBSOU", name: "Dublin City Centre (Temple Bar)", slug: "/hotels/republic-of-ireland/dublin/dublin/dublin-city-centre-temple-bar.html", deSlug: "/hotels/republik-irland/dublin/dublin/dublin-city-centre-temple-bar.html", location: "53.34148,-6.2654", type: "PI", countryCode: "ie", threeLetterId: "BKQ" };
  static readonly DUNFERMLINE: HotelData = { id: "DUNCRO", name: "Dunfermline", slug: "/hotels/scotland/fife/dunfermline/dunfermline.html", location: "56.07776,-3.397825", type: "PI", countryCode: "gb", threeLetterId: "AUK" };
  // UK hotel used only for MLOS restriction testing.
  static readonly DURHAM_MILBURNGATE: HotelData = { id: "DURMIL", name: "Durham City Centre (Milburngate)", slug: "/hotels/england/county-durham/durham/durham-city-centre-milburngate.html", location: "54.778492,-1.578583", type: "PI", countryCode: "gb", threeLetterId: "BLE", cityKey: "DURHAM" };
  static readonly DUSSELDORF_CITY_CENTRE: HotelData = { id: "DUSOST", name: "Dusseldorf City Centre", slug: "/hotels/germany/north-rhine-westphalia/dusseldorf/dusseldorf-city-centre.html", deSlug: "/hotels/deutschland/nordrhein-westfalen/duesseldorf/duesseldorf-city-centre.html", location: "6.794978, 51.229549", type: "PID", countryCode: "de", threeLetterId: "GAL" };
  static readonly EDINBURGH_PARK_AIRPORT: HotelData = { id: "EDIPAR", name: "Edinburgh Park (Airport)", slug: "/hotels/scotland/lothian/edinburgh/edinburgh-park-airport.html", location: "55.928445,-3.307357", type: "PI", countryCode: "gb", threeLetterId: "BAV" };
  // For dit and uat environment.
  static readonly EDINBURGH_HAYMARKET: HotelData = { id: "EDIHAY", name: "Edinburgh Haymarket", slug: "/hotels/scotland/lothian/edinburgh/hub-edinburgh-haymarket.html", location: "55.946785,-3.2139897", type: "HUB", countryCode: "gb", threeLetterId: "MAJ", menusCount: 1, cityKey: "EDINBURGH" };
  static readonly FALKIRK_NORTH: HotelData = { id: "FALBOW", name: "Falkirk North", slug: "/hotels/scotland/central/falkirk/falkirk-north.html", location: "56.05341,-3.758166", type: "PI", countryCode: "gb" };
  static readonly FRANKFURT_CITY_CENTRE: HotelData = { id: "FRAHOF", name: "Frankfurt City Centre", slug: "/hotels/germany/hesse/frankfurt/frankfurt-city-centre.html", deSlug: "/hotels/deutschland/hessen/frankfurt/frankfurt-city-centre.html", location: "50.10702,8.669339", type: "PID", countryCode: "de" };
  // For dev and uat environment.
  static readonly FRANKFURT_MESSE: HotelData = { id: "FRAMTI", name: "Frankfurt Messe", slug: "/hotels/germany/hesse/frankfurt/frankfurt-messe.html", deSlug: "/hotels/deutschland/hessen/frankfurt/frankfurt-messe.html", location: "50.108715,8.647915", type: "PID", countryCode: "de", threeLetterId: "GAA", menusCount: 2, cityKey: "FRANKFURT" };
  // Bart DE hotel used to validate redirect to the Bart hotel details page.
  static readonly FRANKFURT_WESTEND: HotelData = { id: "FRAWES", name: "Frankfurt Westend", slug: "/hotels/germany/hesse/frankfurt/frankfurt-westend.html", deSlug: "/hotels/deutschland/hessen/frankfurt/frankfurt-westend.html", location: "50.116035,8.646051", type: "PID", countryCode: "de" };
  // For uat environment.
  static readonly FREIBURG_CITY_SUD: HotelData = { id: "FRESUD", name: "Freiburg City Sud", slug: "/hotels/germany/baden-wurttemberg/freiburg/freiburg-city-sud.html", deSlug: "/hotels/deutschland/baden-wuerttemberg/freiburg/freiburg-city-sued.html", location: "47.989964,7.836339", type: "PID", countryCode: "de", threeLetterId: "GAN", cityKey: "FREIBURG", hotelName: "Freiburg City Süd" };
  static readonly GLASGOW_CITY_CENTER_GEORGE_SQUARE: HotelData = { id: "GLACIT", name: "Glasgow City Centre (George Square)", slug: "/hotels/scotland/strathclyde/glasgow/glasgow-city-centre-george-square.html", deSlug: "/hotels/schottland/strathclyde/glasgow/glasgow-city-centre-george-square.html", location: "55.860845,-4.245307", type: "PI", countryCode: "gb", threeLetterId: "AEG" };
  static readonly GLASGOW_PACIFIC_QUAY_SECC: HotelData = { id: "GLAPAC", name: "Glasgow Pacific Quay (SECC)", slug: "/hotels/scotland/strathclyde/glasgow/glasgow-pacific-quay-secc.html", location: "55.857802,-4.289081", type: "PI", countryCode: "gb", threeLetterId: "BEE" };
  static readonly GREAT_YARMOUTH_SEAFRONT: HotelData = { id: "GRESOU", name: "Great Yarmouth (Seafront)", slug: "/hotels/england/norfolk/great-yarmouth/great-yarmouth-seafront.html", location: "52.590736,1.735884", type: "PI", countryCode: "gb", threeLetterId: "BIX" };
  // Only for preprod env.
  static readonly HALL_GREEN_BIRMINGHAM: HotelData = { id: "BIRTOB", name: "Birmingham South (Hall Green)", slug: "/hotels/england/west-midlands/birmingham/birmingham-south-hall-green.html", location: "52.479741,-1.897153", type: "PI", countryCode: "gb", threeLetterId: "AXR", menusCount: 1 };
  static readonly HAYDOCK_PARK_M6_J23: HotelData = { id: "HAYSTO", name: "Haydock Park/M6 J23", slug: "/hotels/england/greater-manchester/haydock/haydock-parkm6-j23.html", location: "53.470073,-2.590493", type: "PI", countryCode: "gb", threeLetterId: "APR" };
  // For uat environment.
  static readonly HEIBAH: HotelData = { id: "HEIBAH", name: "Heidelberg City Bahnstadt", slug: "/hotels/germany/baden-wurttemberg/heidelberg/heidelberg-city-bahnstadt.html", deSlug: "/hotels/deutschland/baden-wuerttemberg/heidelberg/heidelberg-city-bahnstadt.html", location: "49.3997259,8.6790123", type: "PID", countryCode: "de", threeLetterId: "GBC" };
  static readonly IPSWICH_NORTH: HotelData = { id: "IPSMTI", name: "Ipswich North", slug: "/hotels/england/suffolk/ipswich/ipswich-north.html", location: "52.101036,1.1071705", type: "PI", countryCode: "gb", threeLetterId: "AKC", cityKey: "IPSWICH_NORTH" };
  // For dit and uat environment.
  static readonly LONDON_COVENT_GARDEN: HotelData = { id: "LONSTM", name: "London Covent Garden", slug: "/hotels/england/greater-london/london/london-covent-garden.html", location: "51.5099988,-0.1272863", type: "HUB", countryCode: "gb" };
  // For uat environment.
  static readonly LONDON_GATWICK_AIRPORT_SOUTH: HotelData = { id: "GATGAT", name: "London Gatwick Airport South (London Road)", slug: "/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html", location: "51.139502,-0.183366", type: "PI", countryCode: "gb", threeLetterId: "AJK", cityKey: "WEST_SUSSEX", hasCityTax: true };
  static readonly LONDON_SOHO: HotelData = { id: "LONSOH", name: "London Soho", slug: "/hotels/england/greater-london/london/hub-london-soho.html", location: "51.51311652368785,-0.13464632313117444", type: "HUB", countryCode: "gb" };
  static readonly LONDON_WEST_BROMPTON: HotelData = { id: "LONWBR", name: "London West Brompton", slug: "/hotels/england/greater-london/london/hub-london-west-brompton.html", location: "51.486892,-0.196906", type: "HUB", countryCode: "gb" };
  // Bart UK hotel used to validate redirect to the Bart hotel details page.
  static readonly LEEDS_CITY_CENTRE_LEEDS_ARENA: HotelData = { id: "LEEHEP", name: "Leeds City Centre (Leeds Arena)", location: "53.804159,-1.543611", type: "PI", countryCode: "gb" };
  static readonly LONDON_FINSBURY: HotelData = { id: "LONFIN", name: "London Finsbury Park", slug: "/hotels/england/greater-london/london/london-finsbury-park.html", location: "51.563211,-0.107299", type: "PI", countryCode: "gb" };
  static readonly LONDON_HAMPSTEAD: HotelData = { id: "LONHMP", name: "London Hampstead", slug: "/hotels/england/greater-london/london/london-hampstead.html", location: "51.551536,-0.167209", type: "PI", countryCode: "gb" };
  // For uat environment.
  static readonly LONDON_HEATHROW_AIRPORT: HotelData = { id: "HEAPTI", name: "London Heathrow Airport (M4/J4)", slug: "/hotels/england/greater-london/london/london-heathrow-airport-m4j4.html", location: "51.496015,-0.447979", type: "PI", countryCode: "gb", threeLetterId: "AQN", cityKey: "LONDON" };
  // For dit and uat environment.
  static readonly LONDON_KING_CROSS: HotelData = { id: "LONKIN", name: "London Kings Cross", slug: "/hotels/england/greater-london/london/hub-london-kings-cross.html", location: "51.533674,-0.122153", type: "HUB", countryCode: "gb", threeLetterId: "MAH", menusCount: 2, cityKey: "LONDON" };
  static readonly LONDON_LEICESTER_SQUARE: HotelData = { id: "LONLEI", name: "London Leicester Square", slug: "/hotels/england/greater-london/london/london-leicester-square.html", location: "51.511143,-0.13035", type: "PI", countryCode: "gb" };
  // For dit and uat environment.
  static readonly LONEUS: HotelData = { id: "LONEUS", name: "London Euston", slug: "/hotels/england/greater-london/london/london-euston.html", location: "51.527736,-0.129068", type: "PI", countryCode: "gb", threeLetterId: "AKU", cityKey: "LONDON" };
  // For dit and uat environment.
  static readonly LUTON_TOWN_CENTRE: HotelData = { id: "LUTREG", name: "Luton Town Centre", slug: "/hotels/england/bedfordshire/luton/luton-town-centre.html", location: "52.101036,1.1071705", type: "PI", countryCode: "gb", threeLetterId: "BDT" };
  static readonly MAIDSTONE_A26_WATERINGBURY: HotelData = { id: "MAIWAT", name: "Maidstone (A26/Wateringbury)", slug: "/hotels/england/kent/maidstone/maidstone-a26wateringbury.html", location: "51.25503,0.4237912", type: "PI", countryCode: "gb", threeLetterId: "ATD" };
  static readonly MANCHESTER_OLD_TRAFFORD: HotelData = { id: "MANOLD", name: "Manchester Old Trafford", slug: "/hotels/england/greater-manchester/manchester/manchester-old-trafford.html", location: "53.46524,-2.28816", type: "PI", countryCode: "gb", threeLetterId: "AWM" };
  // For dit and uat environment.
  static readonly MANCHESTER_CITY_CENTRE: HotelData = { id: "MANOXF", name: "Manchester City Centre (Princess Street)", slug: "/hotels/england/greater-manchester/manchester/manchester-city-centre-princess-street.html", type: "PI", countryCode: "gb", threeLetterId: "BKK" };
  static readonly MANCHESTER_CITY_CENTRE_PORTLAND: HotelData = { id: "MANMTI", name: "Manchester City Centre (Portland Street)", slug: "/hotels/england/greater-manchester/manchester/manchester-city-centre-portland-street.html", location: "53.476515268025885,-2.2424340248107884", type: "PI", countryCode: "gb" };
  // DE hotel used only for MLOS restriction testing.
  static readonly MUNCHEN_CITY_ZENTRUM: HotelData = { id: "MUNCIT", name: "München City Zentrum", slug: "/hotels/germany/bavaria/munich/munich-city-centre.html", deSlug: "/hotels/deutschland/bayern/muenchen/muenchen-city-zentrum.html", location: "48.135388,11.565913", type: "PID", countryCode: "de", threeLetterId: "GAE", cityKey: "MUNICH", englishName: "Munich City Centre", germanName: "Muenchen City Zentrum" };
  static readonly NEWCASTLE_CITY_CENTER_THE_GATE: HotelData = { id: "NEWTHY", name: "Newcastle City Centre (The Gate)", slug: "/hotels/england/tyne-and-wear/newcastle/newcastle-city-centre-the-gate.html", location: "54.972925,-1.617819", type: "PI", countryCode: "gb", threeLetterId: "BFX" };
  static readonly NEWHAVEN: HotelData = { id: "NEWDRO", name: "Newhaven", slug: "/hotels/england/east-sussex/newhaven/newhaven.html", location: "50.79982677471851,0.06166335336628057", type: "PI", countryCode: "gb", threeLetterId: "ALS" };
  static readonly STUTTGART_AIRPORT_MESSE: HotelData = { id: "STUAIR", name: "Stuttgart Airport/Messe", slug: "/hotels/germany/baden-wurttemberg/stuttgart/stuttgart-airport-messe.html", deSlug: "/hotels/deutschland/baden-wuerttemberg/stuttgart/stuttgart-airport-messe.html", location: "48.69770199997964,9.166722834170669", type: "PID", countryCode: "de", threeLetterId: "GBH", cityKey: "STUTTGART" };
  static readonly PRESTON_EAST: HotelData = { id: "PRENOR", name: "Preston East", slug: "/hotels/england/lancashire/preston/preston-east.html", location: "53.789703,-2.658726", type: "PI", countryCode: "gb", threeLetterId: "AEW" };
  static readonly WIESBADEN_CITY_CENTRE: HotelData = { id: "WIECIT", name: "Wiesbaden City Centre", slug: "/hotels/germany/hesse/wiesbaden/wiesbaden-city-centre.html", deSlug: "/hotels/deutschland/hessen/wiesbaden/wiesbaden-city-centre.html", location: "50.072062,8.248433", type: "PID", countryCode: "de", threeLetterId: "GBQ" };
  static readonly WORKSOP: HotelData = { id: "WORHIG", name: "Worksop", slug: "/hotels/england/nottinghamshire/worksop/worksop.html", location: "53.309274,-1.145187", type: "PI", countryCode: "gb", threeLetterId: "BKS" };
  static readonly YORK_SOUTH_WEST: HotelData = { id: "YORMTI", name: "York South West", slug: "/hotels/england/north-yorkshire/york/york-south-west.html", location: "53.905607,-1.187166", type: "PI", countryCode: "gb", threeLetterId: "AOL" };
  static readonly MILTON_KEYNES_CENTRAL: HotelData = { id: "MILBAR", name: "Milton Keynes Central(Xscape)", slug: "/hotels/england/buckinghamshire/milton-keynes/milton-keynes-centralxscape", type: "PI", countryCode: "gb" };

  static readonly ALL_HOTELS: HotelData[] = [
    Hotels.BANGOR_GWYNEDD_NORTH_WALES,
    Hotels.BERLIN_ALEXANDERPLATZ,
    Hotels.CARDIFF,
    Hotels.CHRISTCHURCH_HIGHCLIFFE,
    Hotels.DONCASTER_CENTRAL_HIGH_FISHERGATE,
    Hotels.DOUGLAS_ISLE_OF_MAN,
    Hotels.DRESDEN_CITY_CENTER,
    Hotels.DUBLIN_CITY_CENTER_TEMPLE_BAR,
    Hotels.DUNFERMLINE,
    Hotels.DURHAM_MILBURNGATE,
    Hotels.DUSSELDORF_CITY_CENTRE,
    Hotels.EDINBURGH_PARK_AIRPORT,
    Hotels.EDINBURGH_HAYMARKET,
    Hotels.FALKIRK_NORTH,
    Hotels.FRANKFURT_CITY_CENTRE,
    Hotels.FRANKFURT_MESSE,
    Hotels.FRANKFURT_WESTEND,
    Hotels.FREIBURG_CITY_SUD,
    Hotels.GLASGOW_CITY_CENTER_GEORGE_SQUARE,
    Hotels.GLASGOW_PACIFIC_QUAY_SECC,
    Hotels.GREAT_YARMOUTH_SEAFRONT,
    Hotels.HALL_GREEN_BIRMINGHAM,
    Hotels.HAYDOCK_PARK_M6_J23,
    Hotels.HEIBAH,
    Hotels.IPSWICH_NORTH,
    Hotels.LONDON_COVENT_GARDEN,
    Hotels.LONDON_GATWICK_AIRPORT_SOUTH,
    Hotels.LONDON_SOHO,
    Hotels.LONDON_WEST_BROMPTON,
    Hotels.LEEDS_CITY_CENTRE_LEEDS_ARENA,
    Hotels.LONDON_FINSBURY,
    Hotels.LONDON_HAMPSTEAD,
    Hotels.LONDON_HEATHROW_AIRPORT,
    Hotels.LONDON_KING_CROSS,
    Hotels.LONDON_LEICESTER_SQUARE,
    Hotels.LONEUS,
    Hotels.LUTON_TOWN_CENTRE,
    Hotels.MAIDSTONE_A26_WATERINGBURY,
    Hotels.MANCHESTER_OLD_TRAFFORD,
    Hotels.MANCHESTER_CITY_CENTRE,
    Hotels.MANCHESTER_CITY_CENTRE_PORTLAND,
    Hotels.MUNCHEN_CITY_ZENTRUM,
    Hotels.NEWCASTLE_CITY_CENTER_THE_GATE,
    Hotels.NEWHAVEN,
    Hotels.STUTTGART_AIRPORT_MESSE,
    Hotels.PRESTON_EAST,
    Hotels.WIESBADEN_CITY_CENTRE,
    Hotels.WORKSOP,
    Hotels.YORK_SOUTH_WEST,
    Hotels.MILTON_KEYNES_CENTRAL,
  ];

  // Common aliases used by migrated tests.
  static readonly GATWICK_AIRPORT = Hotels.LONDON_GATWICK_AIRPORT_SOUTH;
  static readonly LONDON_COUNTY_HALL: HotelData = {
    id: 'LONWAT',
    name: 'London County Hall',
    slug: '/hotels/england/greater-london/london/london-county-hall.html',
    location: '51.503001,-0.119519',
    type: 'PI',
    countryCode: 'gb',
  };
  /**
   * Default hotel
   * @returns Hotel object
   */
  static get DEFAULT_HOTEL(): HotelData {
    const defaultHotel = Hotels.options.defaultHotel;
    if (defaultHotel) {
      const foundHotel = Hotels.ALL_HOTELS.find((hotel) => hotel.id === defaultHotel);
      if (!foundHotel) {
        throw new Error(`${defaultHotel} hotel id, sent as DEFAULT_HOTEL parameter, is not found in the hotels list!`);
      }
      return foundHotel;
    }

    const stableEnvironments = ['dit', 'uat', 'preprod', 'demo', 'prod'];
    if (Hotels.app === 'ccui') {
      return stableEnvironments.includes(Hotels.environment) ? Hotels.LONDON_GATWICK_AIRPORT_SOUTH : Hotels.environment === 'sit' ? Hotels.LONEUS : Hotels.IPSWICH_NORTH;
    }
    return stableEnvironments.includes(Hotels.environment) ? Hotels.LONDON_GATWICK_AIRPORT_SOUTH : Hotels.LONEUS;
  }

  /**
   * Default german hotel that is used in tests
   * @returns Hotel object
   */
  static get DEFAULT_GERMAN_HOTEL(): HotelData {
    return ['dit', 'uat', 'preprod', 'demo', 'prod'].includes(Hotels.environment) ? Hotels.FREIBURG_CITY_SUD : Hotels.FRANKFURT_MESSE;
  }

  /**
   * Paypal german hotel that is used in tests
   * @returns Hotel object
   */
  static get PAYPAL_GERMAN_HOTEL(): HotelData {
    return ['dit', 'uat', 'preprod', 'demo', 'prod'].includes(Hotels.environment) ? Hotels.FRANKFURT_MESSE : Hotels.STUTTGART_AIRPORT_MESSE;
  }

  static get CONFIRMATION_DEFAULT_GERMAN_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_GERMAN_HOTEL; }
  static get ANCILLARIES_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get AUTHENTICATION_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get CONFIRMATION_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.LONEUS; }
  static get FOOTER_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get GUEST_DETAILS_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get HOME_PAGE_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get HOTEL_DETAILS_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get ECI_LCO_UK_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.LONDON_HEATHROW_AIRPORT; }
  static get ECI_LCO_DE_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.STUTTGART_AIRPORT_MESSE; }
  static get ECI_LCO_DE_FLAT_CITYTAX_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.FRANKFURT_MESSE; }
  static get ECI_LCO_DE_NO_CITYTAX_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.HEIBAH; }
  static get ECI_LCO_IR_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DUBLIN_CITY_CENTER_TEMPLE_BAR; }
  static get MANAGE_BOOKING_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get NAVIGATION_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get PAYMENT_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get SEARCH_CONSOLE_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }
  static get SEARCH_RESULTS_DEFAULT_HOTEL(): HotelData { return Hotels.options.defaultHotel ? Hotels.DEFAULT_HOTEL : Hotels.DEFAULT_HOTEL; }

  /**
   * Get Hotel object based on 'id' property
   * @param hotelId the hotel id
   * @returns hotel
   */
  static getHotelById(hotelId: string): HotelData {
    const foundHotel = Hotels.ALL_HOTELS.find((hotel) => hotel.id === hotelId);
    if (!foundHotel) {
      throw new Error(`${hotelId} hotel is not found in the list!`);
    }
    return foundHotel;
  }

  static readonly DLP_PATHS = {
    BIRMINGHAM: 'hotels/england/west-midlands/birmingham.html',
    LUTON: 'hotels/england/bedfordshire/luton.html',
  } as const;

  static readonly RoomTypes = {
    DOUBLE: 'DOUBLE',
    FAMILY: 'FAMILY',
  } as const;

  static readonly UK_CURRENCY_CODE = 'GBP';
  static readonly POLICY_CODE_D1A = 'D1A';
}
