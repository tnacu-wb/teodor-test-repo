import { Box, Flex } from '@chakra-ui/react';
import type { FlexProps } from '@chakra-ui/react';
import { EckohParameters, EckohStatus } from '@whitbread-eos/api';
import { Button, ModalVariants } from '@whitbread-eos/atoms';
import { useCustomLocale, timeTrackerSeconds } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { useState, useEffect, Dispatch, SetStateAction, useRef } from 'react';

interface Props {
  onSuccess: () => void;
  onFail: () => void;
  initiateIframe: () => void;
  eckohParameters?: EckohParameters;
  eckohStatus: EckohStatus;
  setIsEnabledEckohQuery: Dispatch<SetStateAction<boolean>>;
  disabledEckoh: boolean;
  onIframeLoad: (time: string) => void;
}

export default function LaunchEckoh({
  onSuccess,
  onFail,
  initiateIframe,
  eckohParameters,
  eckohStatus,
  setIsEnabledEckohQuery,
  disabledEckoh,
  onIframeLoad,
}: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { t } = useTranslation('common');
  const router = useRouter();
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isCancelOpen, setIsCancelOpen] = useState(false);
  const [isOpened, setIsOpened] = useState(false);
  const timerRef = useRef<null | (() => string)>(null);
  const { language, country } = useCustomLocale();

  useEffect(() => {
    switch (eckohStatus) {
      case EckohStatus.FAILED:
        setIsOpened(false);
        setIsCancelOpen(true);
        onFail();
        setIsEnabledEckohQuery(false);
        break;
      case EckohStatus.NOT_FOUND:
        onFail();
        setIsEnabledEckohQuery(false);
        break;
      case EckohStatus.PENDING:
        onFail();
        setIsEnabledEckohQuery(true);
        setIsSuccess(false);
        break;
      case EckohStatus.SUCCESS:
        onSuccess();
        setIsEnabledEckohQuery(false);
        setIsSuccess(true);
        break;
    }
  }, [eckohStatus]);

  useEffect(() => {
    if (eckohParameters) {
      setTimeout(showIframe);
    }
  }, [eckohParameters]);

  useEffect(() => {
    if (isOpened && timerRef.current === null) {
      timerRef.current = timeTrackerSeconds();
    } else {
      timerRef.current = null;
    }
  }, [isOpened]);

  const WB_ECKOH_IFRAME = 'wb-echo-iframe';
  const onLoad = () => {
    if (timerRef.current) {
      onIframeLoad(timerRef.current());
    }
  };

  return (
    <>
      <Box mt={16}>
        <Button
          size="sm"
          variant="tertiary"
          isDisabled={disabledEckoh}
          onClick={() => {
            setIsOpened(true);
            initiateIframe();
          }}
          data-testid="launchEckoh_launchButton"
          {...eckohButtonStyles}
        >
          {t('ccui.eckoh.button.title')}
        </Button>
      </Box>
      <ModalVariants
        onClose={() => {
          setIsOpened(false);
          setIsCancelOpen(!isSuccess);
        }}
        closeOnOverlayClick={false}
        isOpen={isOpened}
        variant="info"
        dataTestId="launchEckoh"
        variantProps={{
          title: 'Eckoh Iframe',
          delimiter: true,
        }}
      >
        <iframe
          name={WB_ECKOH_IFRAME}
          width="900"
          height="405"
          onLoad={onLoad}
          title="eckoh iframe"
        ></iframe>
      </ModalVariants>
      <ModalVariants
        variant="cookie"
        isOpen={isCancelOpen}
        closeOnOverlayClick={false}
        onClose={() => setIsCancelOpen(false)}
        variantProps={{ title: t('ccui.eckoh.cancelBooking.title') }}
        dataTestId="launchEckoh_cancel_booking"
        hasAutoFocus={false}
      >
        <Flex {...modalWrapperStyles}>
          <Flex color="darkGrey1">{t('ccui.eckoh.cancelBooking.description')}</Flex>
          <Flex {...modalButtonWrapperStyles}>
            <Button
              size="md"
              variant="primary"
              mr="1rem"
              onClick={() => setIsCancelOpen(false)}
              data-testid="launchEckoh_retryButton"
            >
              {t('ccui.eckoh.cancelBooking.retryButton')}
            </Button>
            <Button
              size="md"
              variant="tertiary"
              ml="1rem"
              onClick={() => {
                setIsCancelOpen(false);
                setIsConfirmOpen(true);
              }}
              data-testid="launchEckoh_cancelButton"
            >
              {t('ccui.eckoh.cancelBooking.cancelButton')}
            </Button>
          </Flex>
        </Flex>
      </ModalVariants>
      <ModalVariants
        variant="cookie"
        isOpen={isConfirmOpen}
        closeOnOverlayClick={false}
        onClose={() => setIsConfirmOpen(false)}
        variantProps={{ title: t('ccui.eckoh.confirmCancelBooking.title') }}
        dataTestId="launchEckoh_confirm"
        headerStyles={{
          maxW: '33rem',
        }}
        hasAutoFocus={false}
      >
        <Flex {...modalWrapperStyles}>
          <Flex {...modalButtonWrapperConfirmStyles}>
            <Button
              size="md"
              variant="primary"
              mr="1rem"
              onClick={() => setIsConfirmOpen(false)}
              data-testid="launchEckoh_confirm_retryButton"
            >
              {t('ccui.eckoh.confirmCancelBooking.retryButton')}
            </Button>
            <Button
              size="md"
              variant="tertiary"
              ml="1rem"
              onClick={() => {
                setIsConfirmOpen(false);
                router.push(`/${country}/${language}`);
              }}
              data-testid="launchEckoh_confirm_cancelButton"
            >
              {t('ccui.eckoh.confirmCancelBooking.cancelButton')}
            </Button>
          </Flex>
        </Flex>
      </ModalVariants>
    </>
  );

  function showIframe() {
    const form = document.createElement('form');
    form.action = publicRuntimeConfig.ECKOH_IFRAME_SRC;
    form.method = 'POST';
    form.target = WB_ECKOH_IFRAME;
    for (const prop in eckohParameters) {
      const input = document.createElement('input');
      input.type = 'hidden';
      input.name = prop;
      input.value = eckohParameters[prop as keyof EckohParameters];
      form.appendChild(input);
    }
    document.body.appendChild(form);
    form.submit();
  }
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

const eckohButtonStyles = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '18rem',
    lg: '18rem',
    xl: '19.313rem',
  },
  height: '3.5rem',
};
const modalButtonWrapperStyles = {
  mt: 'lg',
  justifyContent: 'space-around',
  alignItems: 'center',
} as FlexProps;

const modalButtonWrapperConfirmStyles = {
  mt: 2,
  justifyContent: 'space-around',
  alignItems: 'center',
} as FlexProps;
