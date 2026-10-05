import { Language } from '@whitbread-eos/api';
import React from 'react';

import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { AddCorrespondenceButton } from './add-correspondence-button';

type Props = {
  showCorrespondenceButton: boolean;
  showCompanyCorrespondenceAddress: boolean;
  handleAddCorrespondenceAddress: () => void;
  icons: Record<string, string>;
  buttonStyle: string;
  buttonIconStyle: string;
  baseDataTestId: string;
  nameStyle: string;
  t: (key: string) => string;
  language: Language;
};

export function CorrespondenceAddressSection({
  showCorrespondenceButton,
  showCompanyCorrespondenceAddress,
  handleAddCorrespondenceAddress,
  icons,
  buttonStyle,
  buttonIconStyle,
  baseDataTestId,
  nameStyle,
  t,
  language,
}: Props) {
  return (
    <>
      {showCorrespondenceButton && (
        <AddCorrespondenceButton
          onClick={handleAddCorrespondenceAddress}
          icons={icons}
          buttonStyle={buttonStyle}
          buttonIconStyle={buttonIconStyle}
          baseDataTestId={baseDataTestId}
        />
      )}
      {showCompanyCorrespondenceAddress && (
        <>
          <span
            data-testid={`${baseDataTestId}-display-company-info-widget-title`}
            className={nameStyle}
          >
            {t('payApplication.companyDetails.correspondence.address.optional')}
          </span>
          <CompanyAddressFields
            icons={icons}
            language={language}
            variant={'correspondenceAddress'}
            isIBPayApp
          />
        </>
      )}
    </>
  );
}
