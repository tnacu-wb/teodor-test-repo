import {
  Box,
  Flex,
  Heading,
  Icon,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  ModalFooter,
  Text,
  Button,
  Image,
  type ModalProps,
  FlexProps,
} from '@chakra-ui/react';

import { Dismiss } from '../../../../assets/icons';
import LoadingSpinner from '../../../LoadingSpinner';

interface ModalHeaderProps {
  title: string;
  priceText?: string;
  prefix: string;
  isPrimary?: boolean;
}

const ModalHeaderContent = ({ title, priceText, prefix, isPrimary }: ModalHeaderProps) => (
  <Flex {...(isPrimary ? { ...styles.header, p: 0 } : { ...styles.header })}>
    <Box>
      <Heading {...styles.title}>{title}</Heading>
      {priceText && <Text {...styles.priceText}>{priceText}</Text>}
    </Box>
    <ModalCloseButton data-testid={`${prefix}ModalCloseButton`} {...styles.modalCloseButton}>
      <Icon as={Dismiss} {...styles.closeIcon} />
    </ModalCloseButton>
  </Flex>
);

export interface RoomUpgradeModalVariantProps {
  title: string;
  description?: string;
  imageSrc?: string;
  primaryButtonLabel?: string;
  secondaryButtonLabel?: string;
  priceText?: string;
  onPrimaryAction?: () => void;
  onSecondaryAction?: () => void;
}

export interface Props extends ModalProps {
  isOpen: boolean;
  onClose: () => void;
  dataTestId?: string;
  variantProps?: RoomUpgradeModalVariantProps;
  isLoading?: boolean;
  landscapeModal?: {
    primary?: boolean;
    secondary?: boolean;
  };
}

const RoomUpgradeModal = ({
  isOpen,
  onClose,
  dataTestId = '',
  variantProps,
  isLoading,
  landscapeModal,
  ...modalProps
}: Readonly<Props>) => {
  const prefix = dataTestId ? `${dataTestId}-` : '';

  const { primary, secondary } = landscapeModal ?? {};

  const spinner = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
  } as FlexProps;

  return (
    <Modal
      data-testid={`${prefix}ChakraModal`}
      scrollBehavior="inside"
      isOpen={isOpen}
      onClose={onClose}
      closeOnOverlayClick={false}
      isCentered
      motionPreset="slideInBottom"
      size={primary ? 'xxl' : 'lg'}
      {...modalProps}
    >
      <ModalOverlay />

      {!primary && !secondary && (
        <>
          <ModalContent data-testid={`${prefix}ModalContent`} minH="250px" {...modalStyles}>
            <ModalHeader p="0" data-testid={`${prefix}ModalHeader`}>
              {variantProps?.title && variantProps?.priceText && (
                <ModalHeaderContent
                  title={variantProps.title}
                  priceText={variantProps.priceText}
                  prefix={prefix}
                  isPrimary={primary}
                />
              )}
            </ModalHeader>
            <ModalBody p="0" data-testid={`${prefix}ModalBody`} {...contentStyles}>
              <Flex direction="column" px={4} py={1}>
                {variantProps?.imageSrc && (
                  <Image
                    {...imageStyles}
                    src={variantProps.imageSrc}
                    alt={variantProps.title}
                    mb={2}
                    borderRadius="8px"
                    objectFit="cover"
                  />
                )}
                {variantProps?.description && <Box>{variantProps.description}</Box>}
                {isLoading && (
                  <Box {...spinner}>
                    <LoadingSpinner />
                  </Box>
                )}
              </Flex>
            </ModalBody>

            <ModalFooter p="4">
              {variantProps && (
                <Flex direction="column" gap={2} w="full" justify="center">
                  {variantProps.primaryButtonLabel && (
                    <Button
                      {...styles.button}
                      onClick={variantProps.onPrimaryAction}
                      variant="primary"
                      flex={1}
                      size="full"
                      data-testid={`${prefix}PrimaryButton`}
                    >
                      {variantProps.primaryButtonLabel}
                    </Button>
                  )}
                  {variantProps.secondaryButtonLabel && (
                    <Button
                      {...styles.button}
                      onClick={variantProps.onSecondaryAction}
                      variant="tertiary"
                      flex={1}
                      size="full"
                      data-testid={`${prefix}SecondaryButton`}
                    >
                      {variantProps.secondaryButtonLabel}
                    </Button>
                  )}
                </Flex>
              )}
            </ModalFooter>
          </ModalContent>
        </>
      )}

      {(primary || secondary) && (
        <>
          <ModalContent
            data-testid={`${prefix}ModalContent`}
            minH={secondary ? 'none' : '250px'}
            maxH="317px"
            ml={{
              mobile: '1rem',
              sm: '0',
            }}
            mr={{
              mobile: '1rem',
              sm: '0',
            }}
            {...(primary ? { ...modalStylesLandscapeDefault } : { ...modalStylesLandscapeVariant })}
            flexDirection="row"
          >
            {!secondary && (
              <Box w="50%" p="4">
                {variantProps?.imageSrc && (
                  <Image
                    {...imageStylesLandscape}
                    src={variantProps.imageSrc}
                    alt={variantProps.title}
                    mb={2}
                    borderRadius="8px"
                    objectFit="cover"
                    h="100%"
                  />
                )}
              </Box>
            )}

            <Box w={secondary ? '100%' : '50%'} p="4">
              <Flex direction="column" height="100%" justifyContent="space-between">
                <ModalHeader p="0" data-testid={`${prefix}ModalHeader`}>
                  {variantProps?.title && variantProps?.priceText && (
                    <ModalHeaderContent
                      title={variantProps.title}
                      priceText={variantProps.priceText}
                      prefix={prefix}
                      isPrimary={primary}
                    />
                  )}
                </ModalHeader>
                <ModalBody p="0" data-testid={`${prefix}ModalBody`} {...contentStyles}>
                  {variantProps?.description && (
                    <Box maxH={secondary ? '40px' : '77px'}>{variantProps.description}</Box>
                  )}
                  {isLoading && (
                    <Box {...spinner}>
                      <LoadingSpinner />
                    </Box>
                  )}
                </ModalBody>
                <ModalFooter p="0" mt="2">
                  {variantProps && (
                    <Flex
                      direction={secondary ? 'row' : 'column'}
                      gap={2}
                      w="full"
                      justify="center"
                    >
                      {variantProps.primaryButtonLabel && (
                        <Button
                          {...(secondary
                            ? {
                                ...styles.buttonLandscape,
                                fontSize: 'md',
                                padding: 2,
                                height: '40px',
                              }
                            : { ...styles.buttonLandscape })}
                          onClick={variantProps.onPrimaryAction}
                          variant="primary"
                          flex={1}
                          size="full"
                          data-testid={`${prefix}PrimaryButton`}
                        >
                          {variantProps.primaryButtonLabel}
                        </Button>
                      )}
                      {variantProps.secondaryButtonLabel && (
                        <Button
                          {...(secondary
                            ? {
                                ...styles.buttonLandscape,
                                fontSize: 'md',
                                padding: 2,
                                height: '40px',
                              }
                            : { ...styles.buttonLandscape })}
                          onClick={variantProps.onSecondaryAction}
                          variant="tertiary"
                          flex={1}
                          size="full"
                          data-testid={`${prefix}SecondaryButton`}
                        >
                          {variantProps.secondaryButtonLabel}
                        </Button>
                      )}
                    </Flex>
                  )}
                </ModalFooter>
              </Flex>
            </Box>
          </ModalContent>
        </>
      )}
    </Modal>
  );
};

