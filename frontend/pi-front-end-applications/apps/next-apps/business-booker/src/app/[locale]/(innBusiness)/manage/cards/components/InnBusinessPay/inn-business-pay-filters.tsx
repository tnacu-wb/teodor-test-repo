import { LOCALES } from '@whitbread-eos/api';
import { SearchParamLink, Checkbox } from '@whitbread-eos/atoms/ui';
import {
  getTranslations,
  getSearchParams,
  getCountryLanguageByLocale,
  getAccessLevel,
} from '@whitbread-eos/utils/server';

import { INN_BUSINESS_PAY_TAB_SEARCH_PARAM } from '~components/constants/constants';

type Props = {
  locale?: LOCALES;
  isCardHolderOnly?: boolean;
};

export async function InnBusinessPayFilters({ locale, isCardHolderOnly }: Props) {
  const baseDataTestId = 'InnBusinessPayFilters';
  const { language } = getCountryLanguageByLocale(locale);
  const [{ t }, { isTravelManager, hasAccountHolder }, searchParams] = await Promise.all([
    getTranslations(language, 'cards'),
    getAccessLevel(),
    getSearchParams(),
  ]);

  return (
    <div data-testid={baseDataTestId} className={filtersStyle}>
      <div className={showStyle}>{t('cardMgmt.filters.show')}:</div>

      <div className={checkboxesStyle}>
        <SearchParamLink
          data-testid={`${baseDataTestId}-cancelledCards`}
          name={INN_BUSINESS_PAY_TAB_SEARCH_PARAM.CANCELLED_CARDS}
          toggle
          clear={[INN_BUSINESS_PAY_TAB_SEARCH_PARAM.PAGE_INDEX]}
          className={filterStyle}
          searchParams={searchParams}
        >
          <Checkbox
            id="cancelledCards"
            checked={!!searchParams.get(INN_BUSINESS_PAY_TAB_SEARCH_PARAM.CANCELLED_CARDS)}
            className={checkboxStyle}
          />
          <label htmlFor="cancelledCards" className={labelStyle}>
            {t('cardMgmt.filters.cancelledCards')}
          </label>
        </SearchParamLink>

        {(isTravelManager || hasAccountHolder) && !isCardHolderOnly && (
          <SearchParamLink
            data-testid={`${baseDataTestId}-onlyMyCards`}
            name={INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ONLY_MY_CARDS}
            toggle
            clear={[INN_BUSINESS_PAY_TAB_SEARCH_PARAM.PAGE_INDEX]}
            className={filterStyle}
            searchParams={searchParams}
          >
            <Checkbox
              id="onlyMyCards"
              checked={!!searchParams.get(INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ONLY_MY_CARDS)}
              className={checkboxStyle}
            />
            <label htmlFor="onlyMyCards" className={labelStyle}>
              {t('cardMgmt.filters.myCards')}
            </label>
          </SearchParamLink>
        )}
      </div>
    </div>
  );
}

const filtersStyle = 'flex mobile:mt-10 mobile:flex-wrap items-center gap-y-2';
const filterStyle = 'flex items-center';
const showStyle = 'font-semibold';
const checkboxStyle = 'ml-10 mobile:ml-2 mr-2';
const checkboxesStyle = 'flex';
const labelStyle = 'cursor-pointer';
