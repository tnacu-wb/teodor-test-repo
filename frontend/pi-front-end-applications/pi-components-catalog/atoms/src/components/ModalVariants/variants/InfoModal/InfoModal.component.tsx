import {
  Box,
  BoxProps,
  ButtonProps,
  Flex,
  Grid,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalContentProps,
  ModalHeader,
  ModalOverlay,
  ModalProps,
  ModalHeaderProps,
  StyleProps,
} from '@chakra-ui/react';
import { ReactNode } from 'react';

import { Dismiss } from '../../../../assets/icons';
import Icon from '../../../Icon';

export interface InfoModalVariantProps {
  title: string;
  delimiter?: boolean;
  headerIcon?: React.JSX.Element;
  externalHeaderContentStyle?: BoxProps;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  closeOnOverlayClick?: boolean;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: InfoModalVariantProps;
  headerStyles?: ModalHeaderProps;
  headerContentStyles?: StyleProps;
}

export default function InfoModal({
  onClose,
  isOpen,
  closeOnOverlayClick = true,
  children,
  variantProps,
  dataTestId,
  headerStyles: externalHeaderStyles,
  headerContentStyles: externalHeaderContentStyle,
  ...otherProps
}: Readonly<Props>) {
  const isInContainer = !!otherProps.portalProps?.containerRef;

  const prefix = dataTestId ? `${dataTestId}-` : '';

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      scrollBehavior="inside"
      isCentered
      isOpen={isOpen}
      onClose={onClose}
      closeOnOverlayClick={closeOnOverlayClick}
      {...otherProps}
    >
      <ModalOverlay {...(isInContainer && { width: '100%', height: '100%' })} />
      <ModalContent
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle(isInContainer)}
        width={otherProps.size ? 'auto' : { mobile: isInContainer ? 'auto' : 'full', sm: 'auto' }}
        height={isInContainer ? 'fit-content' : { mobile: 'full', sm: 'auto' }}
        {...(isInContainer && {
          containerProps: { style: { width: '100%', height: '100%' } },
        })}
      >
        <ModalHeader p="lg" data-testid={`${prefix}ModalHeader`} {...externalHeaderStyles}>
          <Flex flexDir="column">
            <Grid {...headerContentStyle} {...externalHeaderContentStyle}>
              {variantProps && (
                <Flex {...headerTitleStyle} data-testid={`${prefix}ModalTitle`}>
                  {variantProps.headerIcon && (
                    <Icon
                      svg={variantProps.headerIcon}
                      {...variantProps.externalHeaderContentStyle}
                    />
                  )}
                  {variantProps.title}
                </Flex>
              )}
              <ModalCloseButton
                data-testid={`${prefix}ModalCloseButton`}
                {...modalCloseButtonStyle}
              >
                <Icon svg={<Dismiss transform="scale(1.1)" />} />
              </ModalCloseButton>
            </Grid>
            {variantProps?.delimiter && (
              <Box data-testid={`${prefix}Delimiter`} {...delimiterModalStyle} />
            )}
          </Flex>
        </ModalHeader>
        <ModalBody data-testid={`${prefix}ModalBody`} {...modalBodyStyle}>
          <Box overflowX="hidden" {...modalBodyBoxStyle}>
            {children}
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}

const modalBodyStyle = {
  overflow: { mobile: 'hidden', sm: 'auto' },
  p: 0,
};

const modalBodyBoxStyle = {
  maxHeight: { mobile: 'calc(96vh - 4rem)', sm: '66vh' },
};

const modalContentStyle = (isInContainer: boolean) =>
  ({
    maxW: 'auto',
    maxH: 'auto',
    overflow: 'auto',
    height: { mobile: 'full', sm: 'fit-content' },
    my: 0,
    borderRadius: { mobile: isInContainer ? '0.875rem' : '0', sm: '0.875rem' },
    boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
    px: '0',
  }) as ModalContentProps;

const headerContentStyle = {
  my: 'md',
  mx: 'sm',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
};

const delimiterModalStyle = {
  w: 'full',
  h: '0.063rem',
  bgColor: 'lightGrey4',
};

const headerTitleStyle = {
  color: 'darkGrey1',
  justifyContent: 'flex-start',
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
  ml: 'sm',
  padding: 0,
};

const modalCloseButtonStyle = {
  h: 'var(--chakra-space-xl)',
  w: 'var(--chakra-space-xl)',
  ml: 'auto',
  position: 'static',
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
