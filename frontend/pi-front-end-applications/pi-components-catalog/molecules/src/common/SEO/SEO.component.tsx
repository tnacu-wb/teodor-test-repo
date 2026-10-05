import type {
  FAQItem,
  SEOInformation,
  SEOHrefLangs,
  CountryCode,
  SEOBreadcrumbItem,
  Breadcrumb,
  Reviews,
  TripAdvisorReviews,
  HotelInformationOptional,
} from '@whitbread-eos/api';
import { HIAEMroomTypesInfo, HIAvailabilityRates } from '@whitbread-eos/api';
import { FT_PI_HREFLANG_ATTRIBUTE, FS_ENABLE_SEO_BREADCRUMB_PI } from '@whitbread-eos/api';
import {
  formatAssetsUrl,
  removeHtmlTags,
  useFeatureToggle,
  useFeatureSwitch,
  useCustomLocale,
  containsPlaceLD,
  getPriceRange,
} from '@whitbread-eos/utils';
import Head from 'next/head';
import { useRouter } from 'next/router';
import { useState, useEffect, JSX } from 'react';

interface Props {
  data: SEOInformation;
  isLoading: boolean;
  isError: boolean;
  displayMeta?: boolean;
  breadcrumbs?: Breadcrumb[];
  tripAdvisorReviews?: TripAdvisorReviews;
  hotel?: HotelInformationOptional;
  hotelsList?: HotelInformationOptional[];
  showBreadcrumbs?: boolean;
  noIndexNoFollow?: boolean;
  destinationCoordinates?: { latitude?: number | null; longitude?: number | null };
  countryCodeISO?: string;
  hotelAvailability?: HIAvailabilityRates;
  roomTypeInformation?: HIAEMroomTypesInfo;
}

export function faqFormatter(faqItems: FAQItem[]) {
  return faqItems
    .map((faqItem: FAQItem) =>
      JSON.stringify({
        '@type': 'Question',
        name: faqItem?.question ?? '',
        acceptedAnswer: {
          '@type': 'Answer',
          text: removeHtmlTags(faqItem?.answer ?? '')
            .replace(/(\r\n|\n|\r)/gm, ' ')
            .trim(),
        },
      })
    )
    ?.join(',');
}

