'use client';

import { SpendingData } from '@whitbread-eos/api';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

type Props = {
  dataTestId: string;
  spendingData: SpendingData[];
  formatSpendingValue: (value: number, isViewList?: boolean, bookingCurrency?: string) => string;
  monthsSpendings: SpendingData[];
  isAccountSuspended?: boolean;
};

export function ViewListByMonth({
  dataTestId,
  spendingData,
  formatSpendingValue,
  monthsSpendings,
  isAccountSuspended = false,
}: Props) {
  const { t } = useTranslation('spending');

  const spendingPerMonths = spendingData.map((item, index) => {
    return (
      item.spending !== 0 && (
        <ul
          key={`List-By-Month-${index}`}
          className="spend-over-time-item flex justify-between text-base border-b border-b-lightGrey3 pb-[.5rem] mb-[.5rem]"
          data-testid={`${dataTestId}-Spend-Over-Time-Per-Months-Item`}
        >
          <li>{item.month}</li>
          <li className="font-semibold">
            {formatSpendingValue(item.spending, true, item.bookingCurrency)}
          </li>
        </ul>
      )
    );
  });

  return (
    <div
      className="view-list-by-month-wrapper"
      data-testid={`${dataTestId}-View-List-By-Month-Desktop`}
    >
      {!isAccountSuspended && (
        <Accordion type="single" collapsible className={accordionStyle}>
          <AccordionItem value="item" className={'border-0'}>
            <AccordionTrigger
              className="py-0 hover:no-underline"
              data-testid={`${dataTestId}-Accordion-Trigger`}
            >
              <h1 data-testid={`${dataTestId}-Accordion-Title`} className={h1Style}>
                {t('spending.filterby.month')}
              </h1>
            </AccordionTrigger>
            <AccordionContent className="pb-0" data-testid={`${dataTestId}-Accordion-Content`}>
              <div
                data-testid={`${dataTestId}-Spend-Over-Time-Per-Months`}
                className={`mt-[1rem] ${
                  monthsSpendings.length > 6 ? 'grid grid-rows-6 grid-flow-col gap-x-12' : ''
                }`}
              >
                {spendingPerMonths}
              </div>
            </AccordionContent>
          </AccordionItem>
        </Accordion>
      )}
      <div className={mobileWrapperStyle} data-testid={`${dataTestId}-View-List-By-Month-Mobile`}>
        {!isAccountSuspended ? spendingPerMonths : t('spending.overPeriod.suspendedAccount')}
      </div>
    </div>
  );
}

const accordionStyle = 'mobile:hidden w-full bg-lightGrey5 py-[.5rem] px-[1rem]';
const h1Style = 'leading-[1.5rem] text-[1.125rem] text-secondaryColor font-semibold my-[.5rem]';
const mobileWrapperStyle =
  'mobile:block hidden w-full bg-lightGrey5 py-[.5rem] px-[1rem] mt-[1.5rem]';
