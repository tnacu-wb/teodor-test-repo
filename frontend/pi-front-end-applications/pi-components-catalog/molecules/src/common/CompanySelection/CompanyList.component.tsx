import { Box, StyleProps, TableContainerProps } from '@chakra-ui/react';
import { Company } from '@whitbread-eos/api';
import { Alert, Notification, TableList, TableListRow } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import React from 'react';

import { COMPANY_MODAL_TYPE } from './modalType.enum';
import { getTableColumns, getTableRows } from './tableConfig';

interface Props {
  companyModalType: COMPANY_MODAL_TYPE;
  companies: Company[];
  tooManyResults: boolean;
  onCompanySelected: (selectedCompany: TableListRow) => void;
}

function CompanyList({
  companyModalType,
  companies,
  tooManyResults,
  onCompanySelected,
}: Readonly<Props>) {
  const { t } = useTranslation();

  if (companies.length === 0) {
    return null;
  }

  const columns = getTableColumns(t, companyModalType);
  const rows = getTableRows(companies, companyModalType);

  return (
    <>
      {tooManyResults && (
        <Box {...containerStyles}>
          <Notification
            maxWidth="full"
            variant="alert"
            status="error"
            title={''}
            description={t('ccui.companyModals.tooManyResults')}
            svg={<Alert />}
          />
        </Box>
      )}
      <TableList
        columns={columns}
        rows={rows}
        isExpandable={false}
        rowStyles={tableRowStyles}
        containerStyles={containerStyles}
        externalHeaderStyles={tableHeaderStyles}
        handleRowClicked={onCompanySelected}
        rowHoverStyles={rowHoverStyles}
      />
    </>
  );
}

const containerStyles: TableContainerProps = {
  marginInlineStart: 'var(--chakra-space-4)',
  marginInlineEnd: 'var(--chakra-space-4)',
  marginTop: 'var(--chakra-space-6)',
};

const tableRowStyles = {
  padding: {
    lg: 'var(--chakra-space-8) 0 var(--chakra-space-8) var(--chakra-space-6)',
  },
  verticalAlign: 'top',
};

const tableHeaderStyles = {
  padding: {
    lg: 'var(--chakra-space-6)',
  },
  verticalAlign: 'top',
};

const rowHoverStyles: StyleProps = {
  bg: 'lightGrey5',
  cursor: 'pointer',
};

export default CompanyList;