const styles = {
  modalCloseButton: {
    h: 'var(--chakra-space-xl)',
    w: 'var(--chakra-space-xl)',
    ml: 'auto',
    position: 'static' as const,
    _focus: { boxShadow: 'none' },
    _hover: { bgColor: 'transparent' },
    _active: { bgColor: 'transparent' },
  },
  header: {
    p: 4,
    pb: 2,
    justify: 'space-between',
    align: 'baseline',
  },
  title: {
    as: 'h2' as const,
    size: 'md',
    color: '#511E62',
    fontWeight: '900',
  },
  priceText: {
    fontSize: 'sm',
    color: '#333333',
    fontWeight: '600',
    mt: '5px',
  },
  closeIcon: {
    transform: 'scale(1.1)',
    stroke: '#511E62',
  },
  button: {
    fontSize: {
      mobile: 'md',
      lg: 'lg',
    },
    padding: {
      mobile: 2,
      lg: 4,
    },
    height: {
      mobile: '40px',
      lg: '56px',
    },
  },
  buttonLandscape: {
    fontSize: 'lg',
    padding: 4,
    height: '56px',
  },
  buttonLandscapeCommon: {
    fontSize: 'lg',
    padding: 4,
    height: '56px',
  },
} as const;

const contentStyles = {
  sx: {
    ul: {
      ml: 'md',
    },
  },
  fontSize: 'sm',
  lineHeight: '20px',
} as const;

const modalStyles = {
  w: {
    mobile: '287px',
    lg: '410px',
  },
  ml: {
    mobile: '1rem',
    sm: '0',
  },
  mr: {
    mobile: '1rem',
    sm: '0',
  },
  marginTop: '1.5rem',
  maxHeight: {
    mobile: '470px',
    lg: '503px',
  },
};

const modalStylesLandscapeDefault = {
  w: '812px',
  marginTop: 'auto',
  maxHeight: {
    mobile: '470px',
    lg: '317px',
  },
};

const modalStylesLandscapeVariant = {
  w: {
    mobile: '287px',
    lg: '737px',
  },
  marginTop: '1.5rem',
  maxHeight: {
    mobile: '470px',
    lg: '503px',
  },
};

const imageStyles = {
  maxHeight: {
    mobile: '100%',
    md: '140px',
    lg: '190px',
  },
};

const imageStylesLandscape = {
  maxHeight: {
    mobile: '100%',
    md: 'full',
  },
};

export default RoomUpgradeModal;
