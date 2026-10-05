import { Box } from '@chakra-ui/react';
import type { PreAuthorisedChargesData, CompanyProfile } from '@whitbread-eos/api';
import { Dispatch, SetStateAction } from 'react';

import AccountToCompany from './AccountToCompany.component';

export interface Props {
  hotelId: string;
  selectedPaymentDetail: string;
  setACCharges: Dispatch<SetStateAction<string[]>>;
  setIsTotalCostVisible: Dispatch<SetStateAction<boolean>>;
  setCompanyReferenceError: Dispatch<SetStateAction<boolean>>;
  setCompanyNumber: Dispatch<SetStateAction<string>>;
  setCompanyId: Dispatch<SetStateAction<string>>;
  setACCompanyReference: Dispatch<SetStateAction<string>>;
  setCompanyDetails: Dispatch<SetStateAction<CompanyProfile | null>>;
  isFromChangePaymentBIC?: boolean;
}

export default function AccountToCompanyContainer({
  hotelId,
  selectedPaymentDetail,
  setACCharges,
  setIsTotalCostVisible,
  setCompanyReferenceError,
  setCompanyNumber,
  setCompanyId,
  setACCompanyReference,
  setCompanyDetails,
  isFromChangePaymentBIC,
}: Readonly<Props>) {
  const setPreAuthorisedCharges = (charges: PreAuthorisedChargesData[]) => {
    const newCharges: string[] = charges.map((charge) => charge.label);
    setACCharges(newCharges);
  };
  return (
    <Box data-testid="accountToCompanyContainer">
      <AccountToCompany
        hotelId={hotelId}
        selectedPaymentDetail={selectedPaymentDetail}
        setPreAuthorisedCharges={setPreAuthorisedCharges}
        setIsTotalCostVisible={setIsTotalCostVisible}
        setCompanyReferenceError={setCompanyReferenceError}
        setCompanyNumber={setCompanyNumber}
        setCompanyId={setCompanyId}
        setCompanyDetails={setCompanyDetails}
        setACCompanyReference={setACCompanyReference}
        isFromChangePaymentBIC={isFromChangePaymentBIC}
      />
    </Box>
  );
}
