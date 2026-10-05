import {
  Box,
  ButtonProps,
  Text,
  FlexProps,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalContentProps,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  ModalProps,
  TextProps,
} from '@chakra-ui/react';
import { ReactElement, ReactNode, RefObject } from 'react';

import { Dismiss } from '../../../../assets/icons';
import Icon from '../../../Icon';

interface ResponsiveStyle {
  mobile?: string;
  xs?: string;
  sm?: string;
  md?: string;
  lg?: string;
  xl?: string;
}

export interface DataModalvariantProps {
  title: string;
  finalFocusRef?: RefObject<any>;
  modalHeight?: ResponsiveStyle;
  header?: ReactElement;
  footer?: string | ReactElement;
  externalFooterStyling?: FlexProps;
  externalTitleStying?: TextProps;
  showDelimiter?: boolean;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: DataModalvariantProps;
  updatedWidth?: ResponsiveStyle;
}

export default function DataModal({
  onClose,
  isOpen,
  children,
  variantProps,
  dataTestId,
  updatedWidth,
  ...otherProps
}: Readonly<Props>) {
  const prefix = dataTestId ? `${dataTestId}-` : '';
  const width = { mobile: 'full', ...updatedWidth };
  const height = { mobile: 'full', ...variantProps?.modalHeight };
  const { showDelimiter = true } = variantProps ?? {};

  return (
    <Modal
      isOpen={isOpen}
      data-testid={`${prefix}ChakraModal`}
      preserveScrollBarGap
      isCentered
      closeOnOverlayClick={false}
      scrollBehavior="inside"
      onClose={onClose}
      finalFocusRef={variantProps?.finalFocusRef}
      size="md"
      {...otherProps}
    >
      <ModalOverlay />
      <ModalContent
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle}
        width={width}
        height={height}
      >
        <ModalHeader data-testid={`${prefix}ModalHeader`} {...modalHeaderStyle}>
          {variantProps?.header ??
            (variantProps?.title && (
              <Text
                {...headerTitleStyle}
                data-testid={`${prefix}ModalTitle`}
                {...variantProps?.externalTitleStying}
              >
                {variantProps.title}
              </Text>
            ))}
          <ModalCloseButton data-testid={`${prefix}ModalCloseIcon`} {...modalCloseButtonStyle}>
            <Icon svg={<Dismiss transform="scale(1.1)" />} />
          </ModalCloseButton>
        </ModalHeader>
        {showDelimiter && <Box {...delimiterModalStyle} />}
        <ModalBody data-testid={`${prefix}ModalBody`} {...modalBodyStyle}>
          {children}
        </ModalBody>
        {variantProps?.footer && (
          <ModalFooter
            data-testid={`${prefix}ModalFooter`}
            {...modalFooterStyle}
            {...variantProps?.externalFooterStyling}
          >
            {variantProps?.footer}
          </ModalFooter>
        )}
      </ModalContent>
    </Modal>
  );
}

const modalContentStyle: ModalContentProps = {
  maxW: { mobile: 'full', lg: 'calc(100vw - var(--chakra-space-12))' },
  maxH: { mobile: 'full', lg: 'calc(100vh - var(--chakra-space-12))' },
  overflow: 'auto',
  borderRadius: { mobile: '0', lg: '0.875rem' },
  boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
  p: 0,
};

const modalHeaderStyle = {
  minH: 'var(--chakra-space-xxl)',
  display: 'grid',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
  margin: 0,
  py: 'var(--chakra-space-5)',
  px: { mobile: 'md', sm: 'lg' },
  gap: 'var(--chakra-space-6)',
};

const delimiterModalStyle = {
  h: '0.063rem',
  bgColor: 'lightGrey4',
};

const headerTitleStyle = {
  color: 'darkGrey1',
  justify: 'center',
  justifyContent: 'flex-start',
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
  padding: 0,
  margin: 0,
};

const modalBodyStyle = {
  overflow: 'auto',
  p: 0,
};

const modalCloseButtonStyle = {
  h: 'var(--chakra-space-xl)',
  w: 'var(--chakra-space-xl)',
  position: 'static',
  alignSelf: 'center',
  fontSize: 'xs',
  lineHeight: '3',
  fontWeight: '400',
  color: 'darkGrey2',
  _focus: {
    boxShadow: 'none',
  },
  _hover: {
    bgColor: 'transparent',
  },
  _active: {
    bgColor: 'transparent',
  },
} as ButtonProps;

const modalFooterStyle = {
  justifyContent: 'flex-start',
  overflow: 'auto',
};