export default function SEO({
  data,
  isLoading,
  isError,
  displayMeta,
  breadcrumbs,
  hotelsList,
  showBreadcrumbs = false,
  hotel,
  noIndexNoFollow = false,
  destinationCoordinates,
  countryCodeISO,
  hotelAvailability,
  roomTypeInformation,
}: Readonly<Props>) {
  const { [FT_PI_HREFLANG_ATTRIBUTE]: isHreflangAttributeEnabled } = useFeatureToggle();
  const { country, language } = useCustomLocale();

  // use helm (server side) feature switch to ensure toggle ready before page load
  const isSEOBreadcrumbEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_SEO_BREADCRUMB_PI,
    fallbackValue: false,
  });
  const isWindowDefined = typeof window !== 'undefined';
  const [origin, setOrigin] = useState('');

  useEffect(() => {
    setOrigin(window?.location?.origin);
  }, [isWindowDefined]);

  const router = useRouter();

  const {
    pageTitle,
    pageDescription,
    cardImageUrl,
    faviconUrl,
    icons,
    msIcons,
    faq,
    hreflangs,
    geoJsonLd,
  } = data;

  if (isLoading || isError) {
    return null;
  }

  let breadcrumbsList: SEOBreadcrumbItem[] =
    breadcrumbs?.map((breadcrumbItem: Breadcrumb, index: number) => ({
      ...breadcrumbItem,
      position: index + 1,
    })) ?? [];

  const hasBreadcrumbs = breadcrumbsList && breadcrumbsList?.length > 0;
  // remove last breadcrumb link - hotel name (which is not a link)
  if (hasBreadcrumbs && !breadcrumbsList[breadcrumbsList.length - 1]?.link) {
    breadcrumbsList = breadcrumbsList.slice(0, -1);
  }

  return (
    <Head>
      {/* General Metadata */}
      <title>{pageTitle}</title>
      <meta name="viewport" content="width=device-width, initial-scale=1" />
      {displayMeta && (
        <>
          <meta name="description" content={pageDescription} />
          {noIndexNoFollow ? (
            <meta name="robots" content="noindex,nofollow" />
          ) : (
            <meta name="robots" content="index,follow" />
          )}
          {/* Twitter Cards */}
          <meta property="twitter:card" content="summary" />
          <meta property="twitter:url" content={getCurrentPageURL()} />
          <meta property="twitter:title" content={pageTitle} />
          <meta property="twitter:description" content={pageDescription} />
          <meta property="twitter:image" content={cardImageUrl} />
          <meta property="twitter:site" content="Premier Inn" />
          <meta property="twitter:creator" />

          {/* Facebook OpenGraph */}
          <meta property="og:type" content="website" />
          <meta property="og:url" content={getCurrentPageURL()} />
          <meta property="og:title" content={pageTitle} />
          <meta property="og:description" content={pageDescription} />
          <meta property="og:image" content={cardImageUrl} />
          <meta property="og:site_name" content="Premier Inn" />
          <meta property="fb:admins" />
          <meta name="msapplication-TileColor" content="#552462" />
          {renderItems(msIcons, (icon: { name: string; content: string }) => (
            <meta key={icon.name} name={icon.name} content={icon.content} />
          ))}
          {isHreflangAttributeEnabled &&
            renderItems(hreflangs, ({ hreflang, href }: SEOHrefLangs) => (
              <link key={hreflang} rel="alternate" hrefLang={hreflang} href={href} />
            ))}
          {renderItems(icons, (icon: { rel: string; sizes: string; href: string }) => (
            <link
              key={icon.href}
              rel={icon.rel}
              sizes={icon.sizes}
              href={formatAssetsUrl(icon.href)}
            />
          ))}
        </>
      )}
      <link rel="icon" type="image/x-icon" href={formatAssetsUrl(faviconUrl)} />
      <link rel="canonical" href={getCanonicalURL(router.locale as CountryCode, hreflangs)} />
      {renderGraph(hotelAvailability, roomTypeInformation)}
    </Head>
  );

  function renderGraph(
    dataHotelAvailabilityPI?: HIAvailabilityRates,
    dataRoomTypeInformation?: HIAEMroomTypesInfo
  ) {
    // ── DLP: full structured schema with WebPage, City, ItemList, FAQPage, BreadcrumbList ──
    if (showBreadcrumbs) {
      const dlpGraph: object[] = [];

      const pageUrl = getCanonicalURL(router.locale as CountryCode, hreflangs) ?? '';
      const baseUrl = origin || (pageUrl ? new URL(pageUrl).origin : '');

      // Destination name = last breadcrumb item (current page)
      const destinationName = breadcrumbs?.[breadcrumbs.length - 1]?.title ?? '';
      const hasDlpCoordinates =
        destinationCoordinates?.latitude != null && destinationCoordinates?.longitude != null;

      // 1. WebPage — ties all entities together via @id references
      const mainEntityRefs: object[] = [];
      if (hotelsList && hotelsList.length > 0) {
        mainEntityRefs.push({ '@id': `${pageUrl}/#itemlist` });
      }
      if (faq?.faqItems?.length > 0) {
        mainEntityRefs.push({ '@id': `${pageUrl}/#faq` });
      }
      dlpGraph.push({
        '@type': 'WebPage',
        '@id': `${pageUrl}/#webpage`,
        url: pageUrl,
        name: pageTitle,
        ...(hasBreadcrumbs && { breadcrumb: { '@id': `${pageUrl}/#breadcrumb` } }),
        ...(mainEntityRefs.length > 0 && { mainEntity: mainEntityRefs }),
        ...(hasDlpCoordinates && { about: { '@id': `${pageUrl}/#place` } }),
        inLanguage: `${language}-${country.toUpperCase()}`,
      });

      // 2. City — destination GEO as a named entity
      if (hasDlpCoordinates) {
        dlpGraph.push({
          '@type': 'City',
          '@id': `${pageUrl}/#place`,
          name: destinationName,
          geo: {
            '@type': 'GeoCoordinates',
            latitude: destinationCoordinates?.latitude,
            longitude: destinationCoordinates?.longitude,
          },
          address: {
            '@type': 'PostalAddress',
            addressCountry: countryCodeISO,
          },
        });
      }

      // 3. ItemList — simplified hotel cards (no review / aggregateRating / geo per hotel)
      if (hotelsList && hotelsList.length > 0) {
        dlpGraph.push({
          '@type': 'ItemList',
          '@id': `${pageUrl}/#itemlist`,
          name: pageTitle,
          numberOfItems: hotelsList.length,
          itemListElement: hotelsList.map((h, i) => ({
            '@type': 'ListItem',
            position: i + 1,
            item: formatHotelForList(h, baseUrl),
          })),
        });
      }

      // 4. FAQPage
      if (faq?.faqItems?.length > 0) {
        dlpGraph.push({
          '@type': 'FAQPage',
          '@id': `${pageUrl}/#faq`,
          mainEntity: faq.faqItems.map((item) => ({
            '@type': 'Question',
            name: item.question ?? '',
            acceptedAnswer: {
              '@type': 'Answer',
              text: removeHtmlTags(item.answer ?? '')
                .replace(/(\r\n|\n|\r)/gm, ' ')
                .trim(),
            },
          })),
        });
      }

      // 5. BreadcrumbList
      if (hasBreadcrumbs) {
        dlpGraph.push({
          '@type': 'BreadcrumbList',
          '@id': `${pageUrl}/#breadcrumb`,
          itemListElement: breadcrumbsList.map((b) => ({
            '@type': 'ListItem',
            position: b.position,
            name: b.title,
            item: formatAssetsUrl(b.link),
          })),
        });
      }

      // 6. geoJsonLd

      if (geoJsonLd) {
        try {
          const jsonLdFragment = `{${geoJsonLd.trim()}}`;
          dlpGraph.push(JSON.parse(jsonLdFragment));
        } catch (error) {
          console.warn('Invalid geoJsonLd fragment', geoJsonLd.toString(), error);
        }
      }

      if (dlpGraph.length === 0) return null;

      return (
        <script
          data-testid="seoGraph"
          type="application/ld+json"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify({ '@context': 'https://schema.org', '@graph': dlpGraph }),
          }}
        />
      );
    }

    // ── HDP: BreadcrumbList + Hotel (full schema) + FAQPage ──
    const hdpGraph: object[] = [];

    if (hasBreadcrumbs && isSEOBreadcrumbEnabled) {
      hdpGraph.push({
        '@type': 'BreadcrumbList',
        itemListElement: breadcrumbsList.map((b) => ({
          '@type': 'ListItem',
          position: b.position,
          name: b.title,
          item: formatAssetsUrl(b.link),
        })),
      });
    }

    if (hotel) {
      // geoJsonLd
      if (hotel.seo?.geoJsonLd) {
        try {
          const jsonLdFragment = `{${hotel.seo.geoJsonLd.trim()}}`;
          hdpGraph.push(JSON.parse(jsonLdFragment));
        } catch (error) {
          console.warn('Invalid geoJsonLd fragment', hotel.seo.geoJsonLd.toString(), error);
        }
      }

      const amenityFeature = hotel.hotelFacilities?.map((facility) => ({
        '@type': 'LocationFeatureSpecification',
        name: facility.name ?? '',
        value: true,
      }));

      const formattedHotel = formatHotel(hotel);
      (formattedHotel as any).identifier = hotel.hotelId ?? '';
      (formattedHotel as any).amenityFeature = amenityFeature;
      (formattedHotel as any).checkinTime =
        hotel.brand === 'PID'
          ? `${hotel.checkInTime ?? '15:00:00'}`
          : `${hotel.checkInTime ?? '14:00:00'}`;
      (formattedHotel as any).checkoutTime = '12:00:00';
      (formattedHotel as any).priceRange = getPriceRange(
        dataHotelAvailabilityPI as HIAvailabilityRates
      );
      if (dataHotelAvailabilityPI && dataRoomTypeInformation) {
        (formattedHotel as any).containsPlace = containsPlaceLD(
          dataHotelAvailabilityPI,
          dataRoomTypeInformation,
          hotel.brand as string
        );
      }
      (formattedHotel as any)['@id'] =
        `${origin}/${country}/${language}/hotels${hotel.links?.detailsPage}.html#hotel`;

      hdpGraph.push(formattedHotel);
    }

    if (faq?.faqItems?.length > 0) {
      hdpGraph.push({
        '@type': 'FAQPage',
        mainEntity: faq.faqItems.map((item) => ({
          '@type': 'Question',
          name: item.question ?? '',
          acceptedAnswer: {
            '@type': 'Answer',
            text: removeHtmlTags(item.answer ?? '')
              .replace(/(\r\n|\n|\r)/gm, ' ')
              .trim(),
          },
        })),
      });
    }

    if (hdpGraph.length === 0) return null;

    return (
      <script
        data-testid="seoGraph"
        type="application/ld+json"
        dangerouslySetInnerHTML={{
          __html: JSON.stringify({ '@context': 'https://schema.org', '@graph': hdpGraph }),
        }}
      />
    );
  }

  // DLP list: lean hotel card — name, url, telephone, hasMap, image, description, address only
  function formatHotelForList(hotel: HotelInformationOptional, baseUrl: string) {
    const hotelUrl = hotel.links?.detailsPage
      ? `${baseUrl}/${country}/${language}/hotels${hotel.links.detailsPage}.html`
      : undefined;
    const hasCoordinates =
      hotel.coordinates?.latitude != null && hotel.coordinates?.longitude != null;
    const hasMap = hasCoordinates
      ? `https://maps.googleapis.com/maps/api/staticmap?sensor=false&zoom=15&size=1600x1200&center=${hotel.coordinates?.latitude},${hotel.coordinates?.longitude}&markers=color:0x511E62%7c${hotel.coordinates?.latitude},${hotel.coordinates?.longitude}&key=${process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}`
      : undefined;
    return {
      '@type': 'Hotel',
      name: hotel.name,
      ...(hotelUrl && { url: hotelUrl }),
      telephone: hotel.contactDetails?.hotelNationalPhone ?? hotel.contactDetails?.phone ?? '',
      ...(hasMap && { hasMap }),
      image: (hotel.galleryImages ?? []).map((image) =>
        formatAssetsUrl(image.imageSrc ?? '').replace('///', '//')
      ),
      description: hotel.hotelDescription
        ? removeHtmlTags(hotel.hotelDescription)
            .replace(/(\r\n|\n|\r)/gm, ' ')
            .trim()
        : undefined,
      address: {
        '@type': 'PostalAddress',
        addressLocality: hotel.address?.cityName ?? hotel.address?.addressLine2,
        postalCode: hotel.address?.postalCode,
        streetAddress: [
          hotel.address?.addressLine1,
          hotel.address?.addressLine3,
          hotel.address?.addressLine2,
        ]
          .filter(Boolean)
          .join(', '),
        ...(hotel.address?.addressLine3 && { addressRegion: hotel.address.addressLine3 }),
        addressCountry: hotel?.countryCodeISO,
      },
    };
  }

  // HDP: full hotel schema — includes review, aggregateRating, geo
  function formatHotel(hotel: HotelInformationOptional) {
    const reviewsRating = hotel?.tripAdvisorReviews?.reviews?.map((review) => review.rating);

    const aggregateRating = hotel?.tripAdvisorReviews
      ? {
          '@type': 'AggregateRating',
          ratingValue: String(hotel.tripAdvisorReviews.rating),
          bestRating:
            reviewsRating &&
            String(
              Math.max(hotel.tripAdvisorReviews.rating as number, ...(reviewsRating as number[]))
            ),
          ratingCount: String(hotel.tripAdvisorReviews.numberOfReviews),
        }
      : undefined;

    return {
      '@type': 'Hotel',
      hasMap: `https://maps.googleapis.com/maps/api/staticmap?sensor=false&zoom=15&size=1600x1200&center=${hotel.coordinates?.latitude},${hotel.coordinates?.longitude}&markers=color:0x511E62%7c${hotel.coordinates?.latitude},${hotel.coordinates?.longitude}&key=${process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY}`,
      name: hotel.name,
      telephone: hotel.contactDetails?.hotelNationalPhone ?? hotel.contactDetails?.phone ?? '',
      image: (hotel.galleryImages ?? []).map((image) =>
        formatAssetsUrl(image.imageSrc ?? '').replace('///', '//')
      ),
      description: hotel.hotelDescription
        ? removeHtmlTags(hotel.hotelDescription)
            .replace(/(\r\n|\n|\r)/gm, ' ')
            .trim()
        : undefined,
      url: `${origin}/${country}/${language}/hotels${hotel.links?.detailsPage}.html`,
      address: {
        '@type': 'PostalAddress',
        addressLocality: hotel.address?.cityName ?? hotel.address?.addressLine2,
        postalCode: hotel.address?.postalCode,
        streetAddress: [
          hotel.address?.addressLine1,
          hotel.address?.addressLine3,
          hotel.address?.addressLine2,
        ]
          .filter(Boolean)
          .join(', '),
        ...(hotel.address?.addressLine3 && { addressRegion: hotel.address.addressLine3 }),
        addressCountry: hotel?.countryCodeISO,
      },
      review: ratingFormatter(hotel.tripAdvisorReviews?.reviews ?? []),
      ...(aggregateRating && { aggregateRating }),
      geo: {
        '@type': 'GeoCoordinates',
        latitude: hotel.coordinates?.latitude,
        longitude: hotel.coordinates?.longitude,
      },
    };
  }

  function getCurrentPageURL() {
    return typeof window !== 'undefined' ? window.location?.href : '';
  }
}

function renderItems<T>(items: T[], callbackFn: (item: T) => JSX.Element) {
  return items && items?.length > 0 && items.map(callbackFn);
}

function getCanonicalURL(country: CountryCode, hreflangs: SEOHrefLangs[]) {
  const language = country === 'gb' ? 'en' : 'de';

  return hreflangs?.find((hreflang) => hreflang.hreflang.includes(`${language}-${country}`))?.href;
}

function ratingFormatter(ratings: Reviews[]) {
  return ratings.map((ratingItem: Reviews) => {
    return {
      '@type': 'Review',
      reviewRating: {
        '@type': 'Rating',
        ratingValue: ratingItem.rating,
      },
      reviewBody: (ratingItem.text ?? '').replaceAll('"', ''),
      datePublished: ratingItem.publishedDate,
      author: {
        '@type': 'Person',
        name: ratingItem?.user?.username ?? '',
      },
    };
  });
}
