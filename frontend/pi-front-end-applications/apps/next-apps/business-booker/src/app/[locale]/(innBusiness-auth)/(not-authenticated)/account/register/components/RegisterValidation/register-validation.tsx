'use client';

import { LOCALES } from '@whitbread-eos/api';
import { useWizardContext } from '@whitbread-eos/layout';
import { useEffect } from 'react';

import { RegisterValidationState } from '../../page';
import AccountExistsManager from '../AccountExistsManager/account-exists-manager';
import AccountExistsNoManager from '../AccountExistsNoManager/account-exists-no-manager';
import ConfirmationEmail from '../ConfirmationEmail/confirmation-email';

type Props = {
  locale: LOCALES;
};

const RegisterValidation = ({ locale }: Props) => {
  const { wizardState } = useWizardContext<RegisterValidationState>();

  useEffect(() => {
    if (!wizardState) {
      return;
    }

    if (wizardState.existingCompany) {
      window?._satellite?.track('signUpExistingCompany');
      return;
    }

    if (!wizardState.existingEmployee) {
      window?._satellite?.track('signUpComplete');
    }
  }, []);

  const RenderConfirmation = () => {
    if (wizardState.existingEmployee) {
      return <AccountExistsManager locale={locale} />;
    }
    if (wizardState.existingCompany) {
      return <AccountExistsNoManager locale={locale} />;
    }
    return <ConfirmationEmail locale={locale} />;
  };

  return <>{RenderConfirmation()}</>;
};

export default RegisterValidation;
