import { Collapse, Link, List, ListItem, Text, VStack } from '@chakra-ui/react';
import {
  handleRefHeightChange,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

export default function TransportInformation() {
  const { transportInformation: data, isLoading, isError, error } = useStaticHotelInformation();
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

  return (
    <VStack {...vStackProps} data-testid="transport-information-section" mb="5">
      <Text
        {...getTypographyProps(titleTransportLegacyTypography, titleTransportSemanticTypography)}
      >
        {t('hoteldetails.localinfo.title')}
      </Text>
      <Collapse startingHeight={95} in={readMore}>
        <List
          ref={(node) => handleRefHeightChange(node, setHeight)}
          data-testid="transport-information-list"
        >
          {data?.map((step, index) => (
            <ListItem key={step}>
              {step && (
                <Text
                  data-testid={`transport-information-item-${index}`}
                  {...getTypographyProps({}, listItemTransportSemanticTypography)}
                >
                  {step}
                </Text>
              )}
            </ListItem>
          ))}
        </List>
      </Collapse>
      {height >= 95 && (
        <Link
          {...linkTransportLayoutStyles}
          {...getTypographyProps(linkTransportLegacyTypography, linkTransportSemanticTypography)}
          data-testid="hdp_transportInformationReadMoreLink"
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

const listItemTransportSemanticTypography = {
  textStyle: 'body-m-regular',
};

const titleTransportLegacyTypography = {
  fontWeight: 'semibold',
};

const titleTransportSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const vStackProps = {
  align: 'stretch',
  fontSize: 'md',
  lineHeight: '3',
  mt: '1.25',
};

const linkTransportLayoutStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const linkTransportLegacyTypography = {
  fontSize: 'sm',
};

const linkTransportSemanticTypography = {
  textStyle: 'link-s-regular',
};
