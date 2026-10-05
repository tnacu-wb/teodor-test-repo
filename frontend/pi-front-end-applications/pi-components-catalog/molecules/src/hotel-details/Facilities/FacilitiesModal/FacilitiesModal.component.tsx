import { HStack, Text, VStack } from '@chakra-ui/react';
import type { FacilityItem } from '@whitbread-eos/api';
import { Icon, List, ModalVariants } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

interface Props {
  facilities: FacilityItem[] | undefined;
  isModalVisible: boolean;
  onModalClose: () => void;
  containerRef?: React.RefObject<HTMLElement>;
}

export default function FacilitiesModal({
  facilities,
  isModalVisible,
  onModalClose,
  containerRef,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const facilityItemTypographyProps = getTypographyProps({}, facilityItemSemanticTypography);
  const facilitiesModalTitleTypographyProps = getTypographyProps(
    facilitiesModalTitleLegacyTypography,
    facilitiesModalTitleSemanticTypography
  );
  const facilitiesModalHeaderStyles = {
    sx: {
      '& [data-testid="facilities-modal-ModalTitle"]': {
        ...facilitiesModalTitleTypographyProps,
      },
    },
  };

  return (
    <ModalVariants
      isOpen={isModalVisible}
      onClose={onModalClose}
      dataTestId="facilities-modal"
      variant="info"
      variantProps={{ title: t('facilities.modal.title'), delimiter: true }}
      headerStyles={facilitiesModalHeaderStyles}
      {...(containerRef && { portalProps: { containerRef } })}
    >
      <VStack {...modalStyles}>
        <List data-testid="facilities-list" {...listStyles}>
          {facilities?.map((facility) => (
            <HStack key={facility?.code} spacing="1.5rem">
              <Icon src={formatAssetsUrl(facility?.icon as string)} w="6" />
              {facility?.description ? (
                <List gap="1">
                  <Text {...facilityItemTypographyProps} data-testid="facility-title">
                    {facility?.name}
                  </Text>
                  <Text
                    {...facilityItemTypographyProps}
                    data-testid="facility-description"
                    color="darkGrey2"
                  >
                    {facility?.description}
                  </Text>
                </List>
              ) : (
                <Text {...facilityItemTypographyProps} data-testid="facility-title">
                  {facility?.name}
                </Text>
              )}
            </HStack>
          ))}
        </List>
      </VStack>
    </ModalVariants>
  );
}

const modalStyles = {
  w: { base: 'calc(100vw - var(--chakra-space-3xl))', md: '30.375rem', xl: '36.875rem' },
  px: 6,
  py: '2xl',
};

const listStyles = {
  w: 'full',
  fontWeight: 500,
  gap: 10,
};

const facilityItemSemanticTypography = {
  textStyle: 'body-m-regular',
};

const facilitiesModalTitleLegacyTypography = {
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
};

const facilitiesModalTitleSemanticTypography = {
  textStyle: 'title-m-regular',
};
