import { Box, Button, Flex, Heading, Text, VStack } from '@chakra-ui/react';
import {
  AddressGuestInputWithCountry,
  CardHolderType,
  FRAUD_SECURITY_CHECK,
  ShortCountry,
} from '@whitbread-eos/api';
import { LoadingSpinner, ModalVariants } from '@whitbread-eos/atoms';
import { useMutationRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useState } from 'react';

import { headerStyles } from '../styles';

interface Props {
  billingAddress: AddressGuestInputWithCountry;
  basketReference: string | null;
  setIsSecurityCheckPassed: Dispatch<SetStateAction<boolean>>;
  setHasErrors: Dispatch<SetStateAction<boolean>>;
  cardHolderNames: CardHolderType;
}

export default function CardSecurityCheck({
  billingAddress,
  basketReference,
  setIsSecurityCheckPassed,
  setHasErrors,
  cardHolderNames,
}: Readonly<Props>) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSuccessOpen, setIsSuccessOpen] = useState(false);
  const [showSpinner, setShowSpinner] = useState(false);
  const { t } = useTranslation(['common']);
  const {
    mutation: fraudCheckMutation,
    isError: isFraudCheckError,
    error: fraudCheckErorr,
  } = useMutationRequest(FRAUD_SECURITY_CHECK, true);

  useEffect(() => {
    if (fraudCheckErorr) {
      setHasErrors(true);
    }

    if (!fraudCheckMutation.isPending && fraudCheckMutation.data) {
      if (
        fraudCheckMutation.data?.initiateSecurityCheck?.fraudCheckDecision === 'REJECT' ||
        fraudCheckMutation.data?.initiateSecurityCheck?.fraudCheckDecision === ''
      ) {
        setIsModalOpen(true);
      } else {
        setIsSuccessOpen(true);
        setIsSecurityCheckPassed(true);
      }
    }
    if (fraudCheckMutation.isPending) {
      const handler = setTimeout(() => {
        setShowSpinner(true);
      }, 4000);

      return () => {
        setShowSpinner(false);
        clearTimeout(handler);
      };
    }
  }, [fraudCheckMutation.data, fraudCheckMutation.isPending]);

  if (showSpinner) {
    return <LoadingSpinner loadingText={t('booking.loading')} />;
  }

  if (isFraudCheckError) {
    return <Text>{(fraudCheckErorr as Error).message}</Text>;
  }

  let cardSecurityCheckCountry: string | number | undefined;
  if ((billingAddress.country || billingAddress.countryCode) === ShortCountry.UK) {
    cardSecurityCheckCountry = ShortCountry.GB;
  } else {
    cardSecurityCheckCountry = billingAddress.country ?? billingAddress.countryCode;
  }

  return (
    <Box data-testid="cardSecurityCheck_Component">
      <Box pb={8}>
        <Heading as="h3" {...headerStyles} data-testid="cardSecurityCheck_title">
          {t('ccui.cardSecurityCheck.btnLabel')}
        </Heading>
      </Box>
      <Button
        size="md"
        variant="tertiary"
        onClick={checkFraudStatus}
        data-testid="cardSecurityCheck_launchButton"
        w={{
          mobile: 'full',
          xs: 'full',
          sm: '16.63rem',
          md: '18rem',
          lg: '18rem',
          xl: '19.063rem',
        }}
      >
        {t('ccui.cardSecurityCheck.btnLabel')}
      </Button>
      <ModalVariants
        onClose={() => setIsModalOpen(false)}
        isOpen={isModalOpen}
        variant="cookie"
        variantProps={{
          title: t('ccui.cardSecurityCheck.title'),
          delimiter: true,
        }}
        dataTestId="cardSecurityCheck_Modal"
      >
        <VStack
          w={{
            mobile: 'full',
            xs: 'full',
            sm: '25.063rem',
            md: '37.5rem',
            lg: '37.5rem',
            xl: '36rem',
          }}
        >
          <Flex p={6} w="full">
            <Button
              w={{
                mobile: 'full',
                xs: 'full',
                sm: '25.063rem',
                md: '34.5rem',
                lg: '34.5rem',
                xl: '33rem',
              }}
              variant="primary"
              onClick={() => setIsModalOpen(false)}
              data-testid="cardSecurityCheck_cancel"
            >
              {t('ccui.fraudCheck.btnCancel')}
            </Button>
          </Flex>
        </VStack>
      </ModalVariants>
      <ModalVariants
        onClose={() => setIsSuccessOpen(false)}
        isOpen={isSuccessOpen}
        variant="cookie"
        variantProps={{
          title: t('ccui.cardSecurityCheck.titleSuccess'),
          delimiter: true,
        }}
        dataTestId="cardSecurityCheck_Modal-Success"
      >
        <VStack
          w={{
            mobile: 'full',
            xs: 'full',
            sm: '25.063rem',
            md: '37.5rem',
            lg: '37.5rem',
            xl: '36rem',
          }}
        >
          <Flex p={6} w="full">
            <Button
              variant="primary"
              onClick={() => setIsSuccessOpen(false)}
              data-testid="cardSecurityCheck_continue"
              w={{
                mobile: 'full',
                xs: 'full',
                sm: '25.063rem',
                md: '34.5rem',
                lg: '34.5rem',
                xl: '31rem',
              }}
            >
              {t('ccui.cardSecurityCheck.btnContinue')}
            </Button>
          </Flex>
        </VStack>
      </ModalVariants>
    </Box>
  );

  function checkFraudStatus() {
    fraudCheckMutation.mutate({
      basketReference: basketReference,
      card: {
        cardHolderFirstName: cardHolderNames.firstName,
        cardHolderLastName: cardHolderNames.lastName,
        cardHolderAddress: {
          addressLine1: billingAddress.addressLine1,
          addressLine2: billingAddress.addressLine2 ?? '',
          addressLine3: billingAddress.addressLine3 ?? '',
          addressLine4: billingAddress.addressLine4 ?? '',
          country: cardSecurityCheckCountry,
          postalCode: billingAddress.postalCode,
        },
      },
    });
  }
}
