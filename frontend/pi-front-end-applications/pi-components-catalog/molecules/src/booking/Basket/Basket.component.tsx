import { InfoOutlineIcon } from '@chakra-ui/icons';
import { Box, Divider, Flex, Link, Text } from '@chakra-ui/react';
import {
  type HIDailyPrice,
  type Channel,
  type ReservationRoomType,
  FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE,
  HotelBrand,
  FT_PI_ROOM_UPGRADE_POPUP,
  RoomUpgradeContent,
  FT_BB_ROOM_UPGRADE_POPUP,
  AnalyticsData,
  AnalyticsDataCartConfirmation,
  RoomClass,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  UserChoice,
} from '@whitbread-eos/api';
import { SoftBundle } from '@whitbread-eos/api/dist/types/graphql';
import {
  Button,
  Card,
  Error,
  ModalVariants,
  Notification,
  PromoTag,
  PencePrice,
} from '@whitbread-eos/atoms';
import {
  analytics,
  formatAssetsUrl,
  formatCurrency,
  formatDate,
  formatPrice,
  getCookie,
  BUNDLE_CHOICE,
  renderSanitizedHtml,
  useCustomLocale,
  useFeatureToggle,
  useSessionStorage,
  useSemanticTypography,
  type PromoActionsType,
  formatRatePrice,
  BUNDLE_CHOICE_OPTIONS,
  MAX_ROOMS_SEARCH_LIMIT,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import React, { useEffect, useState } from 'react';
import ReactDOM from 'react-dom';

import { CityTaxBreakdown, PromoBox } from '../../common';
import { getCorrectBundlePrice } from '../../hotel-details/BundleChoice';
import type { BasketProps } from './Basket.container';
import { getRoomLabel } from './Basket.helpers';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    analyticsDataCartConfirmation: AnalyticsDataCartConfirmation;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

type RoomClassKey = keyof typeof RoomClass;

interface Props extends Omit<BasketProps, 'selectedRate' | 'handleBooking'> {
  totalReservationAmount: number;
  totalCityTaxAmount: number;
  cityTaxRoomPrice: number;
  currencyCode: string;
  dailyPricesPerRoom: HIDailyPrice[][];
  rateName: string;
  rateTags?: string[];
  roomCodes: ReservationRoomType[];
  onBookReservation: (pmsRoomTypes?: string[]) => void;
  brand: string;
  channel: Channel;
  isCityTaxEnabled?: boolean;
  twinroomSelections?: string[];
  upgradeRoomContent?: RoomUpgradeContent;
  isAccessibleType: boolean;
  promoActions?: PromoActionsType;
  userChoice?: UserChoice[];
  isCityTaxBreakdownEnabled?: boolean;
  isSoftBundlesVisible?: boolean;
  adultsNumber?: number;
  metaSearchConfigs?: { rate: string; code: string }[];
  isRoomOnly?: boolean;
}

export default function Basket({
  isCityTaxExempt,
  isLastFewRooms,
  isHDPBasket,
  hasAccessibleRoom,
  hasTwinRoomChoice,
  shouldDisplayMobileBasket,
  numberOfUnits,
  onBookReservation,
  bookRsvIsLoading,
  bookRsvIsError,
  bookRsvError,
  numberOfNights,
  totalReservationAmount,
  totalCityTaxAmount,
  cityTaxRoomPrice,
  currencyCode,
  dailyPricesPerRoom,
  rateName,
  rateTags,
  roomCodes: reservationRoomTypes,
  roomTypeInformationResponse,
  isLessThanLg,
  brand,
  channel,
  isDisabledContinueBtn,
  twinroomSelections,
  upgradeRoomContent,
  isAccessibleType,
  isCityTaxEnabled,
  promoActions,
  userChoice = [],
  softBundles,
  isCityTaxBreakdownEnabled,
  isSoftBundlesVisible,
  adultsNumber,
  metaSearchConfigs,
  isRoomOnly,
}: Readonly<Props>) {
  const totalRoomOnlyAmount = isRoomOnly
    ? (dailyPricesPerRoom?.[0]?.slice(0, 14) ?? []).reduce(
        (prev, curr) => prev + (curr.roomNetPrice ?? 0),
        0
      )
    : 0;
  const [modalOpenCount, setModalOpenCount] = useSessionStorage<number>(
    'roomUpgradeModalCloseCount',
    0
  );
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);

  const [isBreakdownVisible, setIsBreakdownVisible] = useState(false);
  // `numberOfUnits` is derived from the `ROOMS` URL query param. Bounded before it
  // sizes an allocation so a crafted value cannot exhaust the heap (SRE-350).
  const breakdownRoomCount = Math.min(Math.max(numberOfUnits || 0, 0), MAX_ROOMS_SEARCH_LIMIT);
  const [domReady, setDomReady] = useState(false);
  const [isDisabledChooseRoomBtn, setIsDisabledChooseRoomBtn] = useState<boolean>(false);

  let {
    // eslint-disable-next-line prefer-const
    [FT_PI_BB_CCUI_CHOOSE_ROOM_TYPE]: isChooseRoomTypeEnabled,
    [FT_PI_ROOM_UPGRADE_POPUP]: isRoomUpgradeAvailableForPi,
    [FT_BB_ROOM_UPGRADE_POPUP]: isRoomUpgradeAvailableForBb,
    // eslint-disable-next-line prefer-const
    [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled,
  } = useFeatureToggle();

  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled &&
    getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;

  isRoomUpgradeAvailableForPi =
    isRoomUpgradeAvailableForPi && Boolean(channel === 'PI') && !isSoftBundlesVisible;
  isRoomUpgradeAvailableForBb = isRoomUpgradeAvailableForBb && Boolean(channel === 'BB');

  const shouldDisplaySoftBundlesInBasket = isSoftBundlesVisible && softBundles?.isOptional;

  const {
    description,
    heading,
    imageUrl,
    price,
    priceText,
    primaryButtonText,
    roomClass,
    secondaryButtonText,
    selectedRoomClass,
    pmsRoomTypes,
  } = upgradeRoomContent || {};

  const dateFormat = 'EEE dd MMM y';
  const { t } = useTranslation(['common']);
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  const twinRoomLabels: string[] = [
    t('twinroom.improvedTwin.title'),
    t('twinroom.standardTwin.title'),
  ];

  const [screenSize, setScreenSize] = useState({ height: 0 });

  useEffect(() => {
    const handleResize = () => {
      setScreenSize({
        height: window.innerHeight,
      });
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    setDomReady(true);
    setIsDisabledChooseRoomBtn(false);
    sessionStorage.removeItem('softBundles');
  }, []);

  const handleUpgradeModalClose = React.useCallback(() => {
    setIsModalVisible(false);
    setIsLoading(false);
    onBookReservation();
    window.__satelliteLoaded && window._satellite.track('hdp_room_upsell_popup_close');
  }, [onBookReservation]);

  const handleOpenUpgradeModal = React.useCallback(() => {
    if (modalOpenCount < 3 || roomClass) {
      window.__satelliteLoaded && window._satellite.track('hdp_room_upsell_popup_shown');
      analytics.update({
        upsellPopup: {
          upgradeTotalPrice: formatPrice(
            formatCurrency(currencyCode),
            formatRatePrice(totalReservationAmount + (price as number)),
            language
          ),
          upgradeIncrementalPrice: formatPrice(
            formatCurrency(currencyCode),
            formatRatePrice(price as number),
            language
          ),
          currentRoom: RoomClass[selectedRoomClass as RoomClassKey],
          upgradeRoom: RoomClass[roomClass as RoomClassKey],
        },
      });
      setModalOpenCount(modalOpenCount + 1);
      setIsModalVisible(true);
    }
  }, [modalOpenCount, setModalOpenCount]);

  if (bookRsvIsError) {
    return <Text>{(bookRsvError as Error).message}</Text>;
  }

  // roomTypeInformation query
  if (roomTypeInformationResponse?.isLoadingRoomTypeInformation) {
    return (
      <Text data-testid="room-types-loading-message">{t('searchresults.list.hotel.loading')}</Text>
    );
  }

  if (roomTypeInformationResponse?.isErrorRoomTypeInformation) {
    return <Text>{(roomTypeInformationResponse.errorRoomTypeInformation as Error).message}</Text>;
  }

  const upgradePricePerNight = formatPrice(
    formatCurrency(currencyCode),
    formatRatePrice((price as number) / numberOfNights),
    language
  );

  const currencyPrice = formatPrice(
    formatCurrency(currencyCode),
    formatRatePrice(price as number),
    language
  );

  // Construct variantProps for upgrade modal
  const upgradeModalVariantProps = {
    title: heading as string,
    description: renderSanitizedHtml(description as string),
    priceText: priceText?.replace('{{price}}', upgradePricePerNight),
    primaryButtonLabel: primaryButtonText?.replace('{{price}}', currencyPrice),
    secondaryButtonLabel: secondaryButtonText,
    onPrimaryAction: () => {
      setIsLoading((v) => !v);
      onBookReservation(pmsRoomTypes);
      window.__satelliteLoaded && window._satellite.track('hdp_room_upsell_popup_upgraded');
    },
    onSecondaryAction: () => {
      setIsLoading((v) => !v);
      onBookReservation();
      window.__satelliteLoaded && window._satellite.track('hdp_room_upsell_popup_continue');
    },
    imageSrc: formatAssetsUrl(imageUrl as string),
  };

  const landscapeModal = {
    primary: screenSize?.height <= 503,
    secondary: screenSize?.height <= 320,
  };

  const bundlePrice = shouldDisplaySoftBundlesInBasket
    ? getCorrectBundlePrice(softBundles?.softBundleContent ?? [], adultsNumber ?? 1, numberOfNights)
    : 0;

  const totalBundlePrice = totalReservationAmount + bundlePrice;

  const handleAddSoftBundlesToSession = () => {
    sessionStorage.setItem('softBundles', JSON.stringify(softBundles?.softBundleContent ?? []));
  };

  return (
    <>
      {/* Basket on HDP */}
      {!isLessThanLg && isHDPBasket && renderDesktopBasket()}
      {isLessThanLg && shouldDisplayMobileBasket && isHDPBasket && renderMobileBasket()}
      {/* Basket on choose-bathroom, choose-twinroom page */}
      {!shouldDisplayMobileBasket && !isHDPBasket && renderDesktopBasket()}
      {shouldDisplayMobileBasket && !isHDPBasket && renderMobileBasket()}
      {bookRsvIsError && (
        <Box mt="lg">
          <Notification
            svg={<Error />}
            status="error"
            description={t('hoteldetails.booking.error')}
            variant="error"
          />
        </Box>
      )}
      {isCityTaxExempt && (
        <Box mt="lg">
          <Notification
            svg={<InfoOutlineIcon />}
            status="info"
            description={t('config.policyOfCityTaxGlobalMessage.globalMessage')}
            prefixDataTestId="hdp_basketCityTax"
            variant="info"
          />
        </Box>
      )}
      {isModalVisible && modalOpenCount <= 3 && (
        <ModalVariants
          isOpen={isModalVisible}
          onClose={handleUpgradeModalClose}
          variant="upgrade"
          variantProps={upgradeModalVariantProps}
          headerContentStyles={roomUpgradeModalStyles.headerContent}
          headerStyles={roomUpgradeModalStyles.header}
          contentContainerStyles={roomUpgradeModalStyles.contentContainer}
          updatedWidth={roomUpgradeModalStyles.width}
          dataTestId="hdp_roomUpgradeModalPopup"
          landscapeModal={landscapeModal}
          isLoading={isLoading}
        >
          <></>
        </ModalVariants>
      )}
    </>
  );

  function renderDesktopBasket() {
    return (
      <Box data-testid="basket" w="19.3125rem">
        <Card>
          <Flex direction="column" w="full">
            {isLastFewRooms && (
              <Text {...cardHeaderStyles} data-testid="hdp_basketLastFewRooms">
                {t('hoteldetails.lastfewrooms')}
              </Text>
            )}

            {numberOfUnits > 1 ? (
              <Text
                {...cardRateLayoutStyles}
                {...getTypographyProps(cardRateLegacyTypography, cardRateSemanticTypography)}
                data-testid="hdp_basketNrOfRooms"
              >
                {t('hoteldetails.nrOfRooms', { count: numberOfUnits })},{' '}
                {`${brand.toLowerCase() === 'hub' ? 'hub ' : ''}${rateName}`}
              </Text>
            ) : (
              <>
                <Text
                  {...cardTitleLayoutStyles}
                  {...getTypographyProps(cardTitleLegacyTypography, cardTitleSemanticTypography)}
                  data-testid="hdp_basketRoomAndRatePlan"
                >
                  {getRoomLabel(
                    0,
                    noRoomTypeSearch,
                    roomTypeInformationResponse,
                    userChoice,
                    reservationRoomTypes,
                    twinRoomLabels,
                    twinroomSelections
                  )}
                </Text>
                <Text
                  {...cardRateLayoutStyles}
                  {...getTypographyProps(cardRateLegacyTypography, cardRateSemanticTypography)}
                >
                  {`${brand.toLowerCase() === 'hub' ? 'hub ' : ''}${rateName}`}
                </Text>
              </>
            )}
            <Divider {...dividerStyles} />
            <Flex justify="space-between" data-testid="hdp_basketStayPrice">
              <Box data-testid="hdp_basketStay">
                <Text
                  {...stayPriceLayoutStyles}
                  {...getTypographyProps(stayPriceLegacyTypography, stayPriceSemanticTypography)}
                  data-testid="hdp_basketStayText"
                >
                  {t('pihotelinfo.stay')}
                </Text>
                <Text
                  {...nightsNrLayoutStyles}
                  {...getTypographyProps(nightsNrLegacyTypography, nightsNrSemanticTypography)}
                  data-testid="hdp_basketNrOfNightsStay"
                >
                  {t('hoteldetails.nrOfNights', { count: numberOfNights })}
                </Text>
              </Box>

              <Box textAlign="right" data-testid="hdp_basketPrice">
                <Text
                  {...stayPriceLayoutStyles}
                  {...getTypographyProps(stayPriceLegacyTypography, stayPriceSemanticTypography)}
                  data-testid="hdp_basketPriceText"
                >
                  {t('account.dashboard.booking.price')}
                </Text>
                <Flex>
                  {!isBreakdownVisible && (
                    <Link
                      href="#"
                      {...isBreakdownVisibleStyles}
                      {...getTypographyProps(
                        breakdownLinkLegacyTypography,
                        breakdownLinkSemanticTypography
                      )}
                      data-testid="hdp_basketSeeBreakdownLink"
                      onClick={(e) => handleShowBreakdownClick(e)}
                    >
                      {t('pihotelinfo.breakdownShow')}
                    </Link>
                  )}
                  <Text
                    data-testid="total-cost-for-nights"
                    {...nightsNrLayoutStyles}
                    {...getTypographyProps(nightsNrLegacyTypography, nightsNrSemanticTypography)}
                  >
                    {isCityTaxEnabled && isCityTaxBreakdownEnabled
                      ? formatPrice(
                          formatCurrency(currencyCode),
                          formatRatePrice(cityTaxRoomPrice),
                          language
                        )
                      : formatPrice(
                          formatCurrency(currencyCode),
                          formatRatePrice(
                            isRoomOnly ? totalRoomOnlyAmount : totalReservationAmount
                          ),
                          language
                        )}
                  </Text>
                </Flex>
              </Box>
            </Flex>
            {isCityTaxEnabled && isCityTaxBreakdownEnabled && (
              <CityTaxBreakdown
                currencyCode={currencyCode}
                totalCityTaxAmount={totalCityTaxAmount}
                language={language}
              />
            )}

            <Divider {...dividerStyles} />

            {isBreakdownVisible && numberOfUnits === 1 && (
              <Box>
                <Box>
                  {dailyPricesPerRoom?.[0]?.slice(0, 14)?.map((item) => {
                    const bundlePrice = +(item.netPrice - (item.roomNetPrice ?? 0)).toFixed(2);
                    const effectiveRate = isRoomOnly
                      ? item.effectiveRate - bundlePrice
                      : item.effectiveRate;
                    const netPrice = isRoomOnly ? (item.roomNetPrice ?? 0) : item.netPrice;
                    return (
                      <Text
                        key={item.date}
                        {...breakdownRowStyles}
                        data-testid="hdp_basketBreakdownRow"
                      >
                        <span>{formatDate(item.date, dateFormat, language)}</span>
                        <span>
                          {formatPrice(
                            formatCurrency(currencyCode),
                            formatRatePrice(isCityTaxEnabled ? effectiveRate : netPrice),
                            language
                          )}
                        </span>
                      </Text>
                    );
                  })}
                </Box>

                <Box textAlign="right" data-testid="hdp_basketHideBreakdown">
                  <Link
                    href="#"
                    {...closeBreakdownStyles}
                    {...getTypographyProps(
                      breakdownLinkLegacyTypography,
                      breakdownLinkSemanticTypography
                    )}
                    data-testid="hdp_basketHideBreakdownLink"
                    onClick={(e) => handleCloseBreakdownClick(e)}
                  >
                    {t('pihotelinfo.breakdownHide')}
                  </Link>
                </Box>

                <Divider {...dividerStyles} />
              </Box>
            )}

            {isBreakdownVisible &&
              numberOfUnits > 1 &&
              Array.from({ length: breakdownRoomCount }, (_, i) => i + 1).map((index) => (
                <Box key={`rate-breakdown-${index}`}>
                  <Text data-testid="breakdown-room-number" fontSize="sm" mb="2">
                    <strong>
                      {t('booking.hotel.summary.room').replace('[roomNumber]', String(index))}
                    </strong>{' '}
                    (
                    {getRoomLabel(
                      index - 1,
                      noRoomTypeSearch,
                      roomTypeInformationResponse,
                      userChoice,
                      reservationRoomTypes,
                      twinRoomLabels,
                      twinroomSelections
                    )}
                    )
                  </Text>

                  <Box>
                    {dailyPricesPerRoom?.[index - 1]?.slice(0, 14)?.map((item) => (
                      <Text key={item.date} {...breakdownRowStyles}>
                        <span>{formatDate(item.date, dateFormat, language)}</span>
                        <span>
                          {formatPrice(
                            formatCurrency(currencyCode),
                            formatRatePrice(isCityTaxEnabled ? item.effectiveRate : item.netPrice),
                            language
                          )}
                        </span>
                      </Text>
                    ))}
                  </Box>

                  {index === numberOfUnits && (
                    <Box textAlign="right">
                      <Link
                        href="#"
                        {...closeBreakdownStyles}
                        {...getTypographyProps(
                          breakdownLinkLegacyTypography,
                          breakdownLinkSemanticTypography
                        )}
                        onClick={(e) => handleCloseBreakdownClick(e)}
                      >
                        {t('pihotelinfo.breakdownHide')}
                      </Link>
                    </Box>
                  )}

                  <Divider {...dividerStyles} />
                </Box>
              ))}

            {shouldDisplaySoftBundlesInBasket && (
              <>
                {softBundles?.softBundleContent
                  ?.filter((pack) => !pack.strikeThrough)
                  .map((bundleItem: SoftBundle) => (
                    <Flex
                      justify="space-between"
                      data-testid="hdp_bundleItem"
                      mb="xs"
                      key={bundleItem.name}
                    >
                      <Text
                        as="div"
                        maxW="188px"
                        lineHeight="150%"
                        data-testid="hdp_bundleItemName"
                      >
                        {renderSanitizedHtml(bundleItem.name as string)}
                      </Text>
                      <Text
                        data-testid="hdp_bundleItemPrice"
                        {...nightsNrLayoutStyles}
                        {...getTypographyProps(
                          nightsNrLegacyTypography,
                          nightsNrSemanticTypography
                        )}
                      >
                        {formatPrice(
                          formatCurrency(currencyCode),
                          formatRatePrice(
                            getCorrectBundlePrice([bundleItem], adultsNumber ?? 1, numberOfNights)
                          ),
                          language
                        )}
                      </Text>
                    </Flex>
                  ))}
                <Divider
                  my="md"
                  display={softBundles?.softBundleContent?.length ? 'block' : 'none'}
                />
              </>
            )}

            <Flex wrap="wrap" {...totalCostWrapperStyles} data-testid="hdp_basketTotal">
              <Text
                data-testid="total-cost-title"
                {...getTypographyProps({}, totalCostTitleSemanticTypography)}
              >
                {t('hoteldetails.rates.total')}
              </Text>
              <Text data-testid="total-cost">
                <PencePrice
                  price={isRoomOnly ? totalRoomOnlyAmount : totalBundlePrice}
                  currency={currencyCode}
                  language={language}
                  size="xs"
                />
              </Text>

              {isCityTaxExempt && (
                <Text align="right" {...cityTaxExemptStyles} data-testid="hdp_cityTaxExemptText">
                  {t('hoteldetails.rates.cityTax')}
                </Text>
              )}
            </Flex>
            {isCityTaxEnabled && (
              <Text data-testid="city-tax-info-message" align="right" {...cityTaxInfoMessageStyles}>
                {t('hoteldetails.rates.cityTaxAndCharges')}
              </Text>
            )}
            {rateTags && rateTags.length > 0 && (
              <Box {...PromotionStyles} data-testid="hdp_discountPromoTag">
                <Text>{rateTags && t('booking.summary.includes')}</Text>
                <PromoTag
                  rateDiscountTags={rateTags}
                  customStyleName="basketComponent"
                  promoActions={promoActions}
                ></PromoTag>
              </Box>
            )}
            {renderButton()}
            {!isSoftBundlesVisible && (
              <PromoBox
                channel={channel}
                promoActions={promoActions as PromoActionsType}
                metaSearchConfigs={metaSearchConfigs ?? []}
              />
            )}
          </Flex>
        </Card>
      </Box>
    );
  }

  function renderMobileBasket() {
    return domReady
      ? ReactDOM.createPortal(
          <Box data-testid="mobile-basket" w="full">
            <Card {...mobileBreakdownScrollableStyles}>
              <Flex direction="column" w="full" className="basket-breakdown">
                <Flex direction="column" className="basket-breakdown__rooms--mobile">
                  {isBreakdownVisible && numberOfUnits === 1 && (
                    <Box>
                      <Box>
                        {dailyPricesPerRoom?.[0]?.slice(0, 14)?.map((item) => (
                          <Text
                            key={item.date}
                            {...breakdownRowStyles}
                            data-testid="hdp_mobileBasketBreakdownRow"
                          >
                            <span>{formatDate(item.date, dateFormat, language)}</span>
                            <span>
                              {formatPrice(
                                formatCurrency(currencyCode),
                                formatRatePrice(
                                  isCityTaxEnabled ? item.effectiveRate : item.netPrice
                                ),
                                language
                              )}
                            </span>
                          </Text>
                        ))}
                      </Box>
                      <Divider {...mobileDividerStyles} />
                    </Box>
                  )}
                  {isBreakdownVisible &&
                    numberOfUnits > 1 &&
                    Array.from({ length: breakdownRoomCount }, (_, i) => i + 1).map((index) => (
                      <Box key={`rate-breakdown-${index}`}>
                        <Text data-testid="breakdown-room-number" fontSize="sm" mb="2">
                          <strong>
                            {t('booking.hotel.summary.room').replace('[roomNumber]', String(index))}
                          </strong>{' '}
                          (
                          {getRoomLabel(
                            index - 1,
                            noRoomTypeSearch,
                            roomTypeInformationResponse,
                            userChoice,
                            reservationRoomTypes,
                            twinRoomLabels,
                            twinroomSelections
                          )}
                          )
                        </Text>
                        <Box>
                          {dailyPricesPerRoom?.[index - 1]?.slice(0, 14)?.map((item) => (
                            <Text key={item.date} {...breakdownRowStyles}>
                              <span>{formatDate(item.date, dateFormat, language)}</span>
                              <span>
                                {formatPrice(
                                  formatCurrency(currencyCode),
                                  formatRatePrice(
                                    isCityTaxEnabled ? item.effectiveRate : item.netPrice
                                  ),
                                  language
                                )}
                              </span>
                            </Text>
                          ))}
                        </Box>
                        <Divider {...mobileDividerStyles} />
                      </Box>
                    ))}

                  {isBreakdownVisible && isCityTaxEnabled && isCityTaxBreakdownEnabled && (
                    <>
                      <CityTaxBreakdown
                        currencyCode={currencyCode}
                        totalCityTaxAmount={totalCityTaxAmount}
                        language={language}
                      />
                      <Divider {...mobileDividerStyles} />
                    </>
                  )}
                </Flex>

                <Flex className="basket-breakdown__total--mobile">
                  <Flex direction="column" w="full">
                    {isLastFewRooms && (
                      <Text {...mobileHeaderStyles} data-testid="hdp_mobileBasketLastFewRooms">
                        {t('hoteldetails.lastfewrooms')}
                      </Text>
                    )}
                    <Flex {...mobileTotalCostStyles}>
                      {router.locale === 'gb' ? (
                        <Text data-testid="mobile-total-cost-title" mr="1">
                          {t('hoteldetails.rates.total')}
                        </Text>
                      ) : null}
                      <Text data-testid="mobile-total-cost">
                        <PencePrice
                          price={totalBundlePrice}
                          currency={currencyCode}
                          language={language}
                          size="xs"
                        />
                      </Text>
                    </Flex>
                    {!isBreakdownVisible ? (
                      <Link
                        href="#"
                        {...isBreakdownVisibleStyles}
                        {...getTypographyProps(
                          breakdownLinkLegacyTypography,
                          breakdownLinkSemanticTypography
                        )}
                        data-testid="hdp_mobileBasketSeeBreakdownLink"
                        onClick={(e) => handleShowBreakdownClick(e)}
                      >
                        {t('pihotelinfo.breakdownShow')}
                      </Link>
                    ) : (
                      <Link
                        href="#"
                        {...mobileCloseBreakdownStyles}
                        {...getTypographyProps(
                          breakdownLinkLegacyTypography,
                          breakdownLinkSemanticTypography
                        )}
                        data-testid="hdp_mobileBasketHideBreakdownLink"
                        onClick={(e) => handleCloseBreakdownClick(e)}
                      >
                        {t('pihotelinfo.breakdownHide')}
                      </Link>
                    )}
                  </Flex>
                  <Flex direction="column" justify="flex-end" align="center" w="48">
                    {renderButton()}
                  </Flex>
                </Flex>
              </Flex>
            </Card>
          </Box>,
          document.getElementById('hotel-details-mobile-basket') as HTMLElement
        )
      : null;
  }

  function handleAccessibleBtnClick() {
    if (isSoftBundlesVisible) {
      handleAddSoftBundlesToSession();
    }
    if (isChooseAccessibleRoom())
      if (isChooseRoomTypeEnabled && brand === HotelBrand.PID)
        if (isPremierPlusRoom() || !isAccessibleType) onBookReservation();
        else redirectToChooseRoomTypePage();
      else redirectToChooseBathroomPage();
    else redirectToTwinroomPage();
  }

  function renderButton() {
    // Choose twin room or Choose accessible room button
    if (isChooseAccessibleRoom() || isChooseTwinRoom()) {
      return (
        <Button
          id={isChooseAccessibleRoom() ? 'chooseBathroomCTA' : 'chooseTwinroomCTA'}
          variant="primary"
          size={shouldDisplayMobileBasket ? 'sm' : 'md'}
          w="full"
          onClick={handleAccessibleBtnClick}
          isDisabled={isDisabledChooseRoomBtn}
          data-testid={
            shouldDisplayMobileBasket
              ? 'hdp_mobileBasketChooseRoomTypeButton'
              : 'hdp_basketChooseRoomTypeButton'
          }
        >
          {(isAccessibleType || isChooseTwinRoom()) &&
          (brand === HotelBrand.PI ||
            (isChooseRoomTypeEnabled && brand === HotelBrand.PID && !isPremierPlusRoom()))
            ? t('hoteldetails.rates.chooseroomtype')
            : t('booking.summary.continue')}
        </Button>
      );
    }

    const buttonTextHDP =
      bookRsvIsLoading || isDisabledContinueBtn
        ? t('hoteldetails.booking')
        : t('hoteldetails.booknowtext');

    // Continue button - in HDP for default flow
    return (
      <Button
        variant="primary"
        size={shouldDisplayMobileBasket ? 'sm' : 'md'}
        w="full"
        {...getTypographyProps({}, bookNowButtonSemanticTypography)}
        onClick={
          (isRoomUpgradeAvailableForPi || isRoomUpgradeAvailableForBb) &&
          modalOpenCount < 3 &&
          roomClass
            ? handleOpenUpgradeModal
            : () => {
                handleAddSoftBundlesToSession();
                setIsModalVisible(false);
                onBookReservation();
              }
        }
        isDisabled={bookRsvIsLoading || isDisabledContinueBtn}
        data-testid={
          shouldDisplayMobileBasket ? 'hdp_mobileBasketBookNowButton' : 'hdp_basketBookNowButton'
        }
      >
        {isHDPBasket ? buttonTextHDP : t('booking.summary.continue')}
      </Button>
    );
  }

  function isChooseAccessibleRoom(): boolean {
    return (
      hasAccessibleRoom &&
      isHDPBasket &&
      (!isGermanHotel() || isChooseRoomTypeEnabled) &&
      !isHubOrZipHotel()
    );
  }

  function isPremierPlusRoom(): boolean {
    return reservationRoomTypes.some(({ roomClass }: any) => roomClass === 'PP');
  }

  // only go to Twin room flow if there are no accessible rooms; as accessible room has priority.
  function isChooseTwinRoom(): boolean {
    return (
      hasTwinRoomChoice &&
      !hasAccessibleRoom &&
      isHDPBasket &&
      !isHubOrZipHotel() &&
      !isGermanHotel()
    );
  }

  function handleShowBreakdownClick(e: React.MouseEvent<HTMLElement>) {
    e.preventDefault();
    setIsBreakdownVisible(true);
  }

  function handleCloseBreakdownClick(e: React.MouseEvent<HTMLElement>) {
    e.preventDefault();
    setIsBreakdownVisible(false);
  }

  function redirectToChooseBathroomPage() {
    setIsDisabledChooseRoomBtn(true);
    router.push(
      `/${country}/${language}${channel === 'BB' ? '/business-booker' : ''}/hotels/choose-bathroom`
    );
  }

  function redirectToChooseRoomTypePage() {
    setIsDisabledChooseRoomBtn(true);

    router.push(
      `/${country}/${language}${channel === 'BB' ? '/business-booker' : ''}/hotels/choose-roomtype`
    );
  }

  function redirectToTwinroomPage() {
    setIsDisabledChooseRoomBtn(true);
    router.push(
      `/${country}/${language}${channel === 'BB' ? '/business-booker' : ''}/hotels/choose-twinroom`
    );
  }

  function isGermanHotel() {
    const urlSlug = ['/hotels', ...(router.query.slug as string[])].join('/');
    return urlSlug.includes('/hotels/germany') || urlSlug.includes('/hotels/deutschland');
  }

  function isHubOrZipHotel() {
    return ['hub', 'zip'].includes(brand?.toLowerCase());
  }
}

const mobileHeaderStyles = {
  lineHeight: '2',
  fontSize: 'sm',
  color: 'alert',
  fontWeight: 'semibold',
};

const cardHeaderStyles = {
  ...mobileHeaderStyles,
  mb: 'sm',
};

const cardTitleLayoutStyles = {
  mb: '0.5',
};

const cardTitleLegacyTypography = {
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'semibold',
};

const cardTitleSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const stayPriceLayoutStyles = {
  w: 'full',
  mb: '1',
  color: 'darkGrey1',
};

const stayPriceLegacyTypography = {
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'semibold',
};

const stayPriceSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const nightsNrLayoutStyles = {
  color: 'darkGrey1',
};

const nightsNrLegacyTypography = {
  fontSize: 'md',
};

const nightsNrSemanticTypography = {
  textStyle: 'body-m-regular',
};

const isBreakdownVisibleStyles = {
  mr: 'sm',
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  cursor: 'pointer',
};

const breakdownLinkLegacyTypography = {
  fontSize: 'sm',
};

const breakdownLinkSemanticTypography = {
  textStyle: 'link-s-regular',
};

const dividerStyles = {
  mt: 'md',
  mb: 'lg',
};

const mobileDividerStyles = {
  my: 'md',
};

const breakdownRowStyles = {
  display: 'flex',
  justifyContent: 'space-between',
};

const mobileCloseBreakdownStyles = {
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  cursor: 'pointer',
};

const closeBreakdownStyles = {
  ...mobileCloseBreakdownStyles,
  mt: 'sm',
};

const totalCostWrapperStyles = {
  justify: 'space-between',
  fontSize: 'xl',
  fontWeight: 'bold',
  color: 'darkGrey1',
  mb: 'xs',
};

const totalCostTitleSemanticTypography = {
  textStyle: 'heading-s',
};

const bookNowButtonSemanticTypography = {
  textStyle: 'label-xl',
};

const mobileTotalCostStyles = {
  fontSize: 'xl',
  fontWeight: 'bold',
  color: 'darkGrey1',
};

const cityTaxExemptStyles = {
  width: '100%',
  fontSize: 'xs',
  fontWeight: 'normal',
};
const cardRateLayoutStyles = {
  color: 'primary',
  mb: '0.5',
};

const cardRateLegacyTypography = {
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'semibold',
};

const cardRateSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const roomUpgradeModalStyles = {
  headerContent: { color: '#511E62', fontWeight: 900 },
  header: { color: '#511E62', fontWeight: 900 },
  contentContainer: { color: '#511E62', fontWeight: 900 },
  width: { sm: '100%', md: '37.5rem', lg: '37.5rem', xl: '39.93rem' },
};

// scrollable breakdown for mobile
const mobileBreakdownScrollableStyles = {
  padding: 'var(--chakra-space-md) 0',
  sx: {
    '.basket-breakdown': {
      '&__rooms--mobile': {
        overflowY: 'auto',
        maxHeight: '60vh',
        padding: '0 var(--chakra-space-md)',
      },
      '&__total--mobile': {
        padding: '0 var(--chakra-space-md)',
      },
    },
  },
};
const PromotionStyles = {
  color: 'darkGrey2',
  display: 'flex',
  justifyContent: 'end',
  alignItems: 'center',
  mb: 'xmd',
};

const cityTaxInfoMessageStyles = {
  fontWeight: 'var(--chakra-fontWeights-normal)',
  lineHeight: 'var(--chakra-lineHeights-2)',
  color: 'var(--chakra-colors-darkGrey1)',
  fontSize: 'var(--chakra-fontSizes-xxs)',
  mb: 'md',
};
