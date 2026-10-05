import { PathParams } from '@whitbread-eos/api';
import { Skeleton } from '@whitbread-eos/atoms/ui';
import { Wizard, WizardHeader, WizardPage } from '@whitbread-eos/layout';
import {
  getTranslations,
  formatIBAssetsUrl,
  getCommonIcons,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils/server';

type Props = {
  params?: Promise<PathParams>;
};

export default async function Loading({ params }: Props) {
  const resolvedParams = await params;
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const icons = await getCommonIcons(language);
  const { t } = await getTranslations(language, ['common', 'auth', 'users']);

  return (
    <>
      <Wizard
        header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
        icons={icons}
        initialState={{}}
        initialStepId="loading"
        steps={[
          {
            id: 'loading',
            component: (
              <WizardPage
                type="form"
                showBackButton={false}
                data-testid={'Register-StepTwo-Skeleton'}
              >
                <Skeleton className={'w-1/2 h-[5.5rem]'} />
                <Skeleton className={'w-1/2 h-[1.5rem] mt-[1rem] mb-[3rem]'} />
                <div className={'flex flex-col gap-6'}>
                  <Skeleton className={'w-1/2 h-[3.5rem]'} />
                  <Skeleton className={'w-full h-[3.5rem]'} />
                  <Skeleton className={'w-full h-[3.5rem]'} />
                  <Skeleton className={'w-full h-[3.5rem]'} />
                </div>
                <Skeleton className={'w-full h-[4rem] mt-[2rem]'} />
              </WizardPage>
            ),
          },
        ]}
      />
    </>
  );
}
