import { Box, Text } from '@chakra-ui/react';
import { Checkbox, FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import {
  analytics,
  formatDataTestId,
  GLOBALS,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import {
  emailMarketingDescriptionLayoutStyles,
  emailMarketingDescriptionLegacyTypography,
  emailMarketingDescriptionSemanticTypography,
  emailMarketingHeaderLayoutStyles,
  emailMarketingHeaderLegacyTypography,
  emailMarketingHeaderSemanticTypography,
} from '../emailMarketingTypography.constants';

export default function EmailOptOut({ formField, field }: Readonly<FormDynamicFieldCompProps>) {
  const getTypographyProps = useSemanticTypography();
  const testId = formField.testid ?? 'EmailOptOut';
  const { t } = useTranslation();
  const isRemovePIIDataFromLocalStorageEnabled =
    formField.props?.isRemovePIIDataFromLocalStorageEnabled ?? false;
  const defaultAcceptFutureMailing = formField.props?.defaultValues?.acceptFutureMailing ?? false;
  const [isChecked, setIsChecked] = useState(
    isRemovePIIDataFromLocalStorageEnabled ? !defaultAcceptFutureMailing : false
  );
  const currentLang = formField.props?.currentLang ?? GLOBALS.language.EN;
  const isDEOptInEnabled = currentLang === GLOBALS.language.DE || formField.props?.isDEOptInEnabled;

  const onChange = (option: React.ChangeEvent<HTMLInputElement>) => {
    setIsChecked(option?.target?.checked);
    field.onChange(!option?.target?.checked);
  };

  useEffect(() => {
    if (isRemovePIIDataFromLocalStorageEnabled) {
      setIsChecked(!defaultAcceptFutureMailing);
      field.onChange(defaultAcceptFutureMailing);
    }
  }, [isRemovePIIDataFromLocalStorageEnabled, defaultAcceptFutureMailing]);

  useEffect(() => {
    analytics.update({
      marketingOptInChoice: !isChecked,
    });
  }, [isChecked]);

  return (
    <Box data-testid={formatDataTestId(testId, 'Container')}>
      <Text
        {...emailMarketingHeaderLayoutStyles}
        {...getTypographyProps(
          emailMarketingHeaderLegacyTypography,
          emailMarketingHeaderSemanticTypography
        )}
        data-testid={formatDataTestId(testId, 'Header')}
      >
        {t('booking.emailTitle')}
      </Text>
      <Box
        {...emailMarketingDescriptionLayoutStyles}
        {...getTypographyProps(
          emailMarketingDescriptionLegacyTypography,
          emailMarketingDescriptionSemanticTypography
        )}
        data-testid={formatDataTestId(testId, 'Description')}
        className="formatLinks"
      >
        {renderSanitizedHtml(
          isDEOptInEnabled ? t('booking.emailTextGermanResidence.optOut') : t('booking.emailText')
        )}
      </Box>
      <Checkbox
        name={field.name}
        value={field.value}
        onChange={onChange}
        data-testid={formatDataTestId(testId, 'CheckboxContainer')}
        {...(isRemovePIIDataFromLocalStorageEnabled && {
          isChecked: isChecked,
        })}
      >
        <Text
          {...emailMarketingDescriptionLayoutStyles}
          {...getTypographyProps(
            emailMarketingDescriptionLegacyTypography,
            emailMarketingDescriptionSemanticTypography
          )}
          data-testid={formatDataTestId(testId, 'CheckboxText')}
        >
          {isDEOptInEnabled
            ? t('booking.emailBoxText.GermanResidence.optOut')
            : t('booking.emailBoxText.premierinnhubzip')}
        </Text>
      </Checkbox>
    </Box>
  );
}
