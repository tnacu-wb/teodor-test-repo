import { Box, Collapse, Link, Text, VStack } from '@chakra-ui/react';
import {
  handleRefHeightChange,
  renderSanitizedHtml,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

export default function DirectionsInformation() {
  const { directions: data, isLoading, isError, error } = useStaticHotelInformation();
  const [height, setHeight] = useState(0);
  const [readMore, setReadMore] = useState(false);
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();

  const linkName = readMore ? t('hoteldetails.readless') : t('hoteldetails.readmore');

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }
  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  if (!data) {
    return null;
  }

  return (
    <VStack {...vStackProps} data-testid="directions-section" mb="5">
      <Text
        {...getTypographyProps(titleDirectionsLegacyTypography, titleDirectionsSemanticTypography)}
      >
        {t('hoteldetails.mapgeneraldirections.title')}
      </Text>
      <Collapse startingHeight={95} in={readMore}>
        <Box
          ref={(node) => handleRefHeightChange(node, setHeight)}
          className="formatLinks"
          {...getTypographyProps({}, formatLinksSemanticTypography)}
        >
          {renderSanitizedHtml(data)}
        </Box>
      </Collapse>
      {height >= 95 && (
        <Link
          {...linkLayoutStyles}
          {...getTypographyProps(linkLegacyTypography, linkSemanticTypography)}
          onClick={() => {
            setReadMore(!readMore);
          }}
        >
          <Text boxShadow={!readMore ? '0 0 2rem 1rem white' : 'none'}>{t(linkName)}</Text>
        </Link>
      )}
    </VStack>
  );
}

const vStackProps = {
  align: 'stretch',
  fontSize: 'md',
  lineHeight: '3',
  mt: '1.25',
};

const formatLinksSemanticTypography = {
  textStyle: 'body-m-regular',
};

const titleDirectionsLegacyTypography = {
  fontWeight: 'semibold',
};

const titleDirectionsSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const linkLayoutStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const linkLegacyTypography = {
  fontSize: 'sm',
};

const linkSemanticTypography = {
  textStyle: 'link-s-regular',
};
