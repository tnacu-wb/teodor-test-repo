import { Box, BoxProps, Button, Flex, FlexProps, Image, Text } from '@chakra-ui/react';
import { PromotionPanel } from '@whitbread-eos/api';
import { formatAssetsUrl, renderSanitizedHtml } from '@whitbread-eos/utils';
import React from 'react';

interface PromotionPanelData {
  promotionPanel: PromotionPanel[];
}
export interface Props {
  data: PromotionPanelData;
  routerPush: any;
}

export default function ConfirmationPromotion({ data, routerPush }: Readonly<Props>) {
  if (!data.promotionPanel || data.promotionPanel.length === 0) {
    return null;
  }

  const { image, name, description, linkLabel, linkPath } = data.promotionPanel[0];

  return (
    <Flex {...promoWrapperStyle}>
      {image && (
        <Box {...imageWrapperStyle} data-testid={'promo-image'}>
          <Image
            src={formatAssetsUrl(image)}
            alt="Premier Inn Promo-Image"
            fit="fill"
            objectFit="cover"
          />
        </Box>
      )}
      <Box p="lg">
        <Flex direction="column">
          <Text {...promoTitleStyle} data-testid={'promo-title'}>
            {name}
          </Text>
          <Box {...promoDescriptionStyle} data-testid={'promo-description'}>
            {renderSanitizedHtml(description)}
          </Box>
          <Button
            {...promoButtonStyle}
            data-testid={'promo-link'}
            onClick={() => {
              routerPush(linkPath);
            }}
          >
            {linkLabel}
          </Button>
        </Flex>
      </Box>
    </Flex>
  );
}

const promoWrapperStyle = {
  direction: 'column',
  w: 'full',
  bgColor: 'lightPurple',
} as FlexProps;

const imageWrapperStyle = {
  pos: 'relative',
  w: '100%',
  h: '14.75rem',
} as BoxProps;

const promoTitleStyle = {
  fontSize: 'md',
  color: 'baseWhite',
  fontWeight: 'medium',
};

const promoDescriptionStyle = {
  fontSize: 'sm',
  color: 'baseWhite',
  fontWeight: 'normal',
};

const promoButtonStyle = {
  size: 'full',
  variant: 'tertiary',
  mt: 'lg',
};
