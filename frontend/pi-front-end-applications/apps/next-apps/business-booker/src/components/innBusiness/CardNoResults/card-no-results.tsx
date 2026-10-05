import { LOCALES } from '@whitbread-eos/api';
import { TableRow, TableCell } from '@whitbread-eos/atoms/ui';
import { cn, getCountryLanguageByLocale, getTranslations } from '@whitbread-eos/utils/server';
import Link from 'next/link';

export type UserNoResultsProps = {
  locale?: LOCALES;
  className?: string;
  addCardUrl: string;
  hideAddCardButton?: boolean;
};

export async function CardNoResults({
  locale,
  className,
  addCardUrl,
  hideAddCardButton = false,
}: UserNoResultsProps) {
  const baseDataTestId = 'CardNoResults';
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'cards');

  return (
    <TableRow className={tableRowStyle}>
      <TableCell className={tableCellStyle} colSpan={6}>
        <div data-testid={baseDataTestId} className={cn(containerStyle, className)}>
          <div className={textStyle}>{t('cardMgmt.centrallyStored.noCards.message')}</div>
          {!hideAddCardButton && (
            <Link href={addCardUrl} className={linkStyle}>
              {t('cardMgmt.newCard.label')}
            </Link>
          )}
        </div>
      </TableCell>
    </TableRow>
  );
}

const textStyle = 'font-bold text-lg mb-2';
const linkStyle = 'flex font-medium text-base underline text-secondaryColor';
const containerStyle = 'flex flex-col py-4 px-6 w-full';
const tableCellStyle = 'px-0 border-t-0';
const tableRowStyle = 'hover:bg-transparent';
