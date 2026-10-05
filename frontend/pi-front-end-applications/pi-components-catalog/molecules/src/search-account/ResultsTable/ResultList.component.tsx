import {
  StyleProps,
  Table,
  TableContainer,
  Tbody,
  Td,
  Text,
  Th,
  Thead,
  Tr,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type { DataForUpdateFormType, TableHeader } from '@whitbread-eos/api';
import { ExtendedTableRow, SelectedRowType } from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';

import ResultRow from './ResultRow.component';

interface Props {
  handleClickRow: (selectedRow: SelectedRowType) => void;
  selectedRow: SelectedRowType;
  dataForUpdateForm: DataForUpdateFormType;
  baseDataTestId: string;
  t: (id: string) => string;
  headerTitles: TableHeader[];
  rows: ExtendedTableRow[] | undefined;
  loading?: boolean;
  error?: any;
  isError?: boolean;
  queryClient?: QueryClient;
}

export default function ResultList({
  handleClickRow,
  selectedRow,
  dataForUpdateForm,
  loading,
  rows,
  headerTitles,
  baseDataTestId,
  t,
}: Readonly<Props>) {
  return (
    <TableContainer
      data-testid={formatDataTestId(baseDataTestId, 'Table-Container')}
      {...resultContainerStyle}
    >
      <Table size="sm" {...tableStyles}>
        <Thead {...headerTableStyle}>
          <Tr>
            {headerTitles.map((headerTitle: TableHeader) => {
              return (
                <Th
                  key={headerTitle.id}
                  {...headerResultsTextStyle}
                  data-testid={formatDataTestId(baseDataTestId, `TableHeader-${headerTitle.id}`)}
                >
                  {headerTitle.text}
                </Th>
              );
            })}
          </Tr>
        </Thead>

        <Tbody>
          {rows?.map((row: ExtendedTableRow, i: number) => (
            <ResultRow
              handleClickRow={handleClickRow}
              selectedRow={selectedRow}
              dataForUpdateForm={dataForUpdateForm}
              key={row.AccountId}
              cells={row.cells}
              accountId={row.AccountId}
              baseDataTestId={baseDataTestId}
              rowNr={i}
              t={t}
            />
          ))}
          {loading && (
            <Tr>
              <Td>
                <Text data-testid={formatDataTestId(baseDataTestId, 'Table-loading-message')}>
                  {t('searchresults.list.hotel.loading')}
                </Text>
              </Td>
            </Tr>
          )}
        </Tbody>
      </Table>
    </TableContainer>
  );
}

const resultContainerStyle = {
  display: 'block',
  overflowY: 'visible',
  overflowX: 'visible',
  whiteSpace: 'normal',
} as StyleProps;

const headerTableStyle = {
  backgroundColor: 'lightGrey5',
  h: '4rem',
} as StyleProps;

const tableStyles = {
  width: '100%',
  maxW: '100%',
} as StyleProps;

const headerResultsTextStyle = {
  fontWeight: 'medium',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseBlack',
  textTransform: 'none',
  fontFamily: 'header',
} as StyleProps;
