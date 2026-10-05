import { Box, BoxProps, Flex, FlexProps, StyleProps, Text } from '@chakra-ui/react';
import {
  HotelDistance,
  HOTELS_BY_LOCATION,
  HotelsSearchCriteria,
  SBHotelDetails,
  SearchBrandType,
} from '@whitbread-eos/api';
import {
  Alert,
  DefaultModalVariantProps,
  Error,
  getLogoByBrand,
  LoadingSpinner,
  ModalVariants,
  Notification,
} from '@whitbread-eos/atoms';
import { useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { UseFormSetValue } from 'react-hook-form';

export interface Props {
  isOpen: boolean;
  onClose: () => void;
  onHotelSelected: () => void;
  paramsForQuery: HotelsSearchCriteria;
  handleSetValue?: UseFormSetValue<{ [key: string]: any }>;
}

const NO_MAX_HOTELS = 10;

export default function HotelDropdownModal({
  isOpen,
  onClose,
  onHotelSelected,
  paramsForQuery,
  handleSetValue,
}: Readonly<Props>) {
  const { t } = useTranslation();

  const modalTitle = t('ccui.manageBooking.hotelsModalTitle');
  const modalNoResults = t('ccui.manageBooking.hotelsModalNoResults');
  const modalError = t('ccui.manageBooking.hotelsModalError');

  const { data, isError, isFetching } = useQueryRequest(
    [
      'hotelsLocations',
      paramsForQuery?.location,
      paramsForQuery?.locationFormat,
      paramsForQuery?.radius,
      paramsForQuery?.radiusUnit,
    ],
    HOTELS_BY_LOCATION,
    { ...paramsForQuery },
    { enabled: isOpen },
    undefined,
    true
  );

  const handleOnClose = () => {
    onClose();
  };

  const handleHotelSelection = (hotelDetails: SBHotelDetails) => {
    handleSetValue?.('hotelDetails', hotelDetails);
    onHotelSelected();
  };

  const mocalContent = () => {
    if (isFetching) {
      return <LoadingSpinner loadingText={t('searchresults.list.hotel.loading')} />;
    }
    if (isError) {
      return (
        <Notification
          maxWidth="full"
          variant="error"
          status="error"
          title={''}
          description={modalError}
          svg={<Error />}
        />
      );
    }

    const results = data?.hotelsLocations;
    if (!results || results.length <= 0) {
      return (
        <Notification
          maxWidth="full"
          variant="alert"
          status="error"
          title={''}
          description={modalNoResults}
          svg={<Alert />}
        />
      );
    }

    return mapHotels(results).map((result) => {
      const hotelDetails: SBHotelDetails = { name: result?.value ?? '', code: result.code };
      return (
        <Box
          key={result.code}
          data-testid="HotelDropdownModal-ListItem"
          {...itemStyles}
          onClick={() => handleHotelSelection(hotelDetails)}
        >
          {result.component}
          <Text as="span" {...itemValueStyles}>
            {result?.value}
          </Text>
        </Box>
      );
    });
  };

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={handleOnClose}
      dataTestId="HotelDropdownModal-Container"
      variant="default"
      headerStyles={headerStyles}
      overlayStyles={zIndexStyles}
      contentContainerStyles={zIndexStyles}
      variantProps={
        {
          title: modalTitle,
          closeOnOverlayClick: false,
          overflowVisible: false,
          isCentered: true,
          externalTitleStyling: titleStyles,
        } as DefaultModalVariantProps
      }
    >
      <Flex data-testid="HotelDropdownModal-ListContent" {...containerStyles}>
        {mocalContent()}
      </Flex>
    </ModalVariants>
  );

  function mapHotels(items: HotelDistance[]) {
    const configuredItems = items.length > NO_MAX_HOTELS ? items.slice(0, NO_MAX_HOTELS) : items;
    return configuredItems?.map((item) => {
      return {
        value: item.name,
        code: item.hotelId,
        component: getLogoByBrand(item?.brand as SearchBrandType, item?.hotelId),
      };
    });
  }
}

const titleStyles = {
  justifyContent: 'flex-start',
  marginLeft: 0,
} as FlexProps;

const headerStyles = {
  py: 'var(--chakra-space-4)',
} as FlexProps;

const containerStyles = {
  direction: 'column',
  paddingTop: 'var(--chakra-space-6)',
  paddingInline: 'var(--chakra-space-6)',
  height: '27rem',
  width: { mobile: 'full', lg: '24.5rem', xl: '27.75rem' },
} as FlexProps;

const itemStyles = {
  cursor: 'pointer',
  display: 'flex',
  justifyContent: 'flex-start',
  alignItems: 'center',
  paddingY: 'var(--chakra-space-4)',
  paddingInline: 'var(--chakra-space-1)',
  _last: {
    marginBottom: 'var(--chakra-space-4)',
  },

  _hover: {
    bg: 'var(--chakra-colors-lightGrey5)',
  },
} as BoxProps;

const itemValueStyles: BoxProps = {
  display: 'block',
  textOverflow: 'ellipsis',
  overflow: 'hidden',
  whiteSpace: 'nowrap',
  fontWeight: 'medium',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
};

const zIndexStyles: StyleProps = {
  zIndex: 2000,
};
