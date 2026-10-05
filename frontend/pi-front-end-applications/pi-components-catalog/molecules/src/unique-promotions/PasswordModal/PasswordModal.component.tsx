import { Box, HStack, Text } from '@chakra-ui/react';
import { ModalVariants } from '@whitbread-eos/atoms';
import { usePromoTranslation } from '@whitbread-eos/utils';
import { Check, Copy } from 'lucide-react';
import { useEffect, useState } from 'react';

import { usePromoTableContext } from '../List/usePromoBatchesTableContext';
import { PASSWORD_DURATION, TimerStatus } from './common';
import { styles } from './styles';

const PasswordModal = () => {
  const { oneTimePasswordOpenFileText, downloadFileText, accessFileInstruction } =
    usePromoTranslation();
  const { showModal, setShowModal, batchIdData } = usePromoTableContext();
  const [copied, setCopied] = useState(false);
  const [timeLeft, setTimeLeft] = useState(PASSWORD_DURATION);
  const isExpired = timeLeft <= 0;

  const onClose = () => setShowModal(false);

  const copyToClipboard = async (text: string) => {
    await navigator.clipboard.writeText(text);

    setCopied(true);

    setTimeout(() => {
      setCopied(false);
    }, 2000);
  };

  useEffect(() => {
    if (!showModal && !batchIdData) return;
    setTimeLeft(PASSWORD_DURATION);
    const interval = setInterval(() => {
      setTimeLeft((prev) => (prev <= 1 ? 0 : prev - 1));
    }, 1000);

    return () => clearInterval(interval);
  }, [showModal]);

  return (
    <ModalVariants
      variant="info"
      isOpen={showModal as unknown as boolean}
      onClose={onClose}
      contentContainerStyles={{
        overflow: 'auto',
      }}
      variantProps={{
        title: '',
        isCentered: true,
        delimiter: false,
      }}
      headerStyles={{ p: '0' }}
      updatedWidth={{ xs: '90%', md: '70%', lg: '45%' }}
    >
      <Box sx={styles.passwordContainerWrap}>
        <HStack sx={styles.downloadFileWrapStyles}>
          <Check size={20} />
          <Text sx={styles.downloadFileTextStyles}>{downloadFileText}</Text>
        </HStack>

        <Text sx={styles.titleStyles}>{oneTimePasswordOpenFileText}</Text>
        <Text sx={styles.descriptionStyles}>{accessFileInstruction}</Text>
        <TimerStatus timeLeft={timeLeft} />

        {isExpired ? null : (
          <Box sx={styles.formContainerStyle}>
            <HStack sx={styles.passwordWrapStyles}>
              <Text sx={styles.passwordStyles}>{batchIdData?.password}</Text>

              <HStack sx={styles.copiedTextWrap}>
                {copied ? (
                  <>
                    <Check size={24} color="green" />
                  </>
                ) : (
                  <Copy
                    data-testid="copy-icon"
                    style={styles.copyIcon}
                    size={24}
                    onClick={() => batchIdData?.password && copyToClipboard(batchIdData.password)}
                  />
                )}
              </HStack>
            </HStack>
          </Box>
        )}
      </Box>
    </ModalVariants>
  );
};

export default PasswordModal;
