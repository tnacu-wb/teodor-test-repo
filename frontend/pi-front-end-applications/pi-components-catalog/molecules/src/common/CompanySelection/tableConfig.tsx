import { Address, Company } from '@whitbread-eos/api';
import { Checkbox, TableListColumn, TableListRow } from '@whitbread-eos/atoms';

import { COMPANY_MODAL_TYPE } from './modalType.enum';
import { TABLE_FIELDS } from './tableFields.enum';

export const getTableColumns = (
  t: (x: string, y?: { [key: string]: string }) => string,
  companyModalType: COMPANY_MODAL_TYPE
): TableListColumn[] => {
  const tableListColumns: TableListColumn[] = [
    {
      key: TABLE_FIELDS.COMPANY_NAME,
      title: t('ccui.companyModals.companyName'),
    },
    {
      key: TABLE_FIELDS.ADDRESS,
      title: t('ccui.companyModals.address'),
    },
    {
      key: TABLE_FIELDS.TELEPHONE_NUMBER,
      title: t('ccui.companyModals.tel'),
    },
    {
      key: TABLE_FIELDS.CORP_ID,
      title: t('ccui.companyModals.corpID'),
    },
    {
      key: TABLE_FIELDS.COMPANY_ID,
      title: t('ccui.companyModals.companyID'),
    },
  ];
  const accountToCompanyColumns: TableListColumn[] = [
    {
      key: TABLE_FIELDS.AR_NUMBER,
      title: t('ccui.companyModals.arNumber'),
    },
    {
      key: TABLE_FIELDS.RESTRICTED,
      title: t('ccui.companyModals.restricted'),
      render: (row: TableListRow): React.ReactNode => {
        const rowValue = row[TABLE_FIELDS.RESTRICTED];
        const restricted = typeof rowValue === 'string' && rowValue.toLowerCase() === 'true';
        return <Checkbox isChecked={restricted} isReadOnly checkboxWrapperStyles={{ my: '0' }} />;
      },
    },
  ];

  return companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY
    ? [...tableListColumns, ...accountToCompanyColumns]
    : tableListColumns;
};

export const getTableRows = (companies: Company[], companyModalType: COMPANY_MODAL_TYPE) => {
  return companies.map((company) => {
    const {
      [TABLE_FIELDS.COMPANY_NAME]: name,
      [TABLE_FIELDS.ADDRESS]: address,
      [TABLE_FIELDS.TELEPHONE_NUMBER]: telephoneNumber,
      [TABLE_FIELDS.CORP_ID]: corpId,
      [TABLE_FIELDS.COMPANY_ID]: compId,
      [TABLE_FIELDS.AR_NUMBER]: arNumber,
      [TABLE_FIELDS.RESTRICTED]: restricted,
    } = company;

    const formattedAddress = getFormattedAddress(address);
    const row = {
      [TABLE_FIELDS.COMPANY_NAME]: name,
      [TABLE_FIELDS.ADDRESS]: formattedAddress,
      [TABLE_FIELDS.TELEPHONE_NUMBER]: telephoneNumber,
      [TABLE_FIELDS.CORP_ID]: corpId ?? 'NA',
      [TABLE_FIELDS.COMPANY_ID]: compId,
    };

    const accountToCompanyFields = {
      [TABLE_FIELDS.AR_NUMBER]: arNumber,
      [TABLE_FIELDS.RESTRICTED]: String(restricted),
    };

    return companyModalType === COMPANY_MODAL_TYPE.ACCOUNT_TO_COMPANY
      ? { ...row, ...accountToCompanyFields }
      : row;
  });
};

export const getFormattedAddress = (address: Address): string => {
  const {
    addressLine1,
    addressLine2,
    addressLine3,
    addressLine4 = '',
    country,
    postalCode,
  } = address;

  return [addressLine1, addressLine2, addressLine3, addressLine4, country, postalCode]
    .filter(Boolean)
    .join(', ');
};
