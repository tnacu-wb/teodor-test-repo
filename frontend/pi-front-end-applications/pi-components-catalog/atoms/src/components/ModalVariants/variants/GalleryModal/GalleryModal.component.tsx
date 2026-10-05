import {
  Box,
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
} from '@chakra-ui/react';
import { ReactNode } from 'react';

import { Dismiss } from '../../../../assets/icons';
import Icon from '../../../Icon';

export interface GalleryModalVariantProps {
  title: string;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: GalleryModalVariantProps;
}

export default function GalleryModal({
  onClose,
  isOpen,
  children,
  variantProps,
  dataTestId,
  ...otherProps
}: Readonly<Props>) {
  const prefix = dataTestId ? `${dataTestId}-` : '';

  const isInContainer = !!otherProps.portalProps?.containerRef;

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      scrollBehavior="inside"
      isCentered
      isOpen={isOpen}
      onClose={onClose}
      {...otherProps}
    >
      <ModalOverlay {...(isInContainer && { width: '100%', height: '100%' })} />
      <ModalContent
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle(isInContainer)}
        width={isInContainer ? 'auto' : otherProps.size ? 'auto' : { mobile: 'full', lg: 'auto' }}
        height={isInContainer ? 'fit-content' : { mobile: 'full', lg: 'fit-content' }}
        {...(isInContainer && {
          containerProps: { style: { width: '100%', height: '100%' } },
          mx: '1.5rem',
        })}
      >
        <ModalHeader p="0" data-testid={`${prefix}ModalHeader`}>
          <Flex flexDir="column">
            <Grid {...headerContentStyle}>
              {variantProps && (
                <Flex {...headerTitleStyle} data-testid={`${prefix}ModalTitle`}>
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
            <Box {...delimiterModalStyle} />
          </Flex>
        </ModalHeader>
        <ModalBody data-testid={`${prefix}ModalBody`} {...modalBodyStyle}>
          <Box overflow="auto" maxH="calc(100vh - 4rem)">
            {children}
          </Box>
        </ModalBody>
      </ModalContent>
    </Modal>
  );
}

const modalBodyStyle = {
  overflow: 'auto',
  p: 0,
};

const modalContentStyle = (isInContainer: boolean) =>
  ({
    maxW: 'auto',
    maxH: 'auto',
    overflow: 'auto',
    height: { mobile: 'full', lg: 'fit-content' },
    my: 0,
    borderRadius: { mobile: isInContainer ? '0.875rem' : '0', lg: '0.875rem' },
    boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
    px: '0',
  }) as ModalContentProps;

const headerContentStyle = {
  m: 'md',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
};

const delimiterModalStyle = {
  w: 'full',
  h: '0.063rem',
  bgColor: 'lightGrey4',
  display: { mobile: 'block', lg: 'none' },
};

const headerTitleStyle = {
  color: 'darkGrey1',
  justifyContent: 'center',
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
  ml: 'xl',
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
