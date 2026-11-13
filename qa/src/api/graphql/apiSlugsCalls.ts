import { ApiCalls } from './apiCalls';
import { HotelTitle, HotelHeadline, HotelFacility, HotelAddressBySlug, HotelTransportInformation, HotelGalleryImage, HotelRoomConfiguration, HotelRestaurantConfiguration, HotelTripAdvisorReviews } from '../response';
import { withLocaleDefaults } from '../../test-data/locales';
import { Hotels } from '../../test-data/hotels';

interface SlugQueryParams {
  slug?: string;
  language?: string;
  country?: string;
}

interface HotelInformationBySlug {
  title?: string;
  brand?: string;
  headline?: string;
  address?: Record<string, unknown>;
  satNavDirections?: string;
  hotelFacilities?: Array<Record<string, unknown>>;
  directions?: string;
  transportInformation?: string[][];
  galleryImages?: Array<Record<string, unknown>>;
  roomConfiguration?: {
    tabGroups?: Array<{ groupId?: string; groupName?: string }>;
    tabItems?: Array<Record<string, unknown>>;
  };
  restaurant?: Record<string, unknown>;
  parkingDescription?: string;
  hotelDescription?: string;
  contactDetails?: { phone?: string };
  tripAdvisorReviews?: { rating?: number; numberOfReviews?: number };
  links?: { detailsPage?: string };
}

/**
 * Methods for accessing Premier Inn API backend resources.
 * Experience layer - Slugs Query and Mutations
 */
