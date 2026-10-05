import { Box, Divider, Flex, Heading, HeadingProps, Text, TextProps } from '@chakra-ui/react';
import type { ButtonProps } from '@whitbread-eos/atoms';
import { Button, ModalVariants } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';

import type { BookingInformationType } from './BookingInformation.component';
import BookingInformation from './BookingInformation.component';
import type { DpaStatusType } from './DpaStatus.component';
import DpaStatus from './DpaStatus.component';
import type { PersonalInformationType } from './PersonalInformation.component';
import PersonalInformation from './PersonalInformation.component';

export type IDVDataProps = {
  personalInformation: PersonalInformationType;
  bookingInformation: BookingInformationType;
  dpaStatus: DpaStatusType;
};

export interface Props {
  data: IDVDataProps;
  setData: (value: IDVDataProps) => void;
  isVisible: boolean;
  onClose: (dpaPassed: boolean, dpaOverride: boolean) => void;
  onReuseDetails?: () => void;
  t: (id: string) => string;
  inputValues: any;
}

export default function IDVModal({
  data,
  setData,
  isVisible,
  onClose,
  onReuseDetails,
  t,
  inputValues,
}: Readonly<Props>) {
  const baseDataTestId = 'IDVModal';

  const handleDpaPassed = (dpaPassedParam: boolean) => {
    setData({
      ...data,
      dpaStatus: {
        ...data.dpaStatus,
        dpaPassed: dpaPassedParam,
        dpaOverride: false,
      },
    });
  };

  const handleDpaOverride = (dpaOverrideParam: boolean) => {
    setData({
      ...data,
      dpaStatus: {
        ...data.dpaStatus,
        dpaOverride: dpaOverrideParam,
        dpaPassed: false,
      },
    });
  };

  const handleModalClose = () => {
    onClose(data.dpaStatus.dpaPassed, data.dpaStatus.dpaOverride);
  };

  return (
    <ModalVariants
      onClose={handleModalClose}
      variant="gallery"
      isOpen={isVisible}
      variantProps={{ title: '', delimiter: false }}
    >
      <Box data-testid={baseDataTestId} mb="1.5rem" ml="1.5rem" mr="1.5rem">
        <Divider />
        <Flex justifyContent="center">
          <Heading {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'ModalTitle')}>
            {t('ccui.idv.heading')}
          </Heading>
        </Flex>
        <Flex>
          <PersonalInformation
            personalInformation={data.personalInformation}
            t={t}
            inputValues={inputValues}
            baseDataTestId={formatDataTestId(baseDataTestId, 'PersonalInformation')}
          />
          <BookingInformation
            bookingInformation={data.bookingInformation}
            t={t}
            inputValues={inputValues}
            baseDataTestId={formatDataTestId(baseDataTestId, 'BookingInfo')}
          />
          <DpaStatus
            dpaStatus={data.dpaStatus}
            onDpaPassed={handleDpaPassed}
            onDpaOverride={handleDpaOverride}
            onReuseDetails={onReuseDetails}
            t={t}
            baseDataTestId={formatDataTestId(baseDataTestId, 'DpaStatus')}
          />
        </Flex>
        <Flex mt="2rem" justifyContent="flex-end">
          <Button
            data-testid={formatDataTestId(baseDataTestId, 'CloseButton')}
            {...closeButtonStyle}
            onClick={handleModalClose}
          >
            <Text {...btnTextStyle}>{t('ccui.idv.closeButton')}</Text>
          </Button>
        </Flex>
      </Box>
    </ModalVariants>
  );
}

const titleStyle = {
  color: 'darkGrey1',
  fontSize: '1.625rem',
  lineHeight: '2rem',
  fontWeight: '600',
  mt: '1.5rem',
  mb: '2.5rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as HeadingProps;

const closeButtonStyle = {
  size: 'sm',
  variant: 'tertiary',
  w: { mobile: 'full', xs: 'full', sm: 'full', md: 'full', lg: '311px', xl: '333px' },
  my: '1rem',
} as unknown as ButtonProps;

const btnTextStyle = {
  lineHeight: '1.5rem',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
  fontWeight: '600',
  fontSize: '1.125rem',
} as TextProps;
