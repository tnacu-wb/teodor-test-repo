import { Box, BoxProps, Flex, FlexProps, Text } from '@chakra-ui/react';
import { BookingInfoType, DonationPackage, GET_DONATIONS_QUERY } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  formatCurrency,
  isIVMEnabled,
  akamaiImageLoader,
  renderSanitizedHtml,
  useCustomLocale,
  useQueryRequest,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';

interface Props extends BoxProps {
  selectedDonation?: DonationPackage;
  onDonationChange: (state: DonationPackage) => void;
  bookingInformation: BookingInfoType;
  bookingChannel: string;
}

const NO_DONATION_PACKAGE: DonationPackage = {
  code: '',
  currency: '',
  unitPrice: 0,
};

export default function Donations({
  selectedDonation,
  onDonationChange,
  bookingInformation,
  bookingChannel,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();

  const { data, isError, error, isLoading } = useQueryRequest(
    [
      'GetDonations',
      bookingInformation.hotelId,
      country,
      language,
      bookingInformation.ratePlanCode,
      bookingChannel,
    ],
    GET_DONATIONS_QUERY,
    {
      country,
      language,
      hotelId: bookingInformation.hotelId,
      rateCode: bookingInformation.ratePlanCode,
      bookingChannel,
    }
  );

  const donations = data?.donations;

  if (isLoading) {
    return (
      <Text data-testid="Donation-LoadingMessage">{t('searchresults.list.hotel.loading')}</Text>
    );
  }

  if (isError) {
    return (
      <Box>
        <Text data-testid="Donation-Error">Error on getting donations....</Text>
        <Text data-testid="Donation-ErrorMessage">{(error as Error).message}</Text>
      </Box>
    );
  }

  if (!donations) {
    return null;
  }

  const getDonationRadio = (donation: DonationPackage, index: number) => (
    <Box
      key={`${donation?.code}-${donation?.unitPrice}`}
      {...radioBtnStyle}
      data-testid={`Donation-DonationRadio-${index}`}
      onClick={() => onDonationChange(donation)}
      donation-amount={donation.unitPrice}
    >
      <RadioButton
        type={`DonationRadio-${index}`}
        isChecked={selectedDonation && selectedDonation.unitPrice === donation?.unitPrice}
      >
        <Text
          data-testid={`Donation-DonationLabel-${index}`}
          {...getTypographyProps({}, donationLabelSemanticTypography)}
        >
          {donation?.currency
            ? t('charityDonation.donate').replace(
                '{amount, number, ::unit-width-iso-code currency/GBP}',
                `${formatCurrency(donation.currency!)}${donation.unitPrice.toFixed(2)}`
              )
            : t('charityDonation.noDonation')}
        </Text>
      </RadioButton>
    </Box>
  );

  return (
    <Box data-testid="Donation-Container">
      <Box {...donationCostStyle}>
        <Flex direction="column">
          <Text
            data-testid="Donation-Title"
            {...donationTitleStyle}
            {...getTypographyProps(donationTitleLegacyTypography, donationTitleSemanticTypography)}
          >
            {donations?.name}
          </Text>
          <Flex {...contentWrapper}>
            {donations?.imageSrc && (
              <Flex data-testid="image-wrapper" {...imageWrapperStyle}>
                <Box key={donations?.imageSrc} {...imageBoxStyle}>
                  <Image
                    data-testid="Donation-Image"
                    sizes="100vw"
                    src={formatAssetsUrl(donations?.imageSrc)}
                    alt={donations?.imageSrc}
                    width={288}
                    height={200}
                    style={{
                      objectFit: 'contain',
                      width: '100%',
                      color: 'transparent',
                      height: '100%',
                      maxHeight: '200px',
                      paddingTop: '15px',
                      paddingBottom: '15px',
                    }}
                    priority
                    loader={isIVMEnabled() ? akamaiImageLoader : undefined}
                  />
                </Box>
              </Flex>
            )}
            <Box
              data-testid="Donation-Description"
              ml={{ base: 0, md: 'lg' }}
              w="full"
              className="formatLinks"
              {...getTypographyProps({}, donationDescriptionSemanticTypography)}
            >
              {renderSanitizedHtml(donations.description)}
            </Box>
          </Flex>

          <Flex data-testid="donations-wrapper" {...donationOptionsStyle}>
            {!!donations?.donationPackages?.length && getDonationRadio(NO_DONATION_PACKAGE, 0)}
            {donations?.donationPackages?.map((donation: DonationPackage, index: number) =>
              getDonationRadio(donation, index + 1)
            )}
          </Flex>
        </Flex>
      </Box>
    </Box>
  );
}

const contentWrapper = {
  w: 'full',
  mb: '3xl',
  direction: { mobile: 'column', md: 'row' },
} as FlexProps;

const donationCostStyle = {
  w: { mobile: 'full', xs: 'full', md: '45rem', lg: '50.5rem', xl: 'full' },
  mb: '5xl',
  border: '1px solid',
  borderColor: 'lightGrey1',
  borderRadius: 'base',
  p: 'lg',
};
const donationTitleStyle = {
  mb: 'lg',
};

const donationTitleLegacyTypography = {
  lineHeight: '4',
  fontSize: '2xl',
  fontWeight: 'semibold',
};

const donationTitleSemanticTypography = {
  textStyle: 'heading-m',
} as const;

const donationDescriptionSemanticTypography = {
  textStyle: 'body-s-regular',
} as const;

const donationLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const radioBtnStyle = {
  w: { mobile: 'full', md: '9.80rem', lg: '11.125rem', xl: '12rem' },
  mb: { mobile: 'md', md: 'lg' },
  cursor: 'pointer',
};

const donationOptionsStyle = {
  w: 'full',
  justify: { mobile: 'start', xs: 'space-around', sm: 'space-between' },
  direction: { mobile: 'column', md: 'row' },
  wrap: 'wrap',
} as FlexProps;

const imageWrapperStyle = {
  justify: 'space-between',
  mb: { base: 'lg', md: 0 },
  wrap: { mobile: 'nowrap', md: 'wrap' },
  direction: { mobile: 'row', md: 'column' },
} as FlexProps;

const imageBoxStyle = {
  pos: 'relative',
  backgroundColor: '#511e63',
  h: '100%',
  pl: '15px',
  pr: '15px',
  mb: 0,
  maxH: {
    base: '200px',
    md: '100%',
  },
  display: { base: 'block', md: 'flex' },
  alignItems: 'center',
  w: {
    base: '100%',
    md: '18rem',
  },
} as FlexProps;
