import { Box, Flex, Heading } from '@chakra-ui/react';
import { CardHolderType } from '@whitbread-eos/api';
import { Input } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useState } from 'react';

import { headerStyles } from '../styles';

interface Props {
  cardHolderNames: CardHolderType;
  setCardHolderNames: Dispatch<SetStateAction<CardHolderType>>;
  setHasError: Dispatch<SetStateAction<boolean>>;
}

export default function CardHolderName({
  cardHolderNames,
  setCardHolderNames,
  setHasError,
}: Readonly<Props>) {
  const [errorFirstName, setErrorFirstName] = useState<string>('');
  const [errorLastName, setErrorLastName] = useState<string>('');

  const { t } = useTranslation(['common']);

  return (
    <Flex mb={'5xl'} flexDir="column" data-testid="cardHolderNameSection" mt={16}>
      <Box pb={4}>
        <Heading as="h3" {...headerStyles}>
          {t('ccui.cardHolder.title')}
        </Heading>
      </Box>
      <Box
        w={{
          mobile: 'full',
          xs: 'full',
          sm: '25.063rem',
          md: '26.25rem',
          lg: '24.5rem',
          xl: '26.25rem',
        }}
      >
        <Input
          name="cardHolderFirstName"
          value={cardHolderNames.firstName}
          onChange={onCardHolderFirstNameChange}
          placeholderText={t('ccui.cardHolderName.firstName.placeholder')}
          error={errorFirstName}
        />
      </Box>
      <Box
        pt={6}
        w={{
          mobile: 'full',
          xs: 'full',
          sm: '25.063rem',
          md: '26.25rem',
          lg: '24.5rem',
          xl: '26.25rem',
        }}
      >
        <Input
          name="cardHolderLastName"
          value={cardHolderNames.lastName}
          onChange={onCardHolderLastNameChange}
          placeholderText={t('ccui.cardHolderName.lastName.placeholder')}
          error={errorLastName}
        />
      </Box>
    </Flex>
  );

  function onCardHolderFirstNameChange(firstName: string) {
    validateCardHolder(
      firstName,
      20,
      t('ccui.cardHolderName.firstName.requiredMessage'),
      t('ccui.cardHolderName.firstName.invalidMessage'),
      t('details.userForm.firstNameRequired'),
      setErrorFirstName
    );
    setCardHolderNames((prevState) => ({
      ...prevState,
      firstName,
    }));
  }

  function onCardHolderLastNameChange(lastName: string) {
    validateCardHolder(
      lastName,
      30,
      t('ccui.cardHolderName.lastName.requiredMessage'),
      t('ccui.cardHolderName.lastName.invalidMessage'),
      t('details.userForm.lastNameRequired'),
      setErrorLastName
    );
    setCardHolderNames((prevState) => ({
      ...prevState,
      lastName,
    }));
  }

  function validateCardHolder(
    name: string,
    lengthLimit: number,
    requiredMessage: string,
    invalidMessage: string,
    limitMessage: string,
    handleError: Dispatch<SetStateAction<string>>
  ) {
    if (name === '') {
      handleError(requiredMessage);
      setHasError(true);
    } else if (
      !/^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/.test(
        name
      )
    ) {
      handleError(invalidMessage);
      setHasError(true);
    } else if (name.length > lengthLimit) {
      handleError(limitMessage);
      setHasError(true);
    } else {
      handleError('');
      setHasError(false);
    }
  }
}
