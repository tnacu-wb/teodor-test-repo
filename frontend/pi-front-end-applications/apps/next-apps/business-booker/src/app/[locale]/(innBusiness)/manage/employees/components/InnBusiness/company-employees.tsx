import { LOCALES } from '@whitbread-eos/api';
import { getCountryLanguageByLocale, getTranslations } from '@whitbread-eos/utils/server';

type Props = {
  numberOfEmployees: number;
  companyName: string;
  locale?: LOCALES;
  mobile?: boolean;
};

export async function CompanyEmployees({ numberOfEmployees, companyName, locale }: Props) {
  const baseDataTestId = 'CompanyEmployees';
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'users');
  const textDescription = `${numberOfEmployees} ${t(
    'userMgmt.manageEmployees.employeeManagement.employeesAtText'
  )} ${companyName}`;

  return (
    <div data-testid={`${baseDataTestId}-container`}>
      <div className={nameStyle} data-testid={`${baseDataTestId}-text`}>
        {textDescription}
      </div>
    </div>
  );
}

const nameStyle = 'font-normal text-base flex';
