import {
  Box,
  Flex,
  FlexProps,
  Modal,
  ModalBody,
  ModalContent,
  ModalContentProps,
  ModalFooter,
  ModalFooterProps,
  ModalHeader,
  ModalHeaderProps,
  ModalOverlay,
  ModalProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { ReactElement, ReactNode } from 'react';

import { ArrowLeft } from '../../../../assets/icons';
import Icon from '../../../Icon';

export interface LoginModalVariantProps {
  title: string;
  goBackButtonText?: string;
  onGoBack?: () => void;
  footer?: string | ReactElement;
  footerStyles?: ModalFooterProps;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  closeOnOverlayClick?: boolean;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: LoginModalVariantProps;
  hasAutoFocus?: boolean;
  headerStyles?: ModalHeaderProps;
}

export default function LoginModal({
  onClose,
  isOpen,
  closeOnOverlayClick,
  children,
  variantProps,
  dataTestId,
  hasAutoFocus = false,
  headerStyles,
  ...otherProps
}: Readonly<Props>) {
  const prefix = dataTestId ? `${dataTestId}-` : '';

  const compModalHeaderStyles = headerStyles
    ? { ...modalHeaderStyle, ...headerStyles }
    : { ...modalHeaderStyle };
  const compModalFooterStyles = variantProps?.footerStyles
    ? { ...modalFooterStyle, ...variantProps?.footerStyles }
    : { ...modalFooterStyle };

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      scrollBehavior="inside"
      isOpen={isOpen}
      onClose={onClose}
      {...otherProps}
      autoFocus={hasAutoFocus}
      closeOnEsc={closeOnOverlayClick}
      closeOnOverlayClick={closeOnOverlayClick}
    >
      <ModalOverlay />
      <ModalContent
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle}
        width={otherProps.size ? 'auto' : { mobile: 'full', sm: 'auto' }}
      >
        <ModalHeader {...compModalHeaderStyles} data-testid={`${prefix}ModalHeader`}>
          {variantProps && (
            <>
              {variantProps?.onGoBack && variantProps?.goBackButtonText && (
                <Flex
                  {...modalGoBackBtnStyles}
                  onClick={variantProps.onGoBack}
                  data-testid={`${prefix}ModalGoBackButton`}
                >
                  <Icon svg={<ArrowLeft />} />
                  <Text {...modalGoBackTextStyles}>{variantProps.goBackButtonText}</Text>
                </Flex>
              )}
              <Text {...headerTitleStyle(variantProps?.title)} data-testid={`${prefix}ModalTitle`}>
                {variantProps.title}
              </Text>
            </>
          )}
        </ModalHeader>
        <Box {...delimiterModalStyle} />
        <ModalBody data-testid={`${prefix}ModalBody`} {...modalBodyStyle}>
          <Box overflow="auto" maxH="calc(100vh - 9rem)">
            {children}
          </Box>
        </ModalBody>
        {variantProps?.footer && (
          <ModalFooter data-testid={`${prefix}ModalFooter`} {...compModalFooterStyles}>
            {variantProps?.footer}
          </ModalFooter>
        )}
      </ModalContent>
    </Modal>
  );
}

const modalBodyStyle = {
  overflow: 'auto',
  p: '0',
};

const modalContentStyle = {
  maxW: 'auto',
  maxH: 'auto',
  overflow: 'auto',
  height: { mobile: 'full', sm: 'fit-content' },
  top: {
    mobile: '0',
    sm: '2.938rem',
    md: '3.75rem',
    xl: '6.375rem',
  },
  my: '0',
  p: 0,
  borderRadius: { mobile: '0', sm: '0.875rem' },
  boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
} as ModalContentProps;

const modalHeaderStyle = {
  minH: 'var(--chakra-space-xxl)',
  display: 'flex',
  alignItems: 'center',
  py: 'sm',
  px: { mobile: 'md', sm: 'lg' },
};

const delimiterModalStyle = {
  mx: { mobile: 'md', sm: 'lg' },
  h: '0.063rem',
  bgColor: 'lightGrey4',
};

const headerTitleStyle = (title: string) => {
  return {
    ml: 'xl',
    color: 'darkGrey1',
    justify: 'center',
    justifyContent: 'center',
    fontSize: 'lg',
    lineHeight: '3',
    fontFamily: 'header',
    padding: 0,
    minHeight: title ? 'initial' : '2rem',
  };
};

const modalFooterStyle = {
  justifyContent: 'flex-start',
  overflow: 'auto',
  py: 'sm',
  px: { mobile: 'md', sm: 'lg' },
};

const modalGoBackBtnStyles = {
  alignItems: 'center',
  w: 'fit-content',
  justifyContent: 'flex-start',
  cursor: 'pointer',
  py: '2',
} as FlexProps;

const modalGoBackTextStyles = {
  pl: 'sm',
  fontWeight: 'normal',
  fontSize: 'md',
  color: 'darkGrey1',
  lineHeight: '3',
} as TextProps;
