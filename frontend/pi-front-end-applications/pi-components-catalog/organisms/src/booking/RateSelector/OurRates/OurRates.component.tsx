import { Box, Text, Divider } from '@chakra-ui/react';
import type { HIRateClassification } from '@whitbread-eos/api';
import { Info, ModalVariants, Notification } from '@whitbread-eos/atoms';
import { renderSanitizedHtml, getCorporateDiscountRatePlanCode } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

interface Props {
  rateClassifications: (HIRateClassification | undefined)[];
  brand: string;
}

function reOrderRatePLans(rateClassifications: (HIRateClassification | undefined)[]) {
  return rateClassifications.sort((a: any, b: any) => a?.rateOrder - b?.rateOrder);
}

export default function OurRates({ brand, rateClassifications }: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const [isModalOpen, setIsModalOpen] = useState(false);

  function toggleModal() {
    setIsModalOpen(!isModalOpen);
  }

  const corporateDiscountRatePlanCode = getCorporateDiscountRatePlanCode(
    rateClassifications as any
  );

  if (corporateDiscountRatePlanCode) {
    reOrderRatePLans(rateClassifications);
  }

  const additionalInformation = corporateDiscountRatePlanCode && (
    <Box>
      {renderSanitizedHtml(
        t(`promotion.discountrate.${corporateDiscountRatePlanCode}.additionalinformation`)
      )}
      <Divider {...dividerStyle} />
    </Box>
  );

  return (
    <>
      <Box mt="sm">
        <Notification
          border="none"
          status="info"
          prefixDataTestId="hdp_ratesExplainedLink"
          variant="infoGrey"
          svg={<Info />}
          textDecoration="underline"
          isInnerHTML
          description={
            <Text
              {...ourRatesExplainedLinkStyles}
              data-testid="hdp_ratesExplainedLinkText"
              onClick={toggleModal}
            >
              {t('pihotelinfo.ratesExplained')}
            </Text>
          }
        />
      </Box>
      <ModalVariants
        onClose={toggleModal}
        isOpen={isModalOpen}
        variant="info"
        variantProps={{
          title: t('pihotelinfo.ourRates'),
          delimiter: true,
        }}
      >
        <Box p={6} pt={0}>
          {additionalInformation}
          {rateClassifications?.map((rate: HIRateClassification | undefined, index: number) => {
            if (rate) {
              return (
                <Box key={index}>
                  <Text as="b">{`${brand.toLowerCase() === 'hub' ? 'hub ' : ''}${
                    rate?.rateName
                  }`}</Text>
                  <Text pb="md">{rate?.rateDescription}</Text>
                </Box>
              );
            }
          })}
        </Box>
      </ModalVariants>
    </>
  );
}

const ourRatesExplainedLinkStyles = {
  fontSize: 'md',
  textDecoration: 'underline',
  _hover: {
    cursor: 'pointer',
  },
};
const dividerStyle = {
  my: 'md',
  border: 'border: 1px solid',
};
