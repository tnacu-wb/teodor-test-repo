import {
  Box,
  ButtonProps,
  Flex,
  FlexProps,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalContentProps,
  ModalHeader,
  ModalHeaderProps,
  ModalOverlay,
  ModalProps,
  StyleProps,
} from '@chakra-ui/react';
import { useScreenSize } from '@whitbread-eos/utils';
import { ReactNode, RefObject } from 'react';

import { Dismiss } from '../../../../assets/icons';
import Icon from '../../../Icon';

export interface DefaultModalVariantProps {
  title: string;
  overflowVisible?: boolean;
  isCentered?: boolean;
  externalTitleStyling?: FlexProps;
  sizeSm?: string;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  children: ReactNode;
  dataTestId?: string;
  variantProps?: DefaultModalVariantProps;
  headerStyles?: ModalHeaderProps;
  contentContainerStyles?: StyleProps;
  overlayStyles?: StyleProps;
  initialFocusRef?: RefObject<HTMLElement>;
  updatedWidth?: {
    mobile?: string;
    xs?: string;
    sm?: string;
    md?: string;
    lg?: string;
    xl?: string;
  };
}

export default function DefaultModal({
  onClose,
  isOpen,
  children,
  variantProps,
  dataTestId,
  updatedWidth,
  overlayStyles,
  contentContainerStyles,
  headerStyles: externalHeaderStyles,
  initialFocusRef,
  ...otherProps
}: Readonly<Props>) {
  const prefix = dataTestId ? `${dataTestId}-` : '';
  const shouldAppearInFront = variantProps?.overflowVisible ?? false;
  const isModalCentered = !!variantProps?.isCentered;
  const width = { mobile: 'full', sm: 'auto', ...updatedWidth };
  const { isLessThanMd } = useScreenSize();

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      preserveScrollBarGap
      isCentered={isModalCentered}
      scrollBehavior="inside"
      isOpen={isOpen}
      onClose={onClose}
      initialFocusRef={initialFocusRef}
      trapFocus={true}
      blockScrollOnMount={true}
      returnFocusOnClose={true}
      {...otherProps}
      size={isLessThanMd && variantProps?.sizeSm ? 'full' : 'md'}
    >
      <ModalOverlay {...overlayStyles} />
      <ModalContent
        containerProps={contentContainerStyles}
        data-testid={`${prefix}ModalContent`}
        {...modalContentStyle(
          shouldAppearInFront,
          isModalCentered,
          variantProps?.sizeSm,
          isLessThanMd
        )}
        width={otherProps.size ? 'auto' : width}
      >
        <ModalHeader
          {...modalHeaderStyle}
          {...externalHeaderStyles}
          data-testid={`${prefix}ModalHeader`}
        >
          {variantProps && (
            <Flex
              {...headerTitleStyle}
              {...variantProps?.externalTitleStyling}
              data-testid={`${prefix}ModalTitle`}
            >
              {variantProps.title}
            </Flex>
          )}
          <ModalCloseButton data-testid={`${prefix}ModalCloseButton`} {...modalCloseButtonStyle}>
            <Icon svg={<Dismiss transform="scale(1.1)" />} />
          </ModalCloseButton>
        </ModalHeader>
        <Box {...delimiterModalStyle} />
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

const modalContentStyle = (
  overflowVisible: boolean,
  isModalCentered: boolean,
  sizeSm: string | undefined,
  isLessThanMd: boolean | undefined
) => {
  return {
    maxW: 'auto',
    maxH: 'auto',
    overflow: overflowVisible ? 'visible' : 'auto',
    height: { mobile: 'full', sm: 'fit-content' },
    my: 0,
    borderRadius: { mobile: '0', sm: isLessThanMd && sizeSm ? 0 : '0.875rem' },
    boxShadow: '0 0.125rem 0.75rem var(--chakra-colors-darkGrey2)',
    p: 0,
    top: isModalCentered
      ? 0
      : { mobile: 0, sm: sizeSm ? 0 : '2.938rem', md: '3.75rem', xl: '6.375rem' },
  } as ModalContentProps;
};

const modalHeaderStyle = {
  minH: 'var(--chakra-space-xxl)',
  display: 'grid',
  gridTemplateColumns: '1fr auto',
  alignItems: 'center',
  py: 'sm',
  px: { mobile: 'md', sm: 'lg' },
};

const delimiterModalStyle = {
  mx: { mobile: 'md', sm: 'lg' },
  h: '0.063rem',
  bgColor: 'lightGrey4',
};

const headerTitleStyle = {
  ml: 'xl',
  color: 'darkGrey1',
  justify: 'center',
  justifyContent: 'center',
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
  padding: 0,
};

const modalCloseButtonStyle = {
  h: 'var(--chakra-space-xl)',
  w: 'var(--chakra-space-xl)',
  position: 'static',
  alignSelf: 'flex-end',
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