export class ApiSlugsCalls {
  /**
   * GraphQL for GetHotelInformationBySlug
  * @param {SlugQueryParams} inputData variables for the hotelInformationBySlug query
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {HotelInformationBySlug} hotel information by slug response
   */
  static async graphqlGetHotelInformationBySlug({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: SlugQueryParams = {}): Promise<HotelInformationBySlug> {
    const variables = { slug, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const response = await ApiCalls.makeGraphqlCall<{ hotelInformationBySlug?: HotelInformationBySlug }>('hotelInformationBySlug.graphql', variables);
    return response.body.data.hotelInformationBySlug ?? {};
  }

  /**
   * GraphQL for GetHotelTitle
  * @param {SlugQueryParams} inputData variables for the hotel title query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {HotelTitle} hotelInformation response from get hotel title Experience API call response call.
   * @returns {String} hotelInformation.name is the hotel name.
   * @returns {String} hotelInformation.brand is the hotel type.
   */
  static async graphqlGetHotelTitle({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelTitle> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return new HotelTitle({ title: response.title, brand: response.brand });
  }

  /**
   * GraphQL query for GetHotelStrapline
  * @param {SlugQueryParams} inputData variables for the hotel strapline query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns hotelInformation.headline extracted from get hotel strapline Experience API call response call.
   */
  static async graphqlGetHotelStrapline({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelHeadline> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return new HotelHeadline({ headline: response.headline });
  }

  /**
   * GraphQL call for GetHotelFacilities
  * @param {SlugQueryParams} inputData variables for the hotel facilities query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {Array.< HotelFacility >} list of hotel facilities extracted from get hotel facilities Experience API call response
   */
  static async graphqlGetHotelFacilities({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelFacility[]> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    const hotelFacilitiesFromResponse = response.hotelFacilities ?? [];
    const hotelFacilitiesList: HotelFacility[] = [];
    for (const hotelFacility of hotelFacilitiesFromResponse) {
      hotelFacilitiesList.push(new HotelFacility({ hotelFacility }));
    }
    return hotelFacilitiesList;
  }

  /**
   * GraphQL call for GetHotelLocationInformation
  * @param {SlugQueryParams} inputData variables for the hotel location query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {HotelAddressBySlug} HotelAddress object that keeps the data from the response
   */
  static async graphqlGetHotelLocationInformation({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelAddressBySlug> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return new HotelAddressBySlug({
      hotelAddressApiResponse: {
        address: response.address ?? {},
        satNavDirections: response.satNavDirections,
      },
    });
  }

  /**
   * GraphQL call for GetHotelDirections
  * @param {SlugQueryParams} inputData variables for the hotel directions query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {String} hotelInformation.directions are the directions to the hotel
   */
  static async graphqlGetHotelDirections({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<string> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return response.directions ?? '';
  }

  /**
   * GraphQL call for GetHotelTransportInformation
  * @param {SlugQueryParams} inputData variables for the hotel transport-information query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {Array.<HotelTransportInformation>} hotelInformation.transportInformation is the transport to the hotel information 
   */
  static async graphqlGetHotelTransportInformation({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelTransportInformation[]> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    const transportInformationFromResponse = response.transportInformation ?? [];
    const transportInformation: HotelTransportInformation[] = [];
    for (const transportInformationItem of transportInformationFromResponse) {
      transportInformation.push(new HotelTransportInformation({ hotelTransportInfoApiResponse: transportInformationItem }));
    }
    return transportInformation;
  }

  /**
   * GraphQL query for GetHotelPhotoGallery
  * @param {SlugQueryParams} inputData variables for the hotel photo-gallery query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {Array.<HotelGalleryImage>} array with the photo gallery images
   */
  static async graphqlGetHotelPhotoGallery({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelGalleryImage[]> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    const galleryImages = response.galleryImages ?? [];
    const photoGallery: HotelGalleryImage[] = [];
    for (const galleryImageItem of galleryImages) {
      photoGallery.push(new HotelGalleryImage({ hotelPhotoGalleryImage: galleryImageItem }));
    }
    return photoGallery;
  }

  /**
   * GraphQL query for GetHotelRoomConfiguration
  * @param {SlugQueryParams} inputData variables for the hotel room-configuration query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {Array.<HotelRoomConfiguration>} array with the hotel room configuration
   */
  static async graphqlGetHotelRoomConfiguration({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelRoomConfiguration[]> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    const roomConfiguration = response.roomConfiguration ?? {};
    const roomConfigurationArray: HotelRoomConfiguration[] = [];
    for (const tabGroupItem of roomConfiguration.tabGroups ?? []) {
      const tabPanelItems: Array<Record<string, unknown>> = [];
      for (const tabItem of roomConfiguration.tabItems ?? []) {
        if (tabGroupItem.groupId === tabItem.roomType) {
          tabPanelItems.push(tabItem);
        }
      }
      roomConfigurationArray.push(new HotelRoomConfiguration({ tabGroup: tabGroupItem, tabPanelItems }));
    }
    return roomConfigurationArray;
  }

  /**
   * GraphQL query for GetHotelRestaurantConfiguration
  * @param {SlugQueryParams} inputData variables for the hotel restaurant-configuration query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {HotelRestaurantConfiguration} HotelRestaurantConfiguration object that keeps the data from the response
   */
  static async graphqlGetHotelRestaurantConfiguration({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<HotelRestaurantConfiguration> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    const restaurantConfiguration = response.restaurant ?? {};
    return new HotelRestaurantConfiguration({ hotelRestaurantConfiguration: restaurantConfiguration });
  }

  /**
   * GraphQL query for GetHotelParking
  * @param {SlugQueryParams} inputData variables for the hotel parking query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {String} hotelInformation.parkingDescription are the description of hotel parking
   */
  static async graphqlGetHotelParking({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<string> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return response.parkingDescription ?? '';
  }

  /**
   * GraphQL call for GetHotelDescription
  * @param {SlugQueryParams} inputData variables for the hotel description query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {String} hotelInformation.hotelDescription is the hotel description found on the bottom of the 'Hotel Details' page
   */
  static async graphqlGetHotelDescription({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<string> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return response.hotelDescription ?? '';
  }

  /**
   * GraphQL call for GetHotelContactInformationPhoneNumber
  * @param {SlugQueryParams} inputData variables for the hotel contact-information query.
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {String} hotelInformation.contactDetails.phone is the contact phone number found on the bottom of the 'Hotel Details' page
   */
  static async graphqlGetHotelContactInformationPhoneNumber({ slug = Hotels.IPSWICH_NORTH.slug, language, country }: { slug?: string; language?: string; country?: string } = {}): Promise<string> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return response.contactDetails?.phone ?? '';
  }

  /**
  * GraphQL call for GetHotelTripAdvisorReviews
  * @param {SlugQueryParams} inputData variables for the hotel trip advisor reviews query
   * @param {String} inputData.slug hotel slug
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @returns {HotelTripAdvisorReviews} TripAdvisor review details
   */
  static async graphqlGetHotelTripAdvisorReviews({ slug = Hotels.LONDON_GATWICK_AIRPORT_SOUTH.slug, language, country }: SlugQueryParams = {}): Promise<HotelTripAdvisorReviews> {
    const response = await this.graphqlGetHotelInformationBySlug({ slug, language, country });
    return new HotelTripAdvisorReviews({
      tripAdvisorReviews: response.tripAdvisorReviews,
      links: response.links,
    });
  }

}
