import { Box, Text, TextProps } from '@chakra-ui/react';
import { FaqItem, HotelBrand } from '@whitbread-eos/api';
import { Accordion } from '@whitbread-eos/atoms';
import type { AccordionItemProp } from '@whitbread-eos/atoms';
import {
  renderSanitizedHtml,
  useSemanticTypography,
  useStaticHotelInformation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

export const HotelFaq = () => {
  const { brand, faq, isLoading, isError, error } = useStaticHotelInformation();
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();

  const [faqItemList, setFaqItemList] = useState<AccordionItemProp[]>([]);

  useEffect(() => {
    const faqItems: AccordionItemProp[] = [];
    faq?.faqItems?.map((faqData: FaqItem) => {
      const contentText = <Box>{renderSanitizedHtml(faqData?.answer ?? '')}</Box>;
      faqItems.push({ title: faqData?.question, content: contentText });
    });
    setFaqItemList(faqItems);
  }, [faq]);

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  const isHubOrPiHotel = [HotelBrand.HUB, HotelBrand.PI, HotelBrand.PID].includes(
    brand?.toUpperCase() as HotelBrand
  );

  if (!isHubOrPiHotel) {
    return null;
  }

  return (
    <Box data-testid="hdp_faqs">
      <Box maxW="full" {...containerStyles}>
        <Text
          data-testid="hdp-faqs-title"
          as="h3"
          {...getTypographyProps(headingFaqLegacyTypography, headingFaqSemanticTypography)}
        >
          {faq?.title ? faq.title : t('hoteldetails.faqs.title')}
        </Text>
        <Box data-testid="hdp-faqs-list" mt={{ base: '2', sm: '1' }} className="formatLinks">
          <Accordion
            allowMultiple={false}
            accordionItems={faqItemList}
            bgColor="var(--chakra-colors-lightGrey5)"
          />
        </Box>
      </Box>
    </Box>
  );
};

const headingFaqLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { base: 'xl', sm: '2xl' },
  lineHeight: { base: '3', sm: '4' },
};

const headingFaqSemanticTypography = {
  textStyle: {
    base: 'heading-s',
    md: 'heading-m',
  } as unknown as TextProps['textStyle'],
};

const containerStyles = {
  mt: { base: 'lg', sm: '3xl' },
  mb: { base: 'lg', sm: '2xl' },
};
