import { Box, Flex, Heading, RadioGroup, Text } from '@chakra-ui/react';
import { CardStatus, CcuiCardType } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { useEffect, useState } from 'react';

import { descriptionStyles, headerStyles } from '../styles';
import PasswordCheckOverlay from './component/PasswordCheckOverlay';

interface Props {
  value: string;
  setValue: (type: string) => void;
  disabledOption: string;
  cardType: string;
}

export default function CardPresentSection({
  value,
  setValue,
  disabledOption,
  cardType,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [isOverlayOpen, setIsOverlayOpen] = useState<boolean>(false);
  const isCNP = value === CardStatus.CARD_NOT_PRESENT && cardType === CcuiCardType.NEW_PIBA;

  useEffect(() => {
    if (isCNP) {
      setIsOverlayOpen(true);
    }
  }, [isCNP]);

  const cardStatus = [
    {
      name: t('ccui.cardPresentStatus.cardPresent'),
      dataTestID: 'Card present',
      type: CardStatus.CARD_PRESENT,
    },
    {
      name: t('ccui.cardPresentStatus.CNP'),
      dataTestID: 'CNP (Card not present)',
      type: CardStatus.CARD_NOT_PRESENT,
    },
  ];

  return (
    <Flex flexDir="column" data-testid="cardStatusSection">
      <Box pb={4} data-testid="cardStatusSection_title">
        <Heading as="h3" {...headerStyles} mb="md" data-testid="cardStatusSection_title-heading">
          {t('ccui.cardPresentStatus.title')}
        </Heading>
      </Box>
      <Box pb={4} data-testid="cardStatusSection_info">
        <Heading as="h6" {...descriptionStyles} data-testid="cardStatusSection_info-heading">
          {t('ccui.cardPresentStatus.info')}
        </Heading>
      </Box>
      <Box pb={4} {...radioWrapperStyle} data-testid="cardStatusSection_options">
        <RadioGroup value={value} onChange={setValue} data-testid="cardStatusSection_radio-group">
          {cardStatus.map((option, index) => (
            <RadioButton
              key={option.type}
              value={option.type}
              listIndex={index === cardStatus.length - 1 ? 'last' : index}
              isDisabled={option.type === disabledOption}
              data-testid={`cardStatusSection-${option.dataTestID}`}
              type={option.dataTestID}
            >
              <Text data-testid={`cardStatusSection_text-${option.dataTestID}`}>{option.name}</Text>
            </RadioButton>
          ))}
        </RadioGroup>
        {isCNP && (
          <PasswordCheckOverlay isOpenOverlay={isOverlayOpen} setIsOpenOverlay={setIsOverlayOpen} />
        )}
      </Box>
    </Flex>
  );
}

const radioWrapperStyle = {
  w: { mobile: 'full', sm: '25.063rem', md: '27.563rem', lg: '24.5rem', xl: '26.25rem' },
};
