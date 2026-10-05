import {
  Box,
  Flex,
  Grid,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
} from '@chakra-ui/react';
import { LanguageEnum } from '@whitbread-eos/api';
import { Bell, Button, DismissDarkGrey, Icon } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import {
  buttonsStyle,
  delimiterModalStyle,
  descriptionStyle,
  headerContentStyle,
  headerTitleStyle,
  modalBodyBoxStyle,
  modalBodyStyle,
  modalCloseButtonStyle,
  modalContentStyle,
  wrapperStyles,
  closeButtonStyle,
  allowButtonDE,
  allowButtonGB,
} from './ConsentNotificationModal.style';

interface Props {
  isModalVisible: boolean;
  onConsentModalClose: () => void;
  onConsentModalAllow: () => void;
  onConsentModalDeny: () => void;
  language?: string;
}
export default function ConsentNotificationModalComponent({
  isModalVisible,
  onConsentModalClose,
  onConsentModalAllow,
  onConsentModalDeny,
  language,
}: Readonly<Props>) {
  const { t } = useTranslation();

  function renderModalContent() {
    return (
      <Flex data-testid="pi-notification-permission-popup-container" {...wrapperStyles}>
        <Box
          data-testid="pi-notification-permission-popup-description"
          {...descriptionStyle}
          sx={{
            a: {
              fontWeight: 'var(--chakra-fontWeights-medium)',
              textDecoration: 'underline',
            },
          }}
        >
          {renderSanitizedHtml(t('pushNotifications.popup.description'))}
        </Box>

        <Flex {...buttonsStyle}>
          <Button
            {...(language === LanguageEnum.GERMAN ? allowButtonDE : allowButtonGB)}
            data-testid="pi-notification-permission-popup-allow-btn"
            size="sm"
            variant="secondary"
            mr="md"
            onClick={onConsentModalAllow}
          >
            {t('pushNotifications.popup.allow')}
          </Button>
          <Button
            data-testid="pi-notification-permission-popup-deny-btn"
            size="sm"
            width="188px"
            variant="tertiary"
            onClick={onConsentModalDeny}
            {...closeButtonStyle}
          >
            {t('pushNotifications.popup.dontAllow')}
          </Button>
        </Flex>
      </Flex>
    );
  }
  return (
    <Modal
      data-testid="pi-notification-permission-popup-chakra-modal"
      scrollBehavior="inside"
      isCentered={false}
      isOpen={isModalVisible}
      onClose={onConsentModalClose}
      closeOnOverlayClick={false}
      blockScrollOnMount={false}
      trapFocus={false}
    >
      <ModalContent
        data-testid="pi-notification-permission-popup-modal-content"
        {...modalContentStyle}
        width={{ mobile: '90%', sm: '27.5rem' }}
        containerProps={{
          width: {
            mobile: '100%',
            sm: '0rem',
          },
          height: {
            mobile: '0rem',
          },
        }}
      >
        <ModalHeader data-testid="pi-notification-permission-popup-modal-header" width={'100%'}>
          <Flex flexDir="column">
            <Grid {...headerContentStyle}>
              <Flex
                {...headerTitleStyle}
                data-testid="pi-notification-permission-popup-modal-title"
              >
                <Icon svg={<Bell />} mr="sm" />
                {t('pushNotifications.popup.title')}
              </Flex>

              <ModalCloseButton
                data-testid="pi-notification-permission-popup-close-btn"
                {...modalCloseButtonStyle}
              >
                <Icon svg={<DismissDarkGrey transform="scale(1.1)" />} />
              </ModalCloseButton>
            </Grid>
            <Box
              data-testid="pi-notification-permission-popup-delimiter"
              {...delimiterModalStyle}
            />
          </Flex>
        </ModalHeader>
        <ModalBody data-testid="pi-notification-permission-popup-modal-body" {...modalBodyStyle}>
          <Box overflowX="hidden" {...modalBodyBoxStyle}>
            {renderModalContent()}
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}
