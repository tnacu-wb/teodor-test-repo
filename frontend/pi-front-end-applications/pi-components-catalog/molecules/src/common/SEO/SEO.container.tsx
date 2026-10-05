import {
  Breadcrumb,
  GET_SEO_INFORMATION,
  PageName,
  TripAdvisorReviews,
  HotelInformationOptional,
  HIAEMroomTypesInfo,
  HIAvailabilityRates,
} from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';

import Seo from './SEO.component';

interface Props {
  page: PageName;
  hotelId?: string;
  bookingFlowId?: string;
  displayMeta?: boolean;
  dlpData?: any;
  breadcrumbs?: Breadcrumb[];
  tripAdvisorReviews?: TripAdvisorReviews;
  hotel?: HotelInformationOptional;
  hotelsList?: HotelInformationOptional[];
  noIndexNoFollow?: boolean;
  destinationCoordinates?: { latitude?: number | null; longitude?: number | null };
  countryCodeISO?: string;
  hotelAvailability?: HIAvailabilityRates;
  roomTypeInformation?: HIAEMroomTypesInfo;
}

export default function SEOContainer({
  page,
  hotelId,
  bookingFlowId,
  displayMeta,
  dlpData,
  breadcrumbs,
  tripAdvisorReviews,
  hotelsList,
  hotel,
  noIndexNoFollow = false,
  destinationCoordinates,
  countryCodeISO,
  hotelAvailability,
  roomTypeInformation,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const queryKey = ['seoInformation', language, country, page];
  const queryParams: any = {
    language,
    country,
    page,
  };

  if (hotelId) {
    queryKey.push(hotelId);
    queryParams['hotelId'] = hotelId;
  }

  if (bookingFlowId) {
    queryKey.push(bookingFlowId);
    queryParams['bookingFlowId'] = bookingFlowId;
  }

  const { data, isLoading, isError, error } = useQueryRequest(
    queryKey,
    GET_SEO_INFORMATION,
    queryParams,
    { enabled: page !== PageName.DLP }
  );

  const { seoInformation = {} } = data || {};
  if (page === PageName.DLP && dlpData && breadcrumbs) {
    return (
      <Seo
        data={dlpData}
        breadcrumbs={breadcrumbs}
        hotelsList={hotelsList}
        showBreadcrumbs={true}
        isLoading={false}
        isError={false}
        displayMeta
        noIndexNoFollow={noIndexNoFollow}
        destinationCoordinates={destinationCoordinates}
        countryCodeISO={countryCodeISO}
      />
    );
  }
  if (page === PageName.HDP && breadcrumbs?.length) {
    return (
      <Seo
        {...{
          data: seoInformation,
          isLoading,
          isError,
          error,
          displayMeta,
          breadcrumbs,
          tripAdvisorReviews,
          hotel,
          hotelAvailability,
          roomTypeInformation,
        }}
      />
    );
  }
  if (page === PageName.SRP) {
    return (
      <Seo
        {...{
          data: seoInformation,
          isLoading,
          isError,
          error,
          displayMeta,
          noIndexNoFollow,
        }}
      />
    );
  }
  return <Seo {...{ data: seoInformation, isLoading, isError, error, displayMeta }} />;
}
