import { Box, BoxProps, Checkbox, Text, TextProps, VStack } from '@chakra-ui/react';
import styled from '@emotion/styled';
import { RateClassification } from '@whitbread-eos/api';
import { ModalVariants } from '@whitbread-eos/atoms';
import { useQueryRequest } from '@whitbread-eos/utils';
import { gql } from 'graphql-request';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

export interface Props {
  isRoomRatePoliciesChecked: boolean;
  onChange: () => void;
  language: string;
  country: string;
  hotelId: string;
  hotelBrand: string;
}

export const GET_ROOM_RATE_POLICIES = gql`
  query getRatesInformation(
    $hotelBrand: String!
    $hotelId: String!
    $language: String!
    $country: String!
  ) {
    ratesInformation(
      brand: $hotelBrand
      hotelId: $hotelId
      language: $language
      country: $country
    ) {
      rateClassifications {
        rateClassification
        rateOrder
        rateNotes
        rateName
        rateLongDescription
        rateDescription
      }
    }
  }
`;

export default function RoomRatePolicies({
  isRoomRatePoliciesChecked,
  onChange,
  language,
  country,
  hotelId,
  hotelBrand,
}: Readonly<Props>) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [stateModal, setStateModal] = useState(false);
  const { t } = useTranslation(['common']);
  const { data } = useQueryRequest('ratesInformation', GET_ROOM_RATE_POLICIES, {
    hotelBrand,
    hotelId,
    language,
    country,
  });

  return (
    <Box data-testid="roomRatePolicies_Component">
      <Box {...checkboxStyle}>
        <CustomCheckbox
          isChecked={isRoomRatePoliciesChecked}
          onChange={showModal}
          data-testid="roomRatePolicies_checkbox"
        />
        <Text noOfLines={1} {...checkboxTextStyle} data-testid="roomRatePolicies_checkbox-text">
          {t('ccui.payment.confirmBooking.textPolicies.label1')}
          <Text
            as="u"
            onClick={() => setIsModalOpen(true)}
            data-testid="roomRatePolicies_launchButton"
            {...roomratePoliciesLink}
            px="0.5ch"
          >
            {t('ccui.payment.confirmBooking.textPolicies.label2')}
          </Text>
          {t('ccui.payment.confirmBooking.textPolicies.label3')}
        </Text>
      </Box>
      <ModalVariants
        onClose={() => setIsModalOpen(!isModalOpen)}
        isOpen={isModalOpen}
        variant="info"
        variantProps={{
          title: t('ccui.roomRate.title'),

          delimiter: true,
        }}
        dataTestId="roomRatePolicies"
      >
        <VStack {...modalStyles}>
          <Box px={6} py="xl">
            <Text as="b" color="darkGrey1" data-testid="roomRatePolicies_flex-title">
              {getRoomRateLabels('flexrate')?.rateName}
            </Text>
            <Text pb="md" {...description} data-testid="roomRatePolicies_flex-description">
              {getRoomRateLabels('flexrate')?.rateDescription}
            </Text>
            <Text as="b" color="darkGrey1" data-testid="roomRatePolicies_semiflex-title">
              {getRoomRateLabels('semiflex')?.rateName}
            </Text>
            <Text pb="md" {...description} data-testid="roomRatePolicies_semiflex-description">
              {getRoomRateLabels('semiflex')?.rateDescription}
            </Text>
            <Text as="b" color="darkGrey1" data-testid="roomRatePolicies_advance-title">
              {getRoomRateLabels('advance')?.rateName}
            </Text>
            <Text pb="md" {...description} data-testid="roomRatePolicies_advance-description">
              {getRoomRateLabels('advance')?.rateDescription}
            </Text>
            <Text as="b" color="darkGrey1" data-testid="roomRatePolicies_standard-title">
              {getRoomRateLabels('standard')?.rateName}
            </Text>
            <Text pb="md" {...description} data-testid="roomRatePolicies_standard-description">
              {getRoomRateLabels('standard')?.rateDescription}
            </Text>
            <Text as="b" color="darkGrey1" data-testid="roomRatePolicies_nonflex-title">
              {getRoomRateLabels('nonflex')?.rateName}
            </Text>
            <Text pb="md" {...description} data-testid="roomRatePolicies_nonflex-description">
              {getRoomRateLabels('nonflex')?.rateDescription}
            </Text>
          </Box>
        </VStack>
      </ModalVariants>
    </Box>
  );

  function showModal() {
    if (!stateModal && !isRoomRatePoliciesChecked) {
      setStateModal(true);
      setIsModalOpen(true);
    }
    onChange();
  }

  function getRoomRateLabels(rateType: string) {
    return data?.ratesInformation?.rateClassifications.find(
      (rate: RateClassification) => rate.rateClassification.toLowerCase() === rateType
    );
  }
}

const CustomCheckbox = styled(Checkbox)`
  span.chakra-checkbox__control {
    margin: 0;
    width: 1.25rem;
    height: 1.25rem;
  }
`;

const description = {
  color: 'darkGrey2',
  fontSize: 'sm',
  lineHeight: 2,
} as TextProps;

const modalStyles = {
  width: '30.375rem',
};
const checkboxStyle = {
  display: 'flex',
  flexDirection: 'row',
  overflow: 'hidden',
} as BoxProps;

const checkboxTextStyle = {
  display: 'inline-flex',
  marginLeft: '0.6rem',
  color: 'darkGrey1',
} as BoxProps;

const roomratePoliciesLink = {
  cursor: 'pointer',
  color: 'lightPurple',
};
