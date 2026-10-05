import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Text } from '@chakra-ui/react';
import {
  SelectedExtrasPackage,
  ExtrasItem,
  FT_PI_FREE_FNB_AND_EXTRAS,
  FT_BB_FREE_FNB_AND_EXTRAS,
  FT_CCUI_FREE_FNB_AND_EXTRAS,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  VALID_EXTRAS_IDS,
} from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import { ExtrasItemComponent } from '@whitbread-eos/molecules';
import {
  extrasNamingCheck,
  formatDataTestId,
  renderSanitizedHtml,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  extrasDetailsList: ExtrasItem[] | undefined;
  selectedRoom: number;
  selectedExtrasList: SelectedExtrasPackage[] | undefined;
  handleSelectedExtrasList?: (updatedExtrasItemsList: SelectedExtrasPackage[] | undefined) => void;
  noNights: number;
  allRooms?: boolean;
  isAmend?: boolean;
}

export default function ExtrasSection({
  extrasDetailsList,
  selectedRoom,
  selectedExtrasList,
  handleSelectedExtrasList,
  noNights,
  allRooms,
  isAmend = false,
}: Readonly<Props>) {
  const {
    [FT_PI_FREE_FNB_AND_EXTRAS]: isPiFreeFnbAndExtrasEnabled,
    [FT_BB_FREE_FNB_AND_EXTRAS]: isBbFreeFnbAndExtrasEnabled,
    [FT_CCUI_FREE_FNB_AND_EXTRAS]: isCcuiFreeFnbAndExtrasEnabled,
  } = useFeatureToggle();

  const isFreeFnbAndExtrasEnabled =
    isPiFreeFnbAndExtrasEnabled || isBbFreeFnbAndExtrasEnabled || isCcuiFreeFnbAndExtrasEnabled;

  const baseDataTestId = `ExtrasSection${allRooms ? '-allRooms' : ''}`;
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();

  const hasFreeExtras = extrasDetailsList?.some((item) => item?.isFree);

  const extrasDetailsListSorted = extrasDetailsList?.sort(
    (firstItem: ExtrasItem, secondItem: ExtrasItem) => {
      if (hasFreeExtras && firstItem?.isFree !== secondItem?.isFree) {
        return firstItem?.isFree ? -1 : 1;
      }

      return (firstItem?.order ?? 0) - (secondItem?.order ?? 0);
    }
  );

  const isExtraItemFree = isFreeFnbAndExtrasEnabled
    ? extrasDetailsListSorted?.some(
        (extrasItem) => (extrasItem as { isFree?: boolean })?.isFree ?? false
      )
    : false;

  const countSelectedExtras = (
    extrasList: SelectedExtrasPackage[] | undefined,
    extrasIds: readonly string[]
  ) => {
    let count = 0;

    extrasList?.forEach((room) => {
      if (room?.packagesList?.some((id) => extrasIds.includes(id))) {
        count++;
      }
    });

    return count;
  };

  const numberOfRooms = selectedExtrasList?.length;

  const sumPreviousEciSelection = selectedExtrasList?.reduce(
    (sum: number, item: SelectedExtrasPackage) => sum + (item?.previousEciSelection as number),
    0
  );
  const sumPreviousLcoSelection = selectedExtrasList?.reduce(
    (sum: number, item: SelectedExtrasPackage) => sum + (item?.previousLcoSelection as number),
    0
  );
  // Total count for selected extras
  const totalInventoryEarlyCheckIn = countSelectedExtras(selectedExtrasList, EARLY_CHECKIN_IDS);

  const totalInventoryLateCheckOut = countSelectedExtras(selectedExtrasList, LATE_CHECKOUT_IDS);

  // Check inventory availability function
  const checkInventoryAvailability = (extrasItemParam: ExtrasItem, allRooms?: boolean) => {
    const { available, id } = extrasItemParam;

    const eciAvailibility =
      allRooms && numberOfRooms
        ? (available as number) + (sumPreviousEciSelection as number) >= numberOfRooms
        : (available as number) + (sumPreviousEciSelection as number) >
          (totalInventoryEarlyCheckIn as number);

    const lcoAvailibility =
      allRooms && numberOfRooms
        ? (available as number) + (sumPreviousLcoSelection as number) >= numberOfRooms
        : (available as number) + (sumPreviousLcoSelection as number) >
          (totalInventoryLateCheckOut as number);

    if (EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number])) {
      return eciAvailibility;
    }

    if (LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])) {
      return lcoAvailibility;
    }

    return available === null;
  };

  const checkSelectedInventory = (id?: string) => {
    if (id && EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number])) {
      return totalInventoryEarlyCheckIn;
    }

    if (id && LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])) {
      return totalInventoryLateCheckOut;
    }

    return 1;
  };

  const allRoomsAvailibility = extrasDetailsListSorted?.some((extrasItem) => {
    if (extrasItem?.available && numberOfRooms) {
      if (
        extrasItem?.id &&
        EARLY_CHECKIN_IDS.includes(extrasItem.id as (typeof EARLY_CHECKIN_IDS)[number])
      ) {
        return (
          (extrasItem?.available as number) + (sumPreviousEciSelection as number) >= numberOfRooms
        );
      }

      if (
        extrasItem?.id &&
        LATE_CHECKOUT_IDS.includes(extrasItem.id as (typeof LATE_CHECKOUT_IDS)[number])
      ) {
        return (
          (extrasItem?.available as number) + (sumPreviousLcoSelection as number) >= numberOfRooms
        );
      }
    }
  });

  const oneExtrasNotAvailable = extrasDetailsListSorted?.some(
    (extrasItem) =>
      (extrasItem?.id &&
        EARLY_CHECKIN_IDS.includes(extrasItem.id as (typeof EARLY_CHECKIN_IDS)[number]) &&
        (extrasItem?.available as number) + (sumPreviousEciSelection as number) < 1) ||
      (extrasItem?.id &&
        LATE_CHECKOUT_IDS.includes(extrasItem.id as (typeof LATE_CHECKOUT_IDS)[number]) &&
        (extrasItem?.available as number) + (sumPreviousLcoSelection as number) < 1)
  );

  const getExtrasNotificationLabel = (extrasItem: ExtrasItem) => {
    const { available, id } = extrasItem;
    let totalAvailable = available;

    if (id && EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number])) {
      totalAvailable = (totalAvailable as number) + (sumPreviousEciSelection as number);
    }

    if (id && LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])) {
      totalAvailable = (totalAvailable as number) + (sumPreviousLcoSelection as number);
    }

    const extrasLabelsCheck = `${extrasNamingCheck(t, id)}`;

    const minRoomsExtrasLabel = renderSanitizedHtml(
      t('ancillaries.extras.notification.min')
        .replace('[packageName]', `${extrasLabelsCheck}`)
        .replace('[numberOfPackages]', `${totalAvailable}`)
    );

    const minAllRoomsExtrasLabel = renderSanitizedHtml(
      t('ancillaries.extras.notification.allRooms')
        .replace('[packageName]', `${extrasLabelsCheck}`)
        .replace('[numberOfPackages]', `${totalAvailable}`)
    );

    const maxRoomsExtrasLabel = renderSanitizedHtml(
      t('ancillaries.extras.notification.max')
        .replace('[packageName]', `${extrasLabelsCheck}`)
        .replace('[packageName]', `${extrasLabelsCheck}`.toLocaleLowerCase())
        .replace('[numberOfPackages]', `${totalAvailable}`)
    );

    const displayAllRoomsNotification =
      totalAvailable &&
      numberOfRooms &&
      allRooms &&
      totalAvailable < numberOfRooms &&
      (allRoomsAvailibility || oneExtrasNotAvailable);

    const higherNumberOfRooms =
      totalAvailable && numberOfRooms && totalAvailable < numberOfRooms && !allRooms;

    if (totalAvailable && selectedExtrasList && numberOfRooms) {
      if (totalAvailable === checkSelectedInventory(id)) {
        if (numberOfRooms === 1) {
          return '';
        } else if (higherNumberOfRooms) {
          return maxRoomsExtrasLabel;
        }
      } else if (higherNumberOfRooms) {
        return minRoomsExtrasLabel;
      } else if (displayAllRoomsNotification) {
        return minAllRoomsExtrasLabel;
      }
    }
  };

  const getAllRoomsUnavailableNotification = (
    extrasDetailsListSorted: ExtrasItem[] | undefined
  ) => {
    const extrasAvailable = { eci: 0, lco: 0 };

    extrasDetailsListSorted?.forEach((extrasItem) => {
      const { available, id } = extrasItem;

      if (available || sumPreviousEciSelection || sumPreviousLcoSelection) {
        if (id && EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number])) {
          extrasAvailable.eci = (available as number) + (sumPreviousEciSelection as number);
        } else if (id && LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])) {
          extrasAvailable.lco = (available as number) + (sumPreviousLcoSelection as number);
        }
      }
    });

    const unavailableAllRoomsExtrasLabel = renderSanitizedHtml(
      t('ancillaries.extras.notification.packages.allRooms')
        .replace('[numberOfPackages]', `${extrasAvailable.eci}`)
        .replace('[numberOfPackages]', `${extrasAvailable.lco}`)
    );

    const isAllRoomsNotificationAvailable =
      extrasAvailable.eci > 0 &&
      extrasAvailable.lco > 0 &&
      ((numberOfRooms as number) > extrasAvailable.eci ||
        (numberOfRooms as number) > extrasAvailable.lco);

    return (
      isAllRoomsNotificationAvailable && (
        <Notification
          maxWidth="full"
          variant="info"
          status="info"
          title={''}
          description={unavailableAllRoomsExtrasLabel as string}
          svg={<Info />}
          wrapperStyles={{ mt: 'xl', mb: 'xl' }}
        />
      )
    );
  };

  return (
    <Box {...wrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      {!isAmend && (
        <Text
          {...extrasTitleLayoutStyle}
          {...getTypographyProps(extrasTitleLegacyTypography, extrasTitleSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'Heading-Title')}
        >
          {allRooms ? t('ancillaries.extras.allRooms.title') : t('upsell.extras.heading')}
        </Text>
      )}

      {!allRoomsAvailibility &&
        allRooms &&
        getAllRoomsUnavailableNotification(extrasDetailsListSorted)}

      {extrasDetailsListSorted?.map((extrasItem: ExtrasItem) => {
        const isRemovable = selectedExtrasList?.[selectedRoom]?.packagesList?.includes(
          extrasItem?.id as string
        ) as boolean;

        const isExtrasAvailablePerItem =
          (extrasItem?.available as number) > 0 ||
          extrasItem?.available === null ||
          (sumPreviousLcoSelection as number) > 0 ||
          (sumPreviousEciSelection as number) > 0;

        const displayExtra = isAmend
          ? isRemovable &&
            VALID_EXTRAS_IDS.includes(extrasItem.id as (typeof VALID_EXTRAS_IDS)[number])
          : true;

        const isExtraitemFree = isExtraItemFree ? extrasItem?.isFree : false;

        return (
          <>
            {isExtrasAvailablePerItem && displayExtra && (
              <>
                <Notification
                  maxWidth="full"
                  variant="info"
                  status="info"
                  title={''}
                  description={getExtrasNotificationLabel(extrasItem) as string}
                  svg={<Info />}
                  wrapperStyles={{ mt: 'xl', mb: 'xl' }}
                />

                <ExtrasItemComponent
                  {...extrasItem}
                  key={`Room-${selectedRoom}-${extrasItem.id}`}
                  isRemovable={isRemovable}
                  isAvailable={checkInventoryAvailability(extrasItem, allRooms)}
                  selectedRoom={selectedRoom}
                  selectedExtrasList={selectedExtrasList}
                  handleSelectedExtrasList={handleSelectedExtrasList}
                  noNights={noNights}
                  price={extrasItem?.price}
                  allRooms={allRooms}
                  numberOfRooms={numberOfRooms}
                  isFree={isExtraitemFree}
                />
              </>
            )}
          </>
        );
      })}
    </Box>
  );
}

const extrasTitleLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const extrasTitleLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { mobile: 'xl', sm: '2xl' },
  lineHeight: { mobile: '3', sm: '4' },
} as TextProps;

const extrasTitleSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const wrapperStyle = {
  flexDirection: 'column',
  alignItems: 'flex-start',
  gap: 2,
} as FlexProps;
