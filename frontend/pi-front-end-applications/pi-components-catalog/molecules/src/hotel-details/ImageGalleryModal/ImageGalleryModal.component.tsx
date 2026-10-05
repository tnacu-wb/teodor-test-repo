import {
  Box,
  HStack,
  Modal,
  Text,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  TextProps,
  BoxProps,
} from '@chakra-ui/react';
import { ArrowLeft, Icon } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

interface ImageGalleryModalProps {
  isModalOpen: boolean;
  onModalClose: () => void;
}

const ImageGalleryModal = ({ isModalOpen, onModalClose }: ImageGalleryModalProps) => {
  const { t } = useTranslation();

  const modalBody = <Box />;

  return (
    <Modal
      isOpen={isModalOpen}
      onClose={onModalClose}
      size="full"
      closeOnEsc
      closeOnOverlayClick={false}
      scrollBehavior="inside"
      blockScrollOnMount
      data-testid="ImageGalleryModal"
    >
      <ModalOverlay />
      <ModalContent data-testid="ImageGalleryModal-Content">
        <ModalHeader flexShrink={0} p={0} data-testid="ImageGalleryModal-Header">
          <Box bg="lightGrey5" py="xlg" px={{ base: 'xlg', md: 'lg', lg: '5xl' }}>
            <HStack gap="sm" alignItems="center" onClick={onModalClose} cursor="pointer">
              <Icon svg={<ArrowLeft />} {...headerIconProps} />
              <Text {...headerTextProps}>{t('hdp.imageGallery.modal.back')}</Text>
            </HStack>
          </Box>
        </ModalHeader>

        <ModalBody flex={1} data-testid="ImageGalleryModal-Body">
          {modalBody}
        </ModalBody>
      </ModalContent>
    </Modal>
  );
};

const headerIconProps: BoxProps = {
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  w: 'var(--chakra-sizes-6)',
  h: 'var(--chakra-sizes-6)',
};

const headerTextProps: TextProps = {
  fontWeight: 400,
  fontSize: { base: 'sm', sm: 'md' },
  lineHeight: { base: '2', sm: '3' },
};

export default ImageGalleryModal;
