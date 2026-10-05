import { Box, BoxProps, Flex, FlexProps, Text } from '@chakra-ui/react';
import { Channel, FactItem } from '@whitbread-eos/api';
import {
  Accordion,
  AccordionItemProp,
  Info,
  ModalVariants,
  Notification,
} from '@whitbread-eos/atoms';
import { renderSanitizedHtml, useStaticHotelInformation } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

interface Props {
  channel: Channel;
}

export const KeyHotelFacts = ({ channel }: Readonly<Props>) => {
  const { t } = useTranslation(['common']);
  const { facts, isLoading, isError, error } = useStaticHotelInformation();
  const [faqItemList, setFaqItemList] = useState<AccordionItemProp[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    const factItems: AccordionItemProp[] = [];
    facts?.factItems &&
      facts?.factItems.length > 0 &&
      facts.factItems.map((factData: FactItem) => {
        const contentText = <Box>{renderSanitizedHtml(factData.description)}</Box>;
        factItems.push({ title: factData.title, content: contentText });
      });
    setFaqItemList(factItems);
  }, []);

  if (isLoading) {
    return <Text>{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error)?.message}</Text>;
  }

  const toggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };

  const isFactObjectValid = () => {
    return facts && facts?.factItems?.length > 0;
  };

  const isValidForRender = () => {
    return channel === Channel.Ccui && isFactObjectValid();
  };

  return isValidForRender() ? (
    <Box data-testid="keyHotelFacts">
      <Notification
        title={t('hoteldetails.facts.title')}
        variant="info"
        status="info"
        svg={<Info />}
        prefixDataTestId="key-hotel-facts"
        {...notificationStyles}
        description={
          <Text {...seeDetailsLinkStyles} data-testid="hdp_hotelKeyFactsLink" onClick={toggleModal}>
            {t('hoteldetails.facts.seeNow')}
          </Text>
        }
      />

      <ModalVariants onClose={toggleModal} isOpen={isModalOpen} variant="info">
        <Box {...modalStyles}>
          <Box maxW="full" {...containerStyles}>
            <Flex {...accordianWrapperStyles}>
              <Box data-testid="hdp-key-hotel-fact-list" {...factListBoxStyles}>
                <Accordion
                  allowMultiple={false}
                  accordionItems={faqItemList}
                  bgColor="var(--chakra-colors-baseWhite)"
                />
              </Box>
            </Flex>
          </Box>
        </Box>
      </ModalVariants>
    </Box>
  ) : null;
};

const factListBoxStyles = {
  minWidth: { base: '100%', md: '90%' },
  m: { base: '4', sm: '1' },
} as BoxProps;

const accordianWrapperStyles = {
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

const containerStyles = {
  mb: { base: 'lg', sm: '2xl' },
};

const modalStyles = {
  w: '50.125rem',
  px: 'md',
  pb: 'lg',
} as BoxProps;

const notificationStyles = {
  maxW: { base: 'full', md: 'full' },
  lineHeight: '2',
  backgroundColor: 'baseWhite',
  border: 'none',
};

const seeDetailsLinkStyles = {
  display: 'inline-block',
  fontSize: 'sm',
  lineHeight: '2',
  color: 'btnSecondaryEnabled',
  _hover: {
    cursor: 'pointer',
  },
};
