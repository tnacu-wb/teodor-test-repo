import { Flex, Heading, Button } from '@chakra-ui/react';
import { ModalVariants, Notification, Success } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

import {
  headingStyles,
  modalButtonsContainerStyles,
  modalButtonsStyles,
  modalContentStyles,
} from './common';

function PreCheckInSuccessModal({
  isOpen,
  onClose,
  messageTag = 'precheckin.complete.msg',
  overlayStyles = {},
}: any) {
  const { t } = useTranslation();

  const baseDataTestId = 'PrecheckInSuccess';
  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={onClose}
      variant="default"
      variantProps={{ title: '', delimiter: false }}
      headerStyles={{ textAlign: 'center', justifyContent: 'flex-end' }}
      updatedWidth={{ sm: '100%', md: '37.5rem', lg: '37.5rem', xl: '39.93rem' }}
      overlayStyles={overlayStyles}
    >
      <Flex {...modalContentStyles}>
        <Heading as="h2" {...headingStyles}>
          {t('precheckin.success.title')}
        </Heading>
        <Notification
          status="success"
          variant="success"
          svg={<Success style={{ marginTop: 'md' }} />}
          prefixDataTestId={`${baseDataTestId}-modal`}
          description={t(messageTag)}
          wrapperStyles={{ w: 'lg', m: 'md', borderRadius: '0.9' }}
        />
        <Flex {...modalButtonsContainerStyles}>
          <Button
            data-testid="PreCheckIn-ModalCloseButton"
            size="xsm"
            marginTop={'lg'}
            onClick={onClose}
            {...modalButtonsStyles}
          >
            {t('precheckin.closebutton')}
          </Button>
        </Flex>
      </Flex>
    </ModalVariants>
  );
}

export default PreCheckInSuccessModal;
