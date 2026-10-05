import { type Claims, UserRoles } from '@whitbread-eos/api';

import analytics from './analytics';

export default function setAnalyticsUser(user: Claims, locale: string | undefined) {
  if (!user) {
    return;
  }

  let analyticsUserLevel = '';

  const userRole = user['https://ccui.opera.whitbread.digital/role'][0];

  if (userRole !== UserRoles.MANAGER) {
    analyticsUserLevel = locale === 'en' ? 'CCUI Agent' : 'CCUI DE Agent';
  } else {
    analyticsUserLevel = locale === 'en' ? 'CCUI Manager' : 'CCUI DE Manager';
  }

  analytics.update({
    userLevel: analyticsUserLevel,
    userID: 'CCUI_User',
    userLoggedIn: 'Logged in',
    CCUI: true,
    dashboard: {
      userAgentEmail: user.email as string,
    },
  });
}
