import { useQueryClient } from '@tanstack/react-query';
import { DataForUpdateFormType, SelectedRowType } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { SearchAccountResultList } from '@whitbread-eos/molecules';

export interface Props {
  handleClickRow: (selectedRow: SelectedRowType) => void;
  selectedRow: SelectedRowType;
  dataForUpdateForm: DataForUpdateFormType;
  t: (id: string) => string;
  baseDataTestId: string;
  resultsData: any;
}

export default function ResultsContainer({
  handleClickRow,
  selectedRow,
  dataForUpdateForm,
  baseDataTestId,
  t,
  resultsData,
}: Readonly<Props>) {
  const queryClient = useQueryClient();

  const headerTitles = [
    {
      id: 'GuestName',
      text: t('ccui.guestAccounts.col.guestName'),
    },
    {
      id: 'CompanyName',
      text: t('ccui.guestAccounts.col.companyName'),
    },
    {
      id: 'Email',
      text: t('ccui.guestAccounts.col.email'),
    },
    {
      id: 'PostCodeHome',
      text: t('ccui.guestAccounts.col.homePostcode'),
    },
    {
      id: 'PostCodeCompany',
      text: t('ccui.guestAccounts.col.companyPostcode'),
    },
  ];

  return (
    <ErrorBoundary>
      <SearchAccountResultList
        handleClickRow={handleClickRow}
        selectedRow={selectedRow}
        dataForUpdateForm={dataForUpdateForm}
        queryClient={queryClient}
        loading={resultsData.isLoading}
        baseDataTestId={baseDataTestId}
        rows={resultsData.guests}
        t={t}
        headerTitles={headerTitles}
      />
    </ErrorBoundary>
  );
}
