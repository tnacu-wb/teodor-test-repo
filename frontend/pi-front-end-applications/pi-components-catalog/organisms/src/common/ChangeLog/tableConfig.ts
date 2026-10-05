import { formatDate } from '@whitbread-eos/utils';

import { ChangeLogTableColumn, ChangeLogTableRow } from './ChangeLogTable.component';
import { TABLE_FIELDS } from './tableFields.enum';

export const getTableColumns = (
  t: (x: string, y?: { [key: string]: string }) => string
): ChangeLogTableColumn[] => {
  return [
    {
      key: TABLE_FIELDS.DATE,
      title: t('ccui.changeLogModal.date'),
      isFilterable: false,
      render: (row: ChangeLogTableRow) => {
        const rowValue = row[TABLE_FIELDS.DATE];
        return formatDate(rowValue, 'dd.MM.yy');
      },
    },
    {
      key: TABLE_FIELDS.TIME,
      title: t('ccui.changeLogModal.time'),
      isFilterable: false,
    },
    {
      key: TABLE_FIELDS.ACTION_TYPE,
      title: t('ccui.changeLogModal.actionType'),
      isFilterable: true,
    },
    {
      key: TABLE_FIELDS.ACTION_DESCRIPTION,
      title: t('ccui.changeLogModal.actionDescription'),
      isFilterable: false,
      clampOverflowingText: true,
    },
    {
      key: TABLE_FIELDS.USER,
      title: t('ccui.changeLogModal.user'),
      isFilterable: true,
    },
  ];
};
