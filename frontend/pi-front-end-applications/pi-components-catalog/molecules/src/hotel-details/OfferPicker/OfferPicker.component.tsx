import { Flex, FlexProps, Heading, Text } from '@chakra-ui/react';
import { HIAEMroomType, HIAvailabilityRates, HIRateClassification } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import { formatCurrency, formatDataTestId, formatPrice } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

type Props = {
  data: {
    hotelAvailability: HIAvailabilityRates;
    ratesInformation: HIAEMroomType[];
    rateClassifications: HIRateClassification[];
  };
  activeRate: string;
  handleRateClick: (rate: string) => void;
  brand: string;
};

export default function OfferPicker({ data, activeRate, handleRateClick, brand }: Readonly<Props>) {
  const baseDataTestId = 'OfferPicker';
  const hotelAvailability = data?.hotelAvailability.hotelAvailability;
  const { t } = useTranslation();

  return (
    <Flex direction="column" data-testid={formatDataTestId(baseDataTestId, 'container')}>
      <Heading
        as="h3"
        data-testid={formatDataTestId(baseDataTestId, 'heading')}
        {...sectionHeadingStyle}
      >
        <Flex {...stepNumberStyle}>1</Flex>
        {t('hoteldetails.rates.grid.chooseRate')}
      </Heading>
      <Flex {...ratesListStyle} data-testid={formatDataTestId(baseDataTestId, 'rates-list')}>
        {hotelAvailability?.roomRates?.map((rate) => {
          const matchedActiveRate = data?.rateClassifications?.find(
            (item) => item?.rateClassification === rate?.ratePlanCode
          );

          return (
            <RadioButton
              key={rate.ratePlanCode}
              value={rate.ratePlanCode}
              data-testid={formatDataTestId(baseDataTestId, `rate-${rate.ratePlanCode}`)}
              padding="var(--chakra-space-xmd)"
              flexDirection={{ mobile: 'column-reverse', sm: 'row' }}
              alignItems={{ mobile: 'center', sm: 'flex-start' }}
              width="full"
              onChange={(ev) => {
                handleRateClick(ev.target.value);
              }}
              isChecked={activeRate === rate.ratePlanCode}
              withOutline
            >
              <Flex
                data-testid={formatDataTestId(baseDataTestId, `rate-content-${rate.ratePlanCode}`)}
                {...rateContentStyle}
              >
                <Flex {...rateContentBoxStyle}>
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, `rate-name-${rate.ratePlanCode}`)}
                    {...rateNameStyle}
                  >
                    {brand.toLowerCase() === 'hub' ? `${t('booking.rates.hub.prefix')} ` : ''}
                    {matchedActiveRate?.rateName}
                  </Text>
                  <Text flexDirection={{ mobile: 'column', md: 'row' }} {...rateTotalTextStyle}>
                    Total from
                    <Text
                      as="span"
                      flexDirection={{ mobile: 'column', md: 'row' }}
                      data-testid={formatDataTestId(
                        baseDataTestId,
                        `rate-price-${rate.ratePlanCode}`
                      )}
                      {...ratePriceTagStyle}
                    >
                      {formatPrice(
                        formatCurrency(rate.roomTypes[0].rooms[0].roomPriceBreakdown?.currencyCode),
                        rate.roomTypes[0].rooms[0].roomPriceBreakdown?.totalNetAmount
                      )}
                    </Text>
                  </Text>
                </Flex>
                <Text fontSize="xs" color="darkGrey2" display={{ mobile: 'none', md: 'block' }}>
                  {matchedActiveRate?.rateDescription}
                </Text>
              </Flex>
            </RadioButton>
          );
        })}
      </Flex>
    </Flex>
  );
}

const sectionHeadingStyle = {
  display: 'inline-flex',
  fontSize: { mobile: 'xl', md: '2xl' },
  fontWeight: 'bold',
  fontFamily: 'header',
  alignItems: 'center',
  mb: '0.938rem',
};

const stepNumberStyle = {
  p: { mobile: '0.625rem 0.625rem', md: '0.188rem 0.125rem' },
  w: '1.438rem',
  h: '1.5rem',
  bg: 'lightGrey4',
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: '50%',
  fontWeight: 'semibold',
  fontSize: 'md',
  mr: '0.688rem',
};

const ratesListStyle = {
  gap: { mobile: 'xs', md: '0.625rem' },
  direction: 'row',
  justifyContent: 'space-between',
  overflow: 'auto',
} as FlexProps;

const rateContentStyle = {
  flexDirection: 'column',
  alignItems: { mobile: 'center', sm: 'flex-start' },
  gap: 'xs',
  ml: { mobile: '-0.5rem', sm: '0' },
} as FlexProps;

const rateContentBoxStyle = {
  flexDirection: { mobile: 'column', md: 'row' },
  alignItems: { mobile: 'center', sm: 'flex-start' },
  justifyContent: 'space-between',
  gap: { mobile: 'xs', sm: '0' },
  w: { mobile: 'full', sm: '276px' },
  textAlign: { mobile: 'center', sm: 'left' },
} as FlexProps;

const rateNameStyle = {
  fontWeight: 'bold',
  fontSize: 'md',
};

const rateTotalTextStyle = {
  alignSelf: 'flex-end',
  ml: 'auto',
  display: 'flex',
  fontSize: { mobile: '0.688rem', sm: 'sm' },
  color: 'darkGrey2',
  fontWeight: 'normal',
  alignItems: 'center',
};

const ratePriceTagStyle = {
  fontSize: 'md',
  fontWeight: 'semibold',
  ml: { mobile: 0, sm: 'xs' },
  color: 'darkGrey1',
};
