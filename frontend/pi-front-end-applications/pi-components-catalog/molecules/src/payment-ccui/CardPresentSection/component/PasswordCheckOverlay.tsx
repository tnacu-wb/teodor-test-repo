import type { FlexProps } from '@chakra-ui/react';
import { Box, Button, Flex } from '@chakra-ui/react';
import { ModalVariants } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction } from 'react';

interface Props {
  isOpenOverlay: boolean;
  setIsOpenOverlay: Dispatch<SetStateAction<boolean>>;
}

export default function PasswordCheckOverlay({ isOpenOverlay, setIsOpenOverlay }: Readonly<Props>) {
  const { t } = useTranslation('common');

  return (
    <Box>
      <ModalVariants
        variant="cookie"
        isOpen={isOpenOverlay}
        closeOnOverlayClick={false}
        onClose={() => setIsOpenOverlay(isOpenOverlay)}
        variantProps={{ title: t('ccui.passwordCheckOverlay.title') }}
        dataTestId="passwordCheckOverlay_passwordCheck"
        hasAutoFocus={false}
      >
        <Flex {...modalWrapperStyles}>
          <Flex color="darkGrey1" data-testid="passwordCheckOverlay_passwordCheck_description">
            {t('ccui.passwordCheckOverlay.text')}
          </Flex>
          <Box {...modalButtonWrapperStyles}>
            <Button
              width="100%"
              variant="primary"
              mr="1rem"
              onClick={() => setIsOpenOverlay(false)}
              data-testid="passwordCheckOverlay_okButton"
            >
              {t('ccui.passwordCheckOverlay.okButton')}
            </Button>
          </Box>
        </Flex>
      </ModalVariants>
    </Box>
  );
}

const modalWrapperStyles = {
  mt: 'md',
  mb: 'lg',
  mx: 'lg',
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '24.5rem',
    lg: '34.5rem',
    xl: '33rem',
  },
  flexDirection: 'column',
} as FlexProps;

const modalButtonWrapperStyles = {
  mt: 'lg',
  justifyContent: 'space-around',
  alignItems: 'center',
} as FlexProps;
