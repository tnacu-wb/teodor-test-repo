import type { BoxProps, ResponsiveValue } from '@chakra-ui/react';
import { Box, Collapse, Link } from '@chakra-ui/react';
import {
  handleRefHeightChange,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

interface MenuDescriptionProps {
  menuDescription: string;
}

export default function HotelRestaurantMenuDescription({
  menuDescription,
}: Readonly<MenuDescriptionProps>) {
  const [height, setHeight] = useState(0);
  const [isCollapsibleOpen, setIsCollapsibleOpen] = useState(false);
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();

  const linkName = isCollapsibleOpen ? t('hoteldetails.readless') : t('hoteldetails.readmore');

  return (
    <>
      <Box {...menuDescriptionBoxStyles}>
        <Collapse
          startingHeight={height ? Math.min(height, 95) : 95}
          in={isCollapsibleOpen}
          data-testid="menu-description-collapse"
        >
          <Box
            ref={(node) => handleRefHeightChange(node, setHeight)}
            className="formatLinks"
            {...getTypographyProps({}, menuDescriptionSemanticTypography)}
          >
            {renderSanitizedHtml(menuDescription)}
          </Box>
        </Collapse>
        {!isCollapsibleOpen && height >= 95 && <Box {...fadeOutBoxStyles} />}
      </Box>

      {height >= 95 && (
        <Link
          color="btnSecondaryEnabled"
          {...getTypographyProps({}, menuDescriptionLinkSemanticTypography)}
          sx={{
            ':hover': {
              textDecoration: 'none',
            },
          }}
          onClick={() => setIsCollapsibleOpen(!isCollapsibleOpen)}
        >
          {linkName}
        </Link>
      )}
    </>
  );
}

const fadeOutBoxStyles = {
  position: 'absolute' as ResponsiveValue<'absolute'>,
  bottom: 0,
  h: '3.125rem',
  w: 'full',
  background:
    'linear-gradient(to bottom, rgba(255,255,255,0) 0%, var(--chakra-colors-baseWhite) 90%)',
};

const menuDescriptionBoxStyles = {
  position: 'relative',
  color: 'baseBlack',
} as BoxProps;

const menuDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
};

const menuDescriptionLinkSemanticTypography = {
  textStyle: 'link-m-regular',
};
