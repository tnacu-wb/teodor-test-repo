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
import { Control, useWatch } from 'react-hook-form';

import {
  emailMarketingDescriptionLayoutStyles,
  emailMarketingDescriptionLegacyTypography,
  emailMarketingDescriptionSemanticTypography,
  emailMarketingHeaderLayoutStyles,
  emailMarketingHeaderLegacyTypography,
  emailMarketingHeaderSemanticTypography,
} from '../emailMarketingTypography.constants';

interface Props extends FormDynamicFieldCompProps {
  control: Control;
}

export default function EmailUpdates({ control, formField, field }: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();
  const isRemovePIIDataFromLocalStorageEnabled =
    formField.props?.isRemovePIIDataFromLocalStorageEnabled ?? false;
  const defaultAcceptFutureMailing = formField.props?.defaultValues?.acceptFutureMailing ?? false;
  const testId = formField.testid ?? 'EmailUpdate';
  const currentLang = formField.props?.currentLang || GLOBALS.language.EN;
  const { t } = useTranslation();
  const selectedCountry = useWatch({ name: 'countryCode', control });
  const isGermanyAsCountry = selectedCountry === 'DE';
  const isRegisterPage = testId === 'RegisterPIPage-AcceptFutureMailing';
  const isDEOptInEnabled =
    (currentLang === GLOBALS.language.DE && !isRegisterPage) || formField.props?.isDEOptInEnabled;
  const isReceiveEmailByDefault =
    (currentLang === 'en' && isGermanyAsCountry) || currentLang === 'de';
  const [isChecked, setIsChecked] = useState(
    isRemovePIIDataFromLocalStorageEnabled ? !defaultAcceptFutureMailing : false
  );
  const getCheckboxText = () => {
    const description = '';

    switch (currentLang) {
      case 'en':
        if (isGermanyAsCountry) {
          return isDEOptInEnabled
            ? t('booking.emailBoxText.GermanResidence.optOut')
            : t('booking.emailBoxText.GermanResidence');
        }

        return t('booking.emailBoxText.premierinnhubzip');
      case 'de':
        return isDEOptInEnabled
          ? t('booking.emailBoxText.GermanResidence.optOut')
          : t('booking.emailBoxText.GermanResidence');
      default:
        return description;
    }
  };

  const onChange = (option: React.ChangeEvent<HTMLInputElement>) => {
    const value = isReceiveEmailByDefault ? option?.target?.checked : !option?.target?.checked;
    setIsChecked(option?.target?.checked);
    field.onChange(value);
  };

  const getEmailText = () => {
    if (isReceiveEmailByDefault) {
      return isDEOptInEnabled
        ? t('booking.emailTextGermanResidence.optOut')
        : t('booking.emailTextGermanResidence');
    }

    return t('booking.emailText');
  };

  useEffect(() => {
    field.onChange(isChecked ? isReceiveEmailByDefault : !isReceiveEmailByDefault);
  }, [isChecked, isReceiveEmailByDefault]);

  useEffect(() => {
    let marketingOptInChoice;

    if (isRegisterPage) {
      marketingOptInChoice = isReceiveEmailByDefault ? isChecked : !isChecked;
    } else {
      marketingOptInChoice = !isChecked;
    }

    analytics.update({ marketingOptInChoice });
  }, [isChecked, isReceiveEmailByDefault, isRegisterPage]);

  useEffect(() => {
    if (isRemovePIIDataFromLocalStorageEnabled && !isRegisterPage) {
      setIsChecked(!defaultAcceptFutureMailing);
    }
  }, [isRemovePIIDataFromLocalStorageEnabled, defaultAcceptFutureMailing, isRegisterPage]);

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
        {renderSanitizedHtml(getEmailText())}
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
          {getCheckboxText()}
        </Text>
      </Checkbox>
    </Box>
  );
}
