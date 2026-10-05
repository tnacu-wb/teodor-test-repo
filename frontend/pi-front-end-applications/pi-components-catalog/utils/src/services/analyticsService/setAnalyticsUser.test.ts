import { type Claims, UserRoles } from '@whitbread-eos/api';

import analytics from './analytics';
import setAnalyticsUser from './setAnalyticsUser';

const analyticsUpdateSpy = jest.spyOn(analytics, 'update').mockReturnValue(undefined);

describe('setAnalyticsUser', () => {
  describe('setAnalyticsUser Method', () => {
    afterEach(() => {
      analyticsUpdateSpy.mockReset();
    });

    it('should not call analytics.update if no user is provided', () => {
      setAnalyticsUser(null as unknown as Claims, 'en');
      expect(analyticsUpdateSpy).not.toHaveBeenCalledTimes(1);
    });

    it('should call analytics update with english ccui agent role', () => {
      setAnalyticsUser(
        {
          ['https://ccui.opera.whitbread.digital/role']: [UserRoles.AGENT],
          email: 'agent@ccui.com',
        },
        'en'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        userLevel: 'CCUI Agent',
        userID: 'CCUI_User',
        userLoggedIn: 'Logged in',
        CCUI: true,
        dashboard: {
          userAgentEmail: 'agent@ccui.com',
        },
      });
    });

    it('should call analytics update with english ccui manager role', () => {
      setAnalyticsUser(
        {
          ['https://ccui.opera.whitbread.digital/role']: [UserRoles.MANAGER],
          email: 'manager@ccui.com',
        },
        'en'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        userLevel: 'CCUI Manager',
        userID: 'CCUI_User',
        userLoggedIn: 'Logged in',
        CCUI: true,
        dashboard: {
          userAgentEmail: 'manager@ccui.com',
        },
      });
    });

    it('should call analytics update with german ccui agent role', () => {
      setAnalyticsUser(
        {
          ['https://ccui.opera.whitbread.digital/role']: [UserRoles.AGENT],
          email: 'agent@ccui.com',
        },
        'de'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        userLevel: 'CCUI DE Agent',
        userID: 'CCUI_User',
        userLoggedIn: 'Logged in',
        CCUI: true,
        dashboard: {
          userAgentEmail: 'agent@ccui.com',
        },
      });
    });

    it('should call analytics update with german ccui manager role', () => {
      setAnalyticsUser(
        {
          ['https://ccui.opera.whitbread.digital/role']: [UserRoles.MANAGER],
          email: 'manager@ccui.com',
        },
        'de'
      );
      expect(analyticsUpdateSpy).toHaveBeenCalledWith({
        userLevel: 'CCUI DE Manager',
        userID: 'CCUI_User',
        userLoggedIn: 'Logged in',
        CCUI: true,
        dashboard: {
          userAgentEmail: 'manager@ccui.com',
        },
      });
    });
  });
});
