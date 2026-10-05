import {
  BUSINESS_BOOKER_USER_ROLES,
  LOCALES,
  PayApplicationDetails,
  PayApplicationStatus,
} from '@whitbread-eos/api';
import { Button, Skeleton } from '@whitbread-eos/atoms/ui';
import {
  getCountryLanguageByLocale,
  getTranslations,
  getPathForLocale,
  ID_TOKEN_COOKIE,
  getUserDetails,
} from '@whitbread-eos/utils/server';
import { Plus } from 'lucide-react';
import { cookies } from 'next/headers';
import Link from 'next/link';

import Analytics from '../Analytics/analytics';
import DeleteAction from './delete-action';

type Props = {
  locale: LOCALES;
  showApply?: boolean;
  applications?: PayApplicationDetails[];
};

export const getValidApplications = (applications: PayApplicationDetails[] | undefined) =>
  applications?.filter((application: PayApplicationDetails) =>
    [
      PayApplicationStatus.Approved,
      PayApplicationStatus.Started,
      PayApplicationStatus.Submitted,
    ].includes(application.status as PayApplicationStatus)
  ) ?? [];

const getTranslationKeyByStatus = (status: PayApplicationStatus) => {
  switch (status) {
    case PayApplicationStatus.Approved:
      return 'homepage.home.innbusinessPay.applications.approved';
    case PayApplicationStatus.Submitted:
      return 'homepage.home.innbusinessPay.applications.submitted';
    case PayApplicationStatus.Started:
      return 'homepage.home.innbusinessPay.applications.started';
    default:
      return status;
  }
};

export async function Applications({ locale, showApply = true, applications = [] }: Props) {
  const baseDataTestId = `Applications`;
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const [{ t }, userDetails] = await Promise.all([
    getTranslations(language, ['homepage']),
    getUserDetails(token),
  ]);
  const { accessLevel } = userDetails?.business ?? {};
  const isTravelManager = accessLevel === BUSINESS_BOOKER_USER_ROLES.SUPER;
  const isBusinessPayManager = accessLevel === BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER;

  if (!applications.length) {
    return <></>;
  }

  return (
    <section className={containerStyle} data-testid={`${baseDataTestId}-Container`}>
      <h2 className={h2Style} data-testid={`${baseDataTestId}-Title`}>
        {t('homepage.home.innbusinessPay.applications.heading')}
      </h2>
      <div className={appsStyle} data-testid={`${baseDataTestId}-List`}>
        {applications.map((application, index) => (
          <div
            key={`home-app-${index}`}
            data-testid={`${baseDataTestId}-ListItem-${application?.applicationGuid ?? index}`}
            className={`${appStyle} ${index === applications.length - 1 ? '' : 'border-b'}`}
          >
            <p
              className={companyNameStyle}
              data-testid={`${baseDataTestId}-ListItemCompanyName-${
                application?.applicationGuid ?? index
              }`}
            >
              {application?.accountName}
            </p>
            <p
              className={statusStyle}
              data-testid={`${baseDataTestId}-ListItemStatus-${
                application?.applicationGuid ?? index
              }`}
            >
              {t(getTranslationKeyByStatus(application.status as PayApplicationStatus))}
            </p>
            <div
              className={actionsStyle}
              data-testid={`${baseDataTestId}-ListItemActions-${
                application?.applicationGuid ?? index
              }`}
            >
              <p className={actionStyle}>
                <span
                  className={
                    application.status === PayApplicationStatus.Submitted
                      ? greenDotStyle
                      : yellowDotStyle
                  }
                  data-testid={`${baseDataTestId}-ListItemDot-${
                    application?.applicationGuid ?? index
                  }`}
                ></span>
                {application.status === PayApplicationStatus.Approved && (
                  <Link
                    className={linkStyle}
                    data-testid={`${baseDataTestId}-ListItemRegister-${
                      application?.applicationGuid ?? index
                    }`}
                    href={getPathForLocale(locale, `business-pay/register`)}
                  >
                    {t('homepage.home.innbusinessPay.applications.registerNow.link')}
                  </Link>
                )}
                {application.status === PayApplicationStatus.Started && (
                  <Link
                    className={linkStyle}
                    data-testid={`${baseDataTestId}-ListItemResume-${
                      application?.applicationGuid ?? index
                    }`}
                    href={getPathForLocale(
                      application.scheme === 'GB' ? LOCALES.EN : LOCALES.DE,
                      `business-pay/pay-application-resume?applicationId=${application.applicationId}&applicationGuid=${application.applicationGuid}`
                    )}
                    prefetch={false}
                  >
                    {t('homepage.home.innbusinessPay.applications.resume.link')}
                  </Link>
                )}
                {application.status === PayApplicationStatus.Submitted && (
                  <span
                    data-testid={`${baseDataTestId}-ListItemInReview-${
                      application?.applicationGuid ?? index
                    }`}
                  >
                    {t('homepage.home.innbusinessPay.applications.inReview')}
                  </span>
                )}
              </p>
              {application.status === PayApplicationStatus.Started && (
                <DeleteAction
                  application={application}
                  baseDataTestId={`${baseDataTestId}-ListItemDelete-${
                    application?.applicationGuid ?? index
                  }`}
                />
              )}
            </div>
          </div>
        ))}
        {showApply && (isTravelManager || isBusinessPayManager) && (
          <div>
            <Link
              href={getPathForLocale(locale, 'business-pay/apply')}
              data-testid={`${baseDataTestId}-AddApplication`}
              prefetch={false}
            >
              <Button variant={'grey'} className={buttonStyle}>
                <Plus size={24} />
                <span>
                  {t('homepage.home.innbusinessPay.applications.applyForNewAccount.button')}
                </span>
              </Button>
            </Link>
          </div>
        )}
      </div>
      <Analytics applications={applications.length} />
    </section>
  );
}

type SkeletonProps = {
  t: (key: string) => string;
};

export function ApplicationsSkeleton({ t }: SkeletonProps) {
  return (
    <section className={containerStyle} data-testid={'Applications-Skeleton'}>
      <h2 className={h2Style}>{t('homepage.home.innbusinessPay.applications.heading')}</h2>
      <Skeleton className={'h-12 md:h-6 w-full mb-4 mt-4'} />
      <Skeleton className={'h-12 md:h-6 w-full mb-4'} />
      <Skeleton className={'h-12 md:h-6 w-full mb-4'} />
      <Skeleton className={'h-[40px] w-full md:w-1/5 mt-4'} />
    </section>
  );
}

const containerStyle = 'mt-4 border-[1px] border-lightGrey3 p-6 rounded-md';
const h2Style = 'text-[1.44rem] leading-[2rem] font-bold mb-2';
const appsStyle = 'flex flex-col';
const appStyle =
  'flex flex-col md:flex-row justify-between md:items-center border-lightGrey3 py-4 md:py-2 text-left';
const companyNameStyle = 'font-bold flex-1';
const statusStyle = 'text-sm flex-1';
const actionsStyle = 'flex-[0.7] flex flex-row-reverse md:flex-row justify-between mt-2 md:mt-0';
const actionStyle = 'flex-[0.6] flex flex-row items-center gap-2 justify-end md:justify-start';
const linkStyle = 'underline text-secondaryColor underline-offset-2';
const greenDotStyle = 'h-[12px] w-[12px] bg-success rounded-full';
const yellowDotStyle = 'h-[12px] w-[12px] bg-warning rounded-full';
const buttonStyle = 'mt-4 flex w-full md:w-auto items-center gap-3 text-lg px-4 h-[40px]';
