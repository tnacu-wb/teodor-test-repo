import { StyleProps, Td, Tr } from '@chakra-ui/react';
import { Cell, DataForUpdateFormType, SelectedRowType } from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';

import UpdateAccountForm from '../ccuiFormConfig/updateForm/UpdateAccountForm.component';

interface Props {
  handleClickRow: (input: SelectedRowType) => void;
  selectedRow: SelectedRowType;
  dataForUpdateForm: DataForUpdateFormType;
  baseDataTestId: string;
  t: (id: string) => string;
  cells: Cell[];
  accountId: string;
  rowNr: number;
}

export default function ResultRow({
  handleClickRow,
  selectedRow,
  dataForUpdateForm,
  cells,
  accountId,
  baseDataTestId,
  rowNr,
}: Readonly<Props>) {
  const rowCount = cells.length + 1; // Extra row for Open/Close button;

  return (
    <>
      <Tr
        cursor="pointer"
        data-testid={formatDataTestId(baseDataTestId, `Table-Row-${rowNr}`)}
        onClick={() => {
          handleClickRow({ ...cells, rowNr, accountId });
        }}
      >
        {cells.map((cell: Cell) => (
          <Td
            key={cell.id}
            data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}`)}
            {...resultRowCellStyle(selectedRow?.rowNr, rowNr)}
          >
            {cell.value}
          </Td>
        ))}
      </Tr>
      {selectedRow?.rowNr === rowNr && (
        <Tr>
          <Td colSpan={rowCount} {...cardRowStyle}>
            <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
          </Td>
        </Tr>
      )}
    </>
  );
}

const cardRowStyle = {
  borderTop: 'none',
  backgroundColor: 'lightGrey5',
} as StyleProps;

const resultRowCellStyle = (SelectedRowNr: number | undefined, rowNr: number) => {
  return {
    borderBottom: SelectedRowNr === rowNr ? 'none' : 'var(--chakra-borders-1px)',
    backgroundColor: SelectedRowNr === rowNr ? 'lightGrey5' : 'inherit',
    _first: {
      maxWidth: '400px',
      whiteSpace: 'noWrap',
      overflow: 'hidden',
    },
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  };
};
