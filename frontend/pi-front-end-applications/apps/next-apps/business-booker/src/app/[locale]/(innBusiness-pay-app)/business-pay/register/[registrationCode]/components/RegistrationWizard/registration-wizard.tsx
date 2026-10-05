import { RegistrationCodeInfo, LOCALES } from '@whitbread-eos/api';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getCountryLanguageByLocale,
  getCommonIcons,
  getTranslations,
  formatIBAssetsUrl,
  authenticateRegistration,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { DetailsForm } from '../DetailsForm';
import { QuestionsForm } from '../QuestionsForm';
import { RegisterIbStep, RegistrationState } from '../types';

type Props = {
  baseDataTestId: string;
  locale: LOCALES;
  registrationCodeInfo: RegistrationCodeInfo;
};

async function RegistrationWizard({ baseDataTestId, locale, registrationCodeInfo }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const [icons, { t }] = await Promise.all([
    getCommonIcons(language),
    getTranslations(language, ['common', 'auth']),
  ]);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const hasQuestions =
    registrationCodeInfo?.authenticationQuestions &&
    registrationCodeInfo?.authenticationQuestions.length > 0;
  const isFinanceUser = registrationCodeInfo?.registrationRole === 'FinanceUser';

  let wizardState: RegistrationState = {
    prepopulatedItems: null,
    authenticationAnswers: [],
  };
  if (isFinanceUser || !hasQuestions) {
    const { registrationPrePopulatedItems } = (await authenticateRegistration(
      token,
      registrationCodeInfo.registrationCode
    )) ?? {
      registrationPrePopulatedItems: null,
    };
    wizardState = {
      ...wizardState,
      prepopulatedItems: registrationPrePopulatedItems,
    };
  }

  const getStepsByRegistrationInfo = () => {
    const steps = [
      {
        id: RegisterIbStep.DETAILS_FORM,
        component: (
          <DetailsForm
            icons={icons}
            locale={locale}
            baseDataTestId={baseDataTestId}
            registrationInfo={registrationCodeInfo}
            token={token}
            isOnlyStep={isFinanceUser || !hasQuestions}
          />
        ),
      },
    ];

    if (!isFinanceUser && hasQuestions) {
      steps.unshift({
        id: RegisterIbStep.QUESTIONS_FORM,
        component: (
          <QuestionsForm
            locale={locale}
            baseDataTestId={baseDataTestId}
            icons={icons}
            registrationInfo={registrationCodeInfo}
            token={token}
          />
        ),
      });
    }

    return steps;
  };

  return (
    <Wizard<RegistrationState>
      icons={icons}
      header={<WizardHeader logoUrl={formatIBAssetsUrl(t('common.content.header.image'))} />}
      initialState={wizardState}
      initialStepId={
        !isFinanceUser && hasQuestions ? RegisterIbStep.QUESTIONS_FORM : RegisterIbStep.DETAILS_FORM
      }
      steps={getStepsByRegistrationInfo()}
    />
  );
}

export { RegistrationWizard };
