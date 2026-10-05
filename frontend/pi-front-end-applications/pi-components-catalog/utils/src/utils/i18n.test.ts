import { useTranslation } from './i18n';

const mockLabelsResponse = { label: 'testLabel', 'userMan.test': 'test' };

jest.mock('../server/index.ts', () => ({
  getInnBusinessHeaderLabels: () => mockLabelsResponse,
  getCardManagementLabels: () => mockLabelsResponse,
  getFooterLabels: () => mockLabelsResponse,
  getUserManagementLabels: () => mockLabelsResponse,
  getHomepageLabels: () => mockLabelsResponse,
  getSpendingLabels: () => mockLabelsResponse,
  getPayApplicationLabels: () => mockLabelsResponse,
  getAuthLabels: () => mockLabelsResponse,
  getProfileManagementLabels: () => mockLabelsResponse,
  getCompanyManagementLabels: () => mockLabelsResponse,
  getNotificationsLabels: () => mockLabelsResponse,
  getInnBusinessLayoutLabels: () => mockLabelsResponse,
  getContactUsLabels: () => mockLabelsResponse,
  getCommonIcons: () => mockLabelsResponse,
}));

describe('useTranslation', () => {
  it('should load labels for EN and common namespace', async () => {
    const { t } = await useTranslation('en');
    expect(t('label')).toEqual('testLabel');
  });

  it('should load labels for DE and common namespace', async () => {
    const { t } = await useTranslation('de');
    expect(t('label')).toEqual('testLabel');
  });

  it('should load labels for EN and cards namespace', async () => {
    const { t } = await useTranslation('en', 'cards');
    expect(t('label')).toEqual('testLabel');
  });

  it('should load labels for EN and footer namespace', async () => {
    const { t } = await useTranslation('en', 'footer');
    expect(t('label')).toEqual('testLabel');
  });
  it('should load labels for EN and users namespace', async () => {
    const { t } = await useTranslation('en', 'users');
    expect(t('label')).toEqual('testLabel');
  });

  it('should load labels for EN and contact namespace', async () => {
    const { t } = await useTranslation('en', 'contact');
    expect(t('label')).toEqual('testLabel');
  });

  it('should load labels for EN and undefined namespace', async () => {
    const { t } = await useTranslation('en', 'NON-EXISTENT-NAMESPACE');
    expect(t('label')).toEqual('label');
  });

  it('should load labels for EN and multiple namespaces', async () => {
    const { t } = await useTranslation('en', [
      'auth',
      'common',
      'cards',
      'contact',
      'footer',
      'users',
      'homepage',
      'spending',
      'profile',
      'company',
      'payApplication',
      'notifications',
      'layout',
      'icons',
    ]);
    expect(t('common.label')).toEqual('testLabel');
    expect(t('cards.label')).toEqual('testLabel');
    expect(t('footer.label')).toEqual('testLabel');
    expect(t('users.label')).toEqual('testLabel');
    expect(t('homepage.label')).toEqual('testLabel');
    expect(t('spending.label')).toEqual('testLabel');
    expect(t('users.userMan.test')).toEqual('test');
    expect(t('profile.label')).toEqual('testLabel');
    expect(t('company.label')).toEqual('testLabel');
    expect(t('payApplication.label')).toEqual('testLabel');
    expect(t('auth.label')).toEqual('testLabel');
    expect(t('notifications.label')).toEqual('testLabel');
    expect(t('layout.label')).toEqual('testLabel');
    expect(t('contact.label')).toEqual('testLabel');
    expect(t('icons.label')).toEqual('testLabel');
  });
});
