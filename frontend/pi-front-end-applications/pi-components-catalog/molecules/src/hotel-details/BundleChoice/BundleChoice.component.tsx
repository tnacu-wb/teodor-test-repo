import {
  Badge,
  Box,
  Collapse,
  Flex,
  FlexProps,
  HStack,
  Link,
  RadioGroup,
  Text,
} from '@chakra-ui/react';
import {
  Currency,
  HIAEMroomType,
  HIRoomRate,
  ActiveChoiceType,
  SoftBundles,
  SoftBundle,
  HIRoomClassCode,
  HIRoomType,
  FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE,
  FT_PI_ROOM_DETAILS_DRAWER,
  HIRatesInformation,
  HIRateClassification,
  Language,
  LanguageEnum,
  HIRoom,
} from '@whitbread-eos/api';
import { Info, RadioCard, Icon, Error, Tick, Tick24, RadioButton } from '@whitbread-eos/atoms';
import {
  analytics,
  formatAssetsUrl,
  formatDataTestId,
  useScreenSize,
  getCookie,
  BUNDLE_CHOICE,
  BUNDLE_CHOICE_OPTIONS,
  getRoomClassByRoomClassCode,
  getAllRoomTypesFromRates,
  getAvailableRoomTypes,
  getRoomClassByCodeAndType,
  useFeatureToggle,
  useStaticHotelInformation,
  getRoomRatesThatMatchRoomClassifications,
  getRateClassification,
  getRoomRatesWithRateInformation,
  hasSameRoomTypeCodes,
  formatPrice,
  formatCurrency,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { useCallback, useState, useRef } from 'react';

import { BundleSideDrawer, RoomSideDrawer } from '../SideDrawer';
import { BundleExtra } from './BundleExtra/BundleExtra.component';

export const isNewBundleContainedInActiveBundle = (
  newBundle: SoftBundles | undefined,
  activeBundle: SoftBundles | undefined
): boolean => {
  const newBundlePackages = newBundle?.softBundleContent?.map((b) => b.id ?? '') ?? [];
  const activeBundlePackages = activeBundle?.softBundleContent?.map((b) => b.id ?? '') ?? [];

  return newBundlePackages.every((id) => activeBundlePackages.includes(id));
};

export const formatRatePrice = (price: number, currency: string, language: string) => {
  const currencySymbol = currency === Currency.EUR_NAME ? Currency.EUR : Currency.GBP;
  const isSymbolAfterPrice = language === LanguageEnum.GERMAN && currency === Currency.EUR_NAME;

  return (
    <Text as="span" fontSize="xl" fontWeight="900" data-testid="Soft-Bundle-Rate-Price-Main">
      {!isSymbolAfterPrice && currencySymbol}
      {price.toFixed(2).split('.')[0]}
      <Box
        as="span"
        fontSize="xxs"
        fontWeight="semibold"
        verticalAlign="text-top"
        ml="1px"
        data-testid="Soft-Bundle-Rate-Price-Decimals"
      >
        <Text display="none">{isSymbolAfterPrice ? ',' : '.'}</Text>
        {price.toFixed(2).split('.')[1]}
      </Box>
      {isSymbolAfterPrice && currencySymbol}
    </Text>
  );
};

export const attachmentLink = (redirectLink = '/', label: string | undefined, testId: string) => {
  const link = redirectLink.startsWith('http') ? redirectLink : formatAssetsUrl(redirectLink);

  const allergyTextStyle = {
    textDecoration: 'underline',
    lineHeight: '2',
    color: 'btnSecondaryEnabled',
    fontWeight: 'normal',
    fontSize: 'sm',
  };

  return (
    redirectLink &&
    label && (
      <Link href={link} isExternal>
        <Text {...allergyTextStyle} data-testid={testId}>
          {label}
        </Text>
      </Link>
    )
  );
};

type RateCode = 'FLEXRATE' | 'SEMIFLEX' | 'ADVANCE' | 'STANDARD' | 'NONFLEX';
const rateOptions = {
  FLEXRATE: [
    { en: 'Pay on arrival available', de: 'Zahlung sofort oder bei Anreise', isCheck: true },
    {
      en: 'Cancel up to 1pm on day of arrival',
      de: 'Kostenlose Stornierung bis 13:00 Uhr am Anreisetag',
      isCheck: true,
    },
    {
      en: 'Dates and extras can be amended',
      de: 'Kostenlose Änderung des Ankunftsdatums und Extras',
      isCheck: true,
    },
  ],
  SEMIFLEX: [
    { en: 'Pay now', de: 'Zahlung sofort fällig', isCheck: false },
    {
      en: 'Cancel up to 3 days before',
      de: 'Kostenlose Stornierung bis zu 3 Tage vor dem Anreisedatum',
      isCheck: true,
    },
    {
      en: 'Dates and extras can be amended',
      de: 'Kostenlose Änderung des Ankunftsdatums und Extras',
      isCheck: true,
    },
  ],
  ADVANCE: [
    { en: 'Pay now', de: 'Zahlung sofort fällig', isCheck: false },
    {
      en: 'Cancel up to 28 days before',
      de: 'Kostenlose Stornierung bis zu 28 Tage vor dem Anreisedatum',
      isCheck: true,
    },
    {
      en: 'Dates and extras can be amended',
      de: 'Kostenlose Änderung des Ankunftsdatums und Extras',
      isCheck: true,
    },
  ],
  STANDARD: [
    { en: 'Pay now', de: 'Zahlung sofort fällig', isCheck: false },
    { en: 'Non-refundable', de: 'Keine Stornierung möglich', isCheck: false },
    {
      en: 'Dates and extras can be amended',
      de: 'Kostenlose Änderung des Ankunftsdatums und Extras',
      isCheck: true,
    },
  ],
  NONFLEX: [
    { en: 'Pay now', de: 'Zahlung sofort fällig', isCheck: false },
    { en: 'Non-refundable', de: 'Keine Stornierung möglich', isCheck: false },
    { en: 'No changes', de: 'Keine Änderungen möglich', isCheck: false },
  ],
};

interface Props {
  brand: string;
  data: {
    roomRates: HIRoomRate[];
    roomTypes: HIAEMroomType[];
    ratesInformation: HIRatesInformation;
  };
  activeChoice: ActiveChoiceType;
  setActiveChoice: (
    value: ActiveChoiceType | ((prev: ActiveChoiceType) => ActiveChoiceType)
  ) => void;
  language?: string;
  nights: number;
  roomClassCodes: HIRoomClassCode[];
  selectedRoomClassAndRate: string;
  setSelectedRoomClassAndRate: (selectedClassAndRate: string) => void;
}

export default function BundleChoice({
  brand,
  data,
  activeChoice,
  setActiveChoice,
  language = 'en',
  nights,
  roomClassCodes,
  selectedRoomClassAndRate,
  setSelectedRoomClassAndRate,
}: Readonly<Props>) {
  const isClassVariant = getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.class;
  const isRoomOnlyVariant = getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.roomOnly;
  const isRateVariant = getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.rate;
  const baseDataTestId = 'BundleChoice';
  const { roomRates, roomTypes } = data;
  const {
    rate: activeRate,
    class: activeRoomClass,
    softBundle: activeBundle,
    isRoomOnly,
  } = activeChoice;
  const [readMore, setReadMore] = useState(false);
  const [isBundleSideDrawerVisible, setIsBundleSideDrawerVisible] = useState(false);
  const [bundleSideDrawerPacks, setBundleSideDrawerPacks] = useState<any[]>([]);
  const [showRoomDrawer, setShowRoomDrawer] = useState(false);
  const [roomDrawerDetails, setRoomDrawerDetails] = useState({
    roomBaseName: '',
    isPremierPlus: false,
    matchingTabItem: {},
  });
  const [isBundleChecked, setIsBundleChecked] = useState(false);
  const currency = roomRates?.[0]?.roomTypes[0].rooms[0].roomPriceBreakdown?.currencyCode;
  const adultsNumber = roomRates?.[0]?.roomTypes[0]?.adults ?? 1;
  const allRoomTypes: HIRoomType[] = getAllRoomTypesFromRates(roomRates);
  const {
    [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: isPremPlusAccFeatureFlag,
    [FT_PI_ROOM_DETAILS_DRAWER]: isRoomDetailsDrawerEnabled,
  } = useFeatureToggle();

  const { roomClassConfiguration, roomConfiguration } = useStaticHotelInformation();

  const rateRefs = useRef<Record<string, HTMLDivElement | null>>({});

  const getRoomRatesByRoomClassCode = (roomClassCode: string): HIRoomRate[] => {
    const byRoomClassCode = (roomRate: HIRoomRate): boolean => {
      return roomRate?.roomTypes?.every((roomType) =>
        roomType?.rooms?.some((room) => room?.roomClass === roomClassCode)
      );
    };
    return roomRates?.filter(byRoomClassCode);
  };

  const handleClassClick = (roomClassCode: string) => {
    const roomRatesByRoomClassCode = getRoomRatesByRoomClassCode(roomClassCode);
    const currentRate = activeChoice.rate;

    const foundRateInClass = roomRatesByRoomClassCode?.find(
      (rate) => rate.ratePlanCode === currentRate
    );

    const preselectedRate = foundRateInClass || roomRatesByRoomClassCode?.[0];

    handleRateClick(preselectedRate, roomClassCode);
  };

  const handleRateClick = useCallback(
    (rate: HIRoomRate, roomClass: string, event?: React.MouseEvent<HTMLDivElement>) => {
      if (event) event.stopPropagation();
      const rateCode = rate.ratePlanCode;

      rateRefs?.current?.[`${roomClass}-${rateCode}`]?.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
        inline: 'nearest',
      });

      const softBundle: SoftBundles | undefined = rate.roomTypes[0].rooms.find(
        (room) => room.roomClass === roomClass
      )?.softBundles;

      const hasIncludedBundle = !softBundle?.isOptional && !!softBundle?.softBundleContent?.length;

      setActiveChoice((prev: ActiveChoiceType) => ({
        ...prev,
        rate: rateCode,
        class: roomClass,
        softBundle: isBundleChecked || hasIncludedBundle ? softBundle : undefined,
      }));

      const rateIndex = roomRates?.findIndex((r) => r?.ratePlanCode === rateCode);
      setSelectedRoomClassAndRate(`${roomClass}-${rateIndex}`);
    },
    [isBundleChecked]
  );

  const handleBundleClick = (e: React.MouseEvent<HTMLDivElement>, softBundle: SoftBundles) => {
    e.stopPropagation();
    if (activeChoice.softBundle) {
      setIsBundleChecked(false);
      setActiveChoice((prev: ActiveChoiceType) => {
        return {
          ...prev,
          softBundle: undefined,
        };
      });
    } else {
      setIsBundleChecked(true);
      setActiveChoice((prev: ActiveChoiceType) => {
        return {
          ...prev,
          softBundle,
        };
      });
    }
  };

  const handleMealInfoClick = (event: any, bundle: SoftBundles) => {
    event.stopPropagation();
    setBundleSideDrawerPacks(
      bundle?.softBundleContent?.map((extra: SoftBundle) => ({
        name: extra?.name ?? '',
        description: extra.description ?? '',
        price: bundle.isOptional ? extra.price : 0,
        image: formatAssetsUrl(extra?.imageSrc ?? ''),
        links: extra?.attachments?.map((attachment: any) =>
          attachmentLink(attachment.path, attachment.label, `Side-Drawer-Attachment-Link`)
        ),
      })) ?? []
    );
    setIsBundleSideDrawerVisible(true);
    analytics.update({
      ...window.analyticsData,
      bundleInfoSelected: true,
      bundleInfo: (bundle.softBundleContent ?? []).map((c) => ({
        id: c.id ?? '',
        description: c.description ?? '',
        price: c.price ?? 0,
      })),
    });
  };

  const { t } = useTranslation();
  const { isLessThanMd } = useScreenSize();
  const linkName = readMore ? t('hoteldetails.readless') : t('hoteldetails.readmore');

  let extraCurrencySymbol = Currency.EUR;

  return (
    <Flex
      maxW="62.313rem"
      direction="column"
      data-testid={formatDataTestId(baseDataTestId, 'container')}
    >
      <Flex {...wrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'wrapper')}>
        {roomClassCodes?.map((roomClassCode) => {
          const roomRatesByRoomClassCode = getRoomRatesByRoomClassCode(roomClassCode);

          const pmsRoomType =
            roomRatesByRoomClassCode?.[0]?.roomTypes?.[0]?.rooms.find(
              (room) => room?.roomClass === roomClassCode
            )?.pmsRoomType ?? '';

          if (!pmsRoomType) return;

          const roomClass = roomTypes?.find(
            (roomClassInfo) =>
              roomClassInfo?.roomTypeCode === pmsRoomType ||
              roomClassInfo?.roomTypeCode.includes(pmsRoomType)
          );

          const roomBaseName =
            roomConfiguration?.tabGroups?.find(
              (roomGroupConfig) => roomGroupConfig.groupId === roomClass?.groupId
            )?.groupName ?? '';

          const matchingTabItem =
            roomConfiguration?.tabItems?.find((tabItem) =>
              hasSameRoomTypeCodes(tabItem?.roomTypeCode, pmsRoomType)
            ) ?? {};

          const foundMatchingTabItem = matchingTabItem && Object.keys(matchingTabItem).length > 0;

          const isClassChecked = activeRoomClass
            ? roomClassCode === activeRoomClass
            : roomClassCode === selectedRoomClassAndRate.split('-')[0];

          let rateSoftBundle: SoftBundles | null = null;
          if (isClassVariant && activeRate) {
            rateSoftBundle =
              roomRates
                .find((rate) => rate.ratePlanCode === activeRate)
                ?.roomTypes?.[0].rooms.find((room) => room.roomClass === roomClassCode)
                ?.softBundles ?? null;
          }

          const roomType = roomRatesByRoomClassCode?.[0]?.roomTypes?.[0]?.roomType || '';

          const rateClassifications =
            roomRates?.map((roomRate) =>
              getRateClassification(
                roomRate?.ratePlanCode,
                data?.ratesInformation?.rateClassifications
              )
            ) ?? [];

          const roomRatesThatMatchRoomClassifications = getRoomRatesThatMatchRoomClassifications(
            rateClassifications,
            roomRatesByRoomClassCode
          );

          const roomRatesWithRateInformation = getRoomRatesWithRateInformation(
            roomRatesThatMatchRoomClassifications,
            rateClassifications as HIRateClassification[]
          );

          return (
            <Box
              onClick={() => handleClassClick(roomClassCode)}
              data-roomClass={roomClassCode}
              key={roomClassCode}
              data-testid={isClassChecked ? 'Soft-Bundle-Class-Selected' : 'Soft-Bundle-Class'}
              sx={
                isClassChecked
                  ? {
                      '&::after': {
                        content: '""',
                        position: 'absolute',
                        top: '0',
                        right: '0',
                        width: '120px',
                        height: '120px',
                        bg: 'tertiary',
                        transform: 'translate(70px, -70px) rotate(-40.16deg)',
                        transformOrigin: 'center',
                      },
                    }
                  : undefined
              }
              {...{
                position: 'relative',
                overflow: 'hidden',
                borderRadius: '12px',
                p: { mobile: 'md', md: '1.25rem' },
                border: isClassChecked
                  ? '2px solid var(--chakra-colors-tertiary)'
                  : '1px solid var(--chakra-colors-lightGrey3)',
                cursor: isClassChecked ? 'default' : 'pointer',
              }}
            >
              {isClassChecked && (
                <Box
                  position="absolute"
                  top="10px"
                  right="7px"
                  zIndex={1}
                  data-testid={formatDataTestId(baseDataTestId, 'tickIcon')}
                >
                  <Icon svg={<Tick24 color="white" />} />
                </Box>
              )}
              <Flex direction={{ base: 'row-reverse', md: 'row' }} mb="1.25rem">
                {roomClass?.roomImage && (
                  <Flex {...imageContainerStyle}>
                    <Image
                      src={formatAssetsUrl(roomClass.roomImage)}
                      alt="Standard room"
                      fill
                      sizes="(max-width: 575px) 140px, (min-width: 576px) 178px"
                      style={{ objectFit: 'cover' }}
                      data-testid="Soft-Bundle-Class-Image"
                    />
                  </Flex>
                )}
                <Flex {...textContainerStyle} direction="column">
                  <Text
                    {...roomClassTextStyle}
                    onClick={() => {
                      if (!foundMatchingTabItem || !isRoomDetailsDrawerEnabled) return;
                      setRoomDrawerDetails({
                        roomBaseName: roomBaseName,
                        isPremierPlus: roomClassCode === 'PP',
                        matchingTabItem: matchingTabItem,
                      });
                      setShowRoomDrawer(true);
                    }}
                    data-testid="Soft-Bundle-Class-Name"
                  >
                    {getRoomClassByCodeAndType(
                      getRoomClassByRoomClassCode(roomClassCode, language),
                      roomClassCode,
                      roomType,
                      getAvailableRoomTypes(allRoomTypes),
                      t,
                      isPremPlusAccFeatureFlag,
                      roomClassConfiguration ?? []
                    )}
                  </Text>
                  {isLessThanMd ? (
                    <>
                      <Collapse
                        startingHeight={95}
                        in={readMore}
                        data-testid={formatDataTestId(baseDataTestId, 'collapse')}
                      >
                        <Box className="formatLinks">
                          <Text
                            data-testid="Soft-Bundle-Class-Description-Mobile"
                            {...descriptionTextStyle}
                          >
                            {roomClass?.roomDescription}
                          </Text>
                        </Box>
                      </Collapse>
                      <Link
                        {...linkStyles}
                        onClick={() => {
                          setReadMore(!readMore);
                        }}
                        data-testid={formatDataTestId(baseDataTestId, 'readmore-link')}
                      >
                        <Text
                          boxShadow={readMore ? 'none' : '0 0 2rem 1rem white'}
                          data-testid={formatDataTestId(baseDataTestId, 'readmore-link-text')}
                        >
                          {t(linkName)}
                        </Text>
                      </Link>
                    </>
                  ) : (
                    <Text {...descriptionTextStyle} data-testid="Soft-Bundle-Class-Description">
                      {roomClass?.roomDescription}
                    </Text>
                  )}
                </Flex>
              </Flex>
              <Flex direction="column" data-testid={formatDataTestId(baseDataTestId, 'rates-list')}>
                <HStack {...ratesListStyle}>
                  {roomRatesWithRateInformation.map((rate: HIRoomRate, index) => {
                    let isRateChecked = activeRate === rate.ratePlanCode;
                    const rateCurrency =
                      rate.roomTypes[0].rooms[0].roomPriceBreakdown?.currencyCode;

                    extraCurrencySymbol =
                      rateCurrency === Currency.EUR_NAME ? Currency.EUR : Currency.GBP;

                    const filterRoomsByClass = (room: HIRoom, roomClassCode: string) => {
                      return room.roomClass === roomClassCode;
                    };

                    const sumPricePerRoomType = (sum: number, pricePerRoomType: number) => {
                      return sum + pricePerRoomType;
                    };

                    const softBundles = rate.roomTypes[0].rooms.find((room) =>
                      filterRoomsByClass(room, roomClassCode)
                    )?.softBundles;

                    const totalReservationAmount =
                      rate.roomTypes
                        .map(
                          (roomType) =>
                            roomType.rooms.filter((room) =>
                              filterRoomsByClass(room, roomClassCode)
                            )[0].roomPriceBreakdown?.totalNetAmount ?? 0
                        )
                        .reduce(sumPricePerRoomType) ?? 0;

                    const totalRoomNetAmount =
                      rate.roomTypes
                        .map(
                          (roomType) =>
                            roomType.rooms.filter((room) =>
                              filterRoomsByClass(room, roomClassCode)
                            )[0].roomPriceBreakdown?.totalRoomNetAmount ?? 0
                        )
                        .reduce(sumPricePerRoomType) ?? 0;

                    const totalRoomBundleAmount = +(
                      totalReservationAmount - totalRoomNetAmount
                    ).toFixed(2);

                    if (isClassChecked && selectedRoomClassAndRate && !activeChoice.rate) {
                      // set default selection
                      const selectedRateIndex = +selectedRoomClassAndRate.split('-')[1];
                      if (index === selectedRateIndex) {
                        isRateChecked = true;
                        setActiveChoice({
                          rate: rate.ratePlanCode,
                          class: roomClassCode,
                          softBundle: softBundles?.isOptional ? undefined : softBundles,
                          isRoomOnly: false,
                        });
                      }
                    }

                    const { rateName } = rateClassifications.find(
                      (el) =>
                        el?.rateClassification === rate?.ratePlanCode ||
                        el?.ratePlanCode === rate?.ratePlanCode
                    ) as HIRateClassification;

                    if (!rateOptions[rate.ratePlanCode as RateCode]) return null;

                    const totalRatePrice =
                      isRoomOnly && isRateChecked ? totalRoomNetAmount : totalReservationAmount;

                    return (
                      <RadioCard
                        onClick={(e) => handleRateClick(rate, roomClassCode, e)}
                        ref={(el: HTMLDivElement) =>
                          (rateRefs.current[`${roomClassCode}-${rate.ratePlanCode}`] = el)
                        }
                        header={
                          <Flex
                            color={isRateChecked && isClassChecked ? 'white' : 'darkGrey1'}
                            justifyContent="space-between"
                            alignItems="center"
                            fontWeight="semibold"
                            width="full"
                          >
                            <Text ml="6px" data-testid="Soft-Bundle-Rate-Name">
                              {`${
                                brand.toLowerCase() === 'hub'
                                  ? `${t('booking.rates.hub.prefix')} `
                                  : ''
                              }${rateName}`}
                            </Text>
                            <Flex
                              direction="column"
                              alignItems="flex-end"
                              data-testid="Soft-Bundle-Rate-Price"
                            >
                              {formatRatePrice(totalRatePrice, rateCurrency, language)}
                              <Text
                                fontWeight="regular"
                                fontSize="xs"
                                data-testid="Soft-Bundle-Rate-Price-Label"
                              >
                                {t('hoteldetails.rates.bundles.roomPrice')}
                              </Text>
                            </Flex>
                          </Flex>
                        }
                        isChecked={isRateChecked && isClassChecked}
                        value={`${roomClassCode}${rate.ratePlanCode}`}
                        key={`${roomClassCode}${rate.ratePlanCode}`}
                        isClassChecked={isClassChecked}
                      >
                        <>
                          <Flex
                            direction="column"
                            gap="md"
                            backgroundColor="white"
                            p="md"
                            data-testid="Soft-Bundle-Rate-Info-List"
                          >
                            {rate &&
                              rateOptions[rate.ratePlanCode as RateCode].map((label) => (
                                <Flex
                                  display="inline-flex"
                                  alignItems="flex-start"
                                  data-testid="Soft-Bundle-Rate-Info-Item"
                                >
                                  {label.isCheck ? (
                                    <Icon
                                      svg={<Tick color="var(--chakra-colors-primary)" />}
                                      w="16px"
                                      mr="10px"
                                      mt="7px"
                                    />
                                  ) : (
                                    <Icon
                                      svg={<Error color="var(--chakra-colors-darkGrey2)" />}
                                      mr="10px"
                                      mt="3px"
                                    />
                                  )}
                                  {label[language as Language]}
                                </Flex>
                              ))}
                          </Flex>
                          {(isRateVariant || (isRoomOnlyVariant && !isRateChecked)) &&
                            softBundles?.softBundleContent && (
                              <Flex
                                {...bundleExtraBox}
                                data-testid="Soft-Bundle-Rate-Extras-Container"
                              >
                                <Flex
                                  marginBottom="1rem"
                                  data-testid="Soft-Bundle-Rate-Extras-Title-Container"
                                >
                                  <Text
                                    fontSize="sm"
                                    fontWeight="semibold"
                                    data-testid="Soft-Bundle-Rate-Extras-Title"
                                  >
                                    {softBundles?.isOptional ? 'Add on' : 'Included extras'}
                                  </Text>
                                  <Icon
                                    svg={<Info />}
                                    onClick={(e) => handleMealInfoClick(e, softBundles)}
                                    mx="xs"
                                    display="flex"
                                    alignItems="center"
                                    cursor="pointer"
                                  />
                                </Flex>

                                {softBundles?.isOptional ? (
                                  <BundleExtra
                                    key={`Bundle-${rate.ratePlanCode}`}
                                    isDisabled={
                                      roomClassCode !== activeRoomClass ||
                                      rate.ratePlanCode !== activeRate
                                    }
                                    isActive={
                                      rate.ratePlanCode === activeRate &&
                                      roomClassCode === activeRoomClass &&
                                      isNewBundleContainedInActiveBundle(softBundles, activeBundle)
                                    }
                                    bundle={softBundles}
                                    onClick={handleBundleClick}
                                    testId={rate.ratePlanCode}
                                    isClassVariant={false}
                                    currencySymbol={extraCurrencySymbol}
                                    language={language}
                                    nights={nights}
                                    adultsNumber={adultsNumber}
                                  />
                                ) : (
                                  softBundles?.softBundleContent.map(
                                    (bundleContent: SoftBundle) => {
                                      const bundleText =
                                        bundleContent?.name ?? bundleContent?.description ?? '';
                                      return (
                                        <Flex
                                          key={bundleContent.id}
                                          display="inline-flex"
                                          alignItems="flex-start"
                                          data-testid="Soft-Bundle-Rate-Extra-Item"
                                        >
                                          {bundleContent.strikeThrough ? (
                                            <Text
                                              textDecoration="line-through"
                                              data-testid="Soft-Bundle-Rate-Extra-Item-Strike-Through"
                                            >
                                              {bundleText}
                                            </Text>
                                          ) : (
                                            <>
                                              <Icon
                                                svg={<Tick color="var(--chakra-colors-primary)" />}
                                                mr="10px"
                                                height="1.5rem"
                                                display="flex"
                                                alignItems="center"
                                              />
                                              <Text data-testid="Soft-Bundle-Rate-Extra-Item-Included">
                                                {bundleText}
                                              </Text>
                                            </>
                                          )}
                                        </Flex>
                                      );
                                    }
                                  )
                                )}
                              </Flex>
                            )}
                          {isRoomOnlyVariant &&
                            isClassChecked &&
                            isRateChecked &&
                            softBundles?.softBundleContent && (
                              <Flex
                                {...bundleExtraBox}
                                data-testid="Soft-Bundle-Rate-Extras-Container"
                              >
                                <Flex
                                  marginBottom="1rem"
                                  data-testid="Soft-Bundle-Rate-Extras-Title-Container"
                                >
                                  <Text
                                    fontSize="sm"
                                    fontWeight="bold"
                                    data-testid="Soft-Bundle-Rate-Extras-Title"
                                  >
                                    {t('upsell.extras.heading')}?
                                  </Text>
                                </Flex>
                                <RadioGroup
                                  value={isRoomOnly?.toString()}
                                  onChange={(option) => {
                                    setActiveChoice((prev: ActiveChoiceType) => ({
                                      ...prev,
                                      softBundle: undefined,
                                      isRoomOnly: option === 'true',
                                    }));
                                  }}
                                  data-testid="Soft-Bundle-Room-Only-Group"
                                >
                                  <Box mb="sm">
                                    <RadioButton
                                      data-testid="Soft-Bundle-Room-Only-Option-False"
                                      value="false"
                                      alignItems="center"
                                      isChecked={isRoomOnly === false}
                                      backgroundColorChecked="#faf6fb"
                                      borderColorChecked="tertiary"
                                    >
                                      <Flex gap="sm">
                                        <Box>
                                          <Badge
                                            ml="-1.563rem"
                                            fontSize="xxs"
                                            backgroundColor="darkPurple"
                                          >
                                            {t('hdp.roomOnly.bestValue')}
                                          </Badge>
                                          <Text
                                            fontSize="md"
                                            mt="sm"
                                            mb="sm"
                                            fontWeight="bold"
                                            lineHeight={3}
                                          >
                                            {t('hdp.roomOnly.recommended')}
                                          </Text>
                                          {softBundles?.softBundleContent.map(
                                            (bundleContent: SoftBundle) => {
                                              const bundleText =
                                                bundleContent?.name ??
                                                bundleContent?.description ??
                                                '';
                                              return (
                                                <Flex
                                                  key={bundleContent.id}
                                                  display="inline-flex"
                                                  alignItems="flex-start"
                                                  data-testid="Soft-Bundle-Rate-Extra-Item"
                                                >
                                                  {bundleContent.strikeThrough ? (
                                                    <Text
                                                      textDecoration="line-through"
                                                      data-testid="Soft-Bundle-Rate-Extra-Item-Strike-Through"
                                                    >
                                                      {bundleText}
                                                    </Text>
                                                  ) : (
                                                    <>
                                                      <Icon
                                                        svg={
                                                          <Tick color="var(--chakra-colors-primary)" />
                                                        }
                                                        mr="10px"
                                                        height="1.5rem"
                                                        display="flex"
                                                        alignItems="center"
                                                      />
                                                      <Text data-testid="Soft-Bundle-Rate-Extra-Item-Included">
                                                        {bundleText}
                                                      </Text>
                                                    </>
                                                  )}
                                                </Flex>
                                              );
                                            }
                                          )}
                                          <Text color="primary" fontWeight="bold" fontSize="xxs">
                                            {t('hdp.roomOnly.included')}
                                          </Text>
                                        </Box>
                                        <Flex flexDirection="column" justifyContent="center">
                                          <Text
                                            fontWeight="bold"
                                            fontSize="2xl"
                                            data-testid="Soft-Bundle-Rate-TotalReservationAmount"
                                          >
                                            {formatRatePrice(
                                              totalReservationAmount,
                                              rateCurrency,
                                              language
                                            )}
                                          </Text>
                                          <Text
                                            fontSize="xxs"
                                            data-testid="Soft-Bundle-Rate-TotalRoomBundleAmount"
                                          >
                                            +
                                            {formatPrice(
                                              formatCurrency(rateCurrency),
                                              totalRoomBundleAmount,
                                              language
                                            )}{' '}
                                            total
                                          </Text>
                                        </Flex>
                                      </Flex>
                                    </RadioButton>
                                  </Box>
                                  <RadioButton
                                    data-testid="Soft-Bundle-Room-Only-Option-True"
                                    value="true"
                                    alignItems="center"
                                    backgroundColorChecked="#faf6fb"
                                    borderColorChecked="tertiary"
                                    isChecked={isRoomOnly === true}
                                  >
                                    <>
                                      <Flex gap="sm">
                                        <Box>
                                          <Text
                                            fontSize="md"
                                            mb="sm"
                                            fontWeight="bold"
                                            lineHeight={3}
                                          >
                                            {t('hdp.roomOnly.roomOnly')}
                                          </Text>
                                          <Text fontSize="sm" color="darkGrey2">
                                            {t('hdp.roomOnly.addExtrasLater')}
                                          </Text>
                                        </Box>
                                        <Flex flexDirection="column" justifyContent="center">
                                          <Text fontWeight="bold" fontSize="2xl">
                                            {formatRatePrice(
                                              totalRoomNetAmount,
                                              rateCurrency,
                                              language
                                            )}
                                          </Text>
                                        </Flex>
                                      </Flex>
                                      <Text
                                        fontSize="sm"
                                        color="darkGrey2"
                                        mt="md"
                                        data-testid="Soft-Bundle-Rate-RoomOnly-AddExtras"
                                      >
                                        {t('hdp.roomOnly.addExtras')
                                          .replace(
                                            '[extras]',
                                            softBundles.softBundleContent
                                              .map((content) => content.name)
                                              .join(' & ')
                                          )
                                          .replace(
                                            '[price]',
                                            formatPrice(
                                              formatCurrency(rateCurrency),
                                              totalRoomBundleAmount,
                                              language
                                            )
                                          )}
                                      </Text>
                                    </>
                                  </RadioButton>
                                </RadioGroup>
                              </Flex>
                            )}
                        </>
                      </RadioCard>
                    );
                  })}
                </HStack>
                {isLessThanMd && (
                  <HStack
                    justify="center"
                    spacing={2}
                    mt={4}
                    data-testid={formatDataTestId(baseDataTestId, 'pagination')}
                  >
                    {roomRatesWithRateInformation.map((rate) => {
                      return (
                        <Box
                          data-testid={formatDataTestId(baseDataTestId, 'pagination-dot')}
                          as="button"
                          key={rate.ratePlanCode}
                          w="8px"
                          h="8px"
                          borderRadius="full"
                          bg={rate.ratePlanCode === activeRate ? 'gray.600' : 'gray.300'}
                          onClick={(e) => handleRateClick(rate, roomClassCode, e)}
                          transition="background-color 0.2s ease"
                          _hover={{
                            bg: rate.ratePlanCode === activeRate ? 'gray.700' : 'gray.400',
                          }}
                        />
                      );
                    })}
                  </HStack>
                )}
              </Flex>
              {isClassVariant && rateSoftBundle?.softBundleContent && (
                <BundleExtra
                  key={rateSoftBundle.softBundleContent.map((content) => content.id).join('')}
                  isDisabled={!rateSoftBundle.isOptional || roomClassCode !== activeRoomClass}
                  isActive={
                    roomClassCode === activeRoomClass &&
                    isNewBundleContainedInActiveBundle(rateSoftBundle, activeBundle)
                  }
                  bundle={rateSoftBundle}
                  onClick={handleBundleClick}
                  testId={rateSoftBundle.softBundleContent.map((content) => content.id).join('')}
                  currencySymbol={extraCurrencySymbol}
                  language={language}
                  nights={nights}
                  adultsNumber={adultsNumber}
                />
              )}
            </Box>
          );
        })}
      </Flex>
      {(isRateVariant || isRoomOnlyVariant) && (
        <BundleSideDrawer
          visible={isBundleSideDrawerVisible}
          onClose={() => setIsBundleSideDrawerVisible(false)}
          currency={currency}
          language={language}
          numberOfNights={nights}
          packages={bundleSideDrawerPacks}
          adultsNumber={adultsNumber}
        />
      )}
      {isRoomDetailsDrawerEnabled && (
        <RoomSideDrawer
          visible={showRoomDrawer}
          onClose={() => setShowRoomDrawer(false)}
          title={roomDrawerDetails.roomBaseName}
          isPremierPlus={roomDrawerDetails.isPremierPlus}
          roomStaticDetails={roomDrawerDetails.matchingTabItem}
          brand={brand}
        />
      )}
    </Flex>
  );
}

