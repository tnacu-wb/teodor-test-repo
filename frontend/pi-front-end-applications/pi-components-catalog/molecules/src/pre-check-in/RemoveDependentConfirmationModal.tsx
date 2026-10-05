import { Flex, Heading, Button } from '@chakra-ui/react';
import { ModalVariants } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

import {
  headingStyles,
  modalButtonsContainerStyles,
  modalButtonsStyles,
  modalContentStyles,
  subHeadingStyles,
} from './common';

interface Props {
  isOpen: boolean;
  onClose: () => void;
  handleDependentDelete: () => void;
}

function RemoveDependentConfirmationModal({
  isOpen,
  onClose,
  handleDependentDelete,
}: Readonly<Props>) {
  const { t } = useTranslation();

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={onClose}
      variant="default"
      variantProps={{ title: '', delimiter: false }}
      headerStyles={{ textAlign: 'center', justifyContent: 'flex-end' }}
      updatedWidth={{ sm: '100%', md: '37.5rem', lg: '37.5rem', xl: '39.93rem' }}
    >
      <Flex {...modalContentStyles}>
        <Heading as="h2" {...headingStyles}>
          {t('precheckin.dependants.warningpopup')}
        </Heading>
        <Heading as="h5" {...subHeadingStyles}>
          {t('precheckin.dependants.warningpopup.desc')}
        </Heading>
        <Flex {...modalButtonsContainerStyles} marginTop={'lg'} gap="lg">
          <Button
            data-testid="PreCheckIn-ModalDenyButton"
            size="xsm"
            variant="tertiary"
            onClick={onClose}
            {...modalButtonsStyles}
          >
            {t('precheckin.details.cancelbtn')}
          </Button>
          <Button
            data-testid="PreCheckIn-ModalConfirmationButton"
            size="xsm"
            onClick={handleDependentDelete}
            {...modalButtonsStyles}
          >
            {t('precheckin.button.yes')}
          </Button>
        </Flex>
      </Flex>
    </ModalVariants>
  );
}

export default RemoveDependentConfirmationModal;
