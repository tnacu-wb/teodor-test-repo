import React from 'react';

import { DataTableCell, DataTableRow } from '~components/innBusiness/DataTable';

type Args = {
  renderRowStatus: (row: DataTableRow, field: string) => React.JSX.Element;
  renderRowAmount: (row: DataTableRow, field: string) => React.JSX.Element;
  t: (t: string) => string;
};

export default function getPaymentsColumns({ renderRowStatus, renderRowAmount, t }: Args) {
  return [
    {
      id: 'paymentDate',
      label: t('statementsInvoicesPayments.payments.table.header.date'),
      headerClassName: dateLabelHeaderStyle,
      className: dateLabelHeaderStyle,
    },
    {
      id: 'paymentDescription',
      label: t('statementsInvoicesPayments.payments.table.header.description'),
      headerClassName: desktopOnlyStyle,
      className: desktopOnlyStyle,
    },
    {
      id: 'paymentFailed',
      label: t('statementsInvoicesPayments.payments.table.header.status'),
      headerClassName: labelHeaderStyle,
      className: labelHeaderStyle,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowStatus(row, 'paymentFailed');
      },
    },
    {
      id: 'paymentValue',
      label: t('statementsInvoicesPayments.payments.table.header.value'),
      headerClassName: labelStyleTextRight,
      className: labelStyleTextRight,
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderRowAmount(row, 'paymentValue');
      },
    },
  ];
}

export const desktopOnlyStyle = 'mobile:hidden w-[30%]';
export const dateLabelHeaderStyle = 'mobile:w-full w-[13%] box-content';
export const labelStyleTextRight = 'mobile:w-full w-[30%] text-right';
export const labelHeaderStyle = 'mobile:w-[100px] w-[27%] box-content';