const bundleExtraBox = {
  direction: 'column',
  borderTop: '1px solid var(--chakra-colors-lightGrey3)',
  p: 'md',
  height: '-webkit-fill-available',
} as FlexProps;

const wrapperStyle = {
  direction: 'column',
  gap: 'md',
} as FlexProps;

const imageContainerStyle = {
  boxSizing: 'border-box',
  position: 'relative',
  ml: { mobile: 'md', md: '0' },
  w: { mobile: '7.75rem', md: '11.125rem' },
  h: { mobile: '4.375rem', md: '6.25rem' },
} as FlexProps;

const textContainerStyle = {
  direction: 'column',
  justifyContent: 'center',
  p: {
    mobile: '0',
    md: 'var(--chakra-space-xs) 0.563rem var(--chakra-space-xs) 1.25rem',
  },
  flex: 1,
} as FlexProps;

const roomClassTextStyle = {
  fontWeight: 'bold',
  fontSize: { mobile: '0.938rem', sm: 'lg' },
  color: 'tertiary',
  textDecoration: 'underline',
};

const descriptionTextStyle = {
  fontSize: 'sm',
  lineHeight: '140%',
  color: 'darkGrey2',
  mt: { mobile: 'sm', md: 'xmd' },
};

const ratesListStyle = {
  gap: '1rem',
  width: 'full',
  overflowX: 'auto' as const,
  position: 'relative' as const,
  direction: 'row' as const,
  alignItems: 'stretch',
};

const linkStyles = {
  fontSize: 'sm',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};
