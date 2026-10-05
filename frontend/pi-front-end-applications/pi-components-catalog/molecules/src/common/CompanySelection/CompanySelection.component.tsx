import { Flex } from '@chakra-ui/react';
import { Company } from '@whitbread-eos/api';
import { TableListRow, Button, DataModalvariantProps, ModalVariants } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React, { RefObject, useState } from 'react';

import CompanyList from './CompanyList.component';
import CompanyProfile from './CompanyProfile.component';
import { COMPANY_MODAL_TYPE } from './modalType.enum';
import { SELECTION_STEP } from './selectionSteps.enum';

interface Props {
  showCompanySelectionModal: boolean;
  companyModalType: COMPANY_MODAL_TYPE;
  companies: Company[];
  tooManyResults?: boolean;
  finalFocusRef: RefObject<any>;
  onClose: () => void;
  onCompanyVerified: (company: Company) => void;
}

function CompanySelection({
  showCompanySelectionModal,
  companyModalType,
  companies,
  tooManyResults = false,
  finalFocusRef,
  onClose,
  onCompanyVerified,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const [currentStep, setCurrentStep] = useState(SELECTION_STEP.LIST);
  const [selectedCompany, setSelectedCompany] = useState<Company | null>(null);
  const isVerifyAndConfirmDisabled =
    companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY
      ? (selectedCompany?.restricted ?? true)
      : false;
  const baseDataTestId = 'CompanySelection';

  let finalRef;
  if (currentStep === SELECTION_STEP.LIST) {
    finalRef = finalFocusRef;
  }

  const handleCompanySelected = (selectedCompany: TableListRow) => {
    const company = companies.find((comp) => comp.companyId === selectedCompany.companyId);
    if (company) {
      setSelectedCompany(company);
      setCurrentStep(SELECTION_STEP.PROFILE);
    }
  };

  const goBackToCompanyList = () => {
    setSelectedCompany(null);
    setCurrentStep(SELECTION_STEP.LIST);
  };

  const modalTitle =
    currentStep === SELECTION_STEP.LIST
      ? t('ccui.companyModals.selectCompany')
      : t('ccui.companyModals.companyProfile');

  const modalBody =
    currentStep === SELECTION_STEP.LIST ? (
      <CompanyList
        companyModalType={companyModalType}
        companies={companies}
        tooManyResults={tooManyResults}
        onCompanySelected={handleCompanySelected}
      />
    ) : (
      <CompanyProfile selectedCompany={selectedCompany} companyModalType={companyModalType} />
    );

  const modalFooter =
    currentStep === SELECTION_STEP.LIST ? (
      <Button
        variant="tertiary"
        data-testid={`${baseDataTestId}-ModalCancelButton`}
        onClick={onClose}
        size={'md'}
      >
        {t('ccui.companyModals.close')}
      </Button>
    ) : (
      <Flex gap="4">
        <Button
          variant="secondary"
          data-testid={`${baseDataTestId}-ModalVerifyButton`}
          isDisabled={isVerifyAndConfirmDisabled}
          onClick={() => {
            selectedCompany && onCompanyVerified(selectedCompany);
          }}
          size={'md'}
        >
          {t('ccui.companyModals.verifyConfirm')}
        </Button>
        <Button
          variant="tertiary"
          data-testid={`${baseDataTestId}-ModalCancelButton`}
          onClick={goBackToCompanyList}
          size={'md'}
        >
          {t('ccui.companyModals.close')}
        </Button>
      </Flex>
    );

  const variantProps: DataModalvariantProps = {
    title: modalTitle,
    finalFocusRef: finalRef,
    footer: modalFooter,
    modalHeight: modalHeight(currentStep),
    externalFooterStyling: footerStyles(currentStep),
  };

  return (
    <ModalVariants
      isOpen={showCompanySelectionModal}
      onClose={currentStep === SELECTION_STEP.LIST ? onClose : goBackToCompanyList}
      variant="data"
      variantProps={variantProps}
      updatedWidth={modalWidth(currentStep)}
      dataTestId={baseDataTestId}
    >
      {modalBody}
    </ModalVariants>
  );
}

const modalWidth = (currentStep: SELECTION_STEP) => {
  const width =
    currentStep === SELECTION_STEP.LIST
      ? { lg: '1224px', xl: '1302px' }
      : { lg: '808px', xl: '864px' };

  return width;
};

const modalHeight = (currentStep: SELECTION_STEP) => {
  const height = currentStep === SELECTION_STEP.LIST ? { lg: '937px' } : { lg: 'fit-content' };

  return height;
};

const footerStyles = (currentStep: SELECTION_STEP) => {
  const styles =
    currentStep === SELECTION_STEP.LIST
      ? {
          paddingInlineStart: 'var(--chakra-space-4)',
          paddingInlineEnd: 'var(--chakra-space-4)',
          py: 'var(--chakra-space-6)',
        }
      : {
          paddingInlineStart: 'var(--chakra-space-8)',
          paddingInlineEnd: 'var(--chakra-space-8)',
          paddingTop: 'var(--chakra-space-10)',
          paddingBottom: 'var(--chakra-space-8)',
        };

  return styles;
};

export default CompanySelection;
