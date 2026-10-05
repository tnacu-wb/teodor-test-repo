import { Text } from '@chakra-ui/react';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

const PrivacyPolicy = () => {
  const { t } = useTranslation();

  return (
    <Text as="div" data-testid="privacy-policy-label" ml="xl">
      {renderSanitizedHtml(
        t('precheckin.privacypolicy.checkbox').replace(
          /<a/g,
          '<a style="color: var(--chakra-colors-btnSecondaryEnabled); font-weight: bold; cursor: pointer;" target="_blank"'
        )
      )}
    </Text>
  );
};

export default PrivacyPolicy;
