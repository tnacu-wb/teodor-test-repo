import {
  Modal,
  ModalBody,
  ModalContent,
  ModalContentProps,
  ModalHeader,
  ModalHeaderProps,
  ModalOverlay,
  ModalProps,
  Text,
} from '@chakra-ui/react';
import { useScreenSize } from '@whitbread-eos/utils';
import { ReactNode } from 'react';

export interface CookieModalVariantProps {
  title: string;
  closeOnOverlayClick?: boolean;
  isCookieConsentModal?: boolean;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: CookieModalVariantProps;
  hasAutoFocus?: boolean;
  headerStyles?: ModalHeaderProps;
}

export default function CookieModal({
  onClose,
  isOpen,
  children,
  variantProps,
  dataTestId,
  hasAutoFocus = true,
  headerStyles,
  ...otherProps
}: Readonly<Props>) {
  const prefix = dataTestId ? `${dataTestId}-` : '';
  const { isLessThanMobile } = useScreenSize();

  const compModalHeaderStyles = headerStyles
    ? { ...modalHeaderStyle, ...headerStyles }
    : { ...modalHeaderStyle };

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      preserveScrollBarGap
      scrollBehavior="inside"
      isOpen={isOpen}
      onClose={onClose}
      {...otherProps}
      autoFocus={hasAutoFocus}
      closeOnEsc={variantProps?.closeOnOverlayClick}
      closeOnOverlayClick={variantProps?.closeOnOverlayClick}
      size={isLessThanMobile ? 'full' : 'md'}
    >
      <ModalOverlay />
      <ModalContent
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle(variantProps?.isCookieConsentModal)}
        width={otherProps.size ? 'auto' : { mobile: 'full', sm: 'auto' }}
      >
        <ModalHeader {...compModalHeaderStyles} data-testid={`${prefix}ModalHeader`}>
          {variantProps && (
            <Text {...headerTitleStyle} data-testid={`${prefix}ModalTitle`}>
              {variantProps.title}
            </Text>
          )}
        </ModalHeader>
        <ModalBody data-testid={`${prefix}ModalBody`} {...modalBodyStyle}>
          {children}
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}

const modalContentStyle = (isCookieConsentModal: boolean | undefined) => {
  return {
    maxW: 'auto',
    maxH: 'auto',
    overflow: 'auto',
    height: { mobile: 'full', sm: 'fit-content' },
    maxHeight: {
      mobile: 'full',
      sm: 'calc(100% - 6rem)',
    },
    top: {
      mobile: 0,
      sm: '10%',
      lg: isCookieConsentModal && '10%',
    },
    m: '0',
    mt: {
      mobile: '0',
      sm: '2xl',
    },
    mb: {
      mobile: '0',
      sm: '2xl',
    },
    borderRadius: { mobile: '0', sm: '0.875rem' },
    boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
  } as ModalContentProps;
};

const modalHeaderStyle = {
  pb: 0,
  pt: 'lg',
  px: 'lg',
};

const headerTitleStyle = {
  color: 'darkGrey1',
  justifyContent: 'center',
  fontSize: {
    mobile: '2xl',
    md: '3xl',
  },
  lineHeight: '4',
  fontWeight: 'semibold',
  fontFamily: 'header',
};

const modalBodyStyle = {
  p: '0',
};
