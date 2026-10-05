import type { PreAuthorisedChargesData, Company, CompanyProfile } from '@whitbread-eos/api';
import React, { Dispatch, SetStateAction, useEffect, useState } from 'react';

import {
  AccountToCompanyDetails,
  AccountToCompanyFields,
  AccountToCompanyPreAuthorisedCharges,
} from './components';

export interface Props {
  hotelId: string;
  selectedPaymentDetail: string;
  setPreAuthorisedCharges: (charges: PreAuthorisedChargesData[]) => void;
  setIsTotalCostVisible: Dispatch<SetStateAction<boolean>>;
  setCompanyReferenceError: Dispatch<SetStateAction<boolean>>;
  setCompanyNumber: Dispatch<SetStateAction<string>>;
  setCompanyId: Dispatch<SetStateAction<string>>;
  setACCompanyReference: Dispatch<SetStateAction<string>>;
  setCompanyDetails: Dispatch<SetStateAction<CompanyProfile | null>>;
  isFromChangePaymentBIC?: boolean;
}

export default function AccountToCompany({
  hotelId,
  selectedPaymentDetail,
  setPreAuthorisedCharges,
  setIsTotalCostVisible,
  setCompanyReferenceError,
  setCompanyNumber,
  setCompanyId,
  setACCompanyReference,
  setCompanyDetails,
  isFromChangePaymentBIC,
}: Readonly<Props>) {
  const [verifiedCompany, setVerifiedCompany] = useState<Company | null>(null);

  useEffect(() => {
    const isVerifiedCompany = !!verifiedCompany;
    setIsTotalCostVisible(isVerifiedCompany);
    if (isVerifiedCompany) {
      setCompanyNumber(verifiedCompany.arNumber ?? '');
      setCompanyId(verifiedCompany?.companyId ?? '');
      setCompanyDetails({
        name: verifiedCompany?.name ?? '',
        address: {
          addressLine1: verifiedCompany?.address?.addressLine1,
          addressLine2: verifiedCompany?.address?.addressLine2,
          addressLine3: verifiedCompany?.address?.addressLine3,
          cityName: verifiedCompany?.address?.cityName ?? '',
          country: verifiedCompany?.address?.country,
          postalCode: verifiedCompany?.address?.postalCode,
        },
      });
    } else {
      setCompanyNumber('');
      setCompanyId('');
      setACCompanyReference('');
      setCompanyDetails(null);
    }
    setPreAuthorisedCharges([]);
  }, [verifiedCompany]);

  return (
    <>
      {!verifiedCompany ? (
        <AccountToCompanyDetails
          hotelId={hotelId}
          selectedPaymentDetail={selectedPaymentDetail}
          setVerifiedCompany={setVerifiedCompany}
        />
      ) : (
        <>
          <AccountToCompanyFields
            verifiedCompany={verifiedCompany}
            setVerifiedCompany={setVerifiedCompany}
          />
          <AccountToCompanyPreAuthorisedCharges
            setPreAuthorisedCharges={setPreAuthorisedCharges}
            setCompanyReferenceError={setCompanyReferenceError}
            setACCompanyReference={setACCompanyReference}
            isFromChangePaymentBIC={isFromChangePaymentBIC}
          />
        </>
      )}
    </>
  );
}
