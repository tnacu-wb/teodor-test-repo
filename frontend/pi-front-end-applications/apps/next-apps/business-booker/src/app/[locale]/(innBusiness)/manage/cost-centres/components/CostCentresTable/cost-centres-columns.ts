import React from 'react';

import { DataTableCell, DataTableRow } from '~components/innBusiness/DataTable';

type Args = {
  renderRowStatus: () => React.JSX.Element;
  renderActions: (code: string) => React.JSX.Element;
  t: (t: string) => string;
};

export default function getCostCentresColumns({ renderRowStatus, renderActions, t }: Args) {
  return [
    {
      id: 'costCentreCode',
      label: t('costCentreMgmt.columns.code'),
      headerClassName: codeHeaderStyle,
      className: codeHeaderStyle,
    },
    {
      id: 'costCentreName',
      label: t('costCentreMgmt.columns.name'),
      headerClassName: nameHeaderStyle,
      className: nameHeaderStyle,
    },
    {
      id: 'status',
      label: t('costCentreMgmt.columns.status'),
      headerClassName: statusHeaderStyle,
      className: statusHeaderStyle,
      render() {
        return renderRowStatus();
      },
    },
    {
      id: 'cardAction',
      label: '',
      headerClassName: actionsHeaderStyle,
      className: 'truncate',
      render(_cell: DataTableCell, row: DataTableRow) {
        return renderActions(row.costCentreCode as string);
      },
    },
  ];
}

export const codeHeaderStyle = 'min-w-[200px] w-[250px] mobile:w-full box-content truncate';
export const nameHeaderStyle = 'min-w-[100px] w-[150px] mobile:w-full';
export const statusHeaderStyle = 'hidden xl:table-cell min-w-[60px] w-[60px] box-content';
export const actionsHeaderStyle = 'min-w-[80px] w-[100px] text-center';
export const labelStyleTextRight = 'mobile:w-full tablet:w-full desktop:w-full text-right';
