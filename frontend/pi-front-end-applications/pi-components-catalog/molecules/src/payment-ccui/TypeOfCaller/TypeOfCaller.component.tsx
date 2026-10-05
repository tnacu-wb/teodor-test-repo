import { Box, Flex, Heading, RadioGroup, Text } from '@chakra-ui/react';
import { TypeOfCaller as TypeOfCallerEnum } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React from 'react';

interface Props {
  value: string | undefined;
  setValue: (type: string) => void;
}

export default function TypeOfCaller({ value, setValue }: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const typeOfCallerOptions = [
    {
      name: t('ccui.typeOfCaller.anyCustomer'),
      value: TypeOfCallerEnum.ANY_CUSTOMER,
      dataTestID: 'typeOfCaller_anyCustomer',
      type: `typeOfCaller_${TypeOfCallerEnum.ANY_CUSTOMER}`,
    },
    {
      name: t('ccui.typeOfCaller.accesibleCustomer'),
      value: TypeOfCallerEnum.ACCESIBLE_CUSTOMER,
      dataTestID: 'typeOfCaller_accesibleCustomer',
      type: `typeOfCaller_${TypeOfCallerEnum.ACCESIBLE_CUSTOMER}`,
    },
  ];

  return (
    <Flex flexDir="column" data-testid="typeOfCaller">
      <Box data-testid="typeOfCaller_options">
        <Box mb={4}>
          <Heading as="h3" {...headerStyles} data-testid="typeOfCaller_titleHeader">
            {t('ccui.typeOfCaller.title')}
          </Heading>
        </Box>
        <Box {...radioWrapperStyle} data-testid="typeOfCaller_options">
          <RadioGroup value={value} onChange={setValue} data-testid="typeOfCaller_radio-group">
            {typeOfCallerOptions.map((option) => (
              <RadioButton
                key={option.type}
                value={option.value}
                data-testid={option.dataTestID}
                type={option.type}
              >
                <Text data-testid={`${option.dataTestID}_text`} {...textStyles}>
                  {option.name}
                </Text>
              </RadioButton>
            ))}
          </RadioGroup>
        </Box>
      </Box>
    </Flex>
  );
}
const radioWrapperStyle = {
  w: { mobile: 'full', sm: '25.063rem', md: '24.5rem', xl: '26.25rem' },
};

const headerStyles = {
  fontSize: 20,
  lineHeight: 3,
  fontStyle: 'normal',
  fontWeight: 'semibold',
  color: 'darkGrey1',
};

const textStyles = {
  color: 'darkGrey1',
};
