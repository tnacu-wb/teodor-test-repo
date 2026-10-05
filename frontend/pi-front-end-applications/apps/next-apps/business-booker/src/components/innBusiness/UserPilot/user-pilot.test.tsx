import { render } from '@testing-library/react';
import React from 'react';

import UserPilot from './user-pilot';

const pathMock = jest.fn();
const identifyMock = jest.fn();
const reloadMock = jest.fn();
// Mock next/navigation
jest.mock('next/navigation', () => ({
  usePathname: jest.fn(),
}));

// Mock userpilot

jest.mock('userpilot', () => ({
  Userpilot: {
    initialize: jest.fn(),
    identify: (...args: any) => identifyMock(...args),
    reload: () => reloadMock(),
  },
}));

describe('UserPilot', () => {
  const userPilotData = {
    employeeId: 'emp123',
    innBusinessPayRoles: ['admin', 'user'],
    role: 'admin',
    companyId: 'comp456',
    companySector: 'Hospitality',
    numberOfEmployees: 100,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    pathMock.mockReturnValue('/');
    process.env.NEXT_PUBLIC_USER_PILOT_APP_TOKEN = 'test-token';
  });

  it('calls Userpilot.identify with correct arguments on mount', () => {
    render(<UserPilot userPilotData={userPilotData} />);
    expect(identifyMock).toHaveBeenCalledWith('emp123', {
      role: 'admin',
      innBusinessPayRoles: 'admin,user',
      company: {
        id: 'comp456',
        companySector: 'Hospitality',
        numberOfEmployees: 100,
      },
    });
  });

  it('should set required user pilot settings on browser window', () => {
    render(<UserPilot userPilotData={userPilotData} />);
    expect(window.userpilotSettings).toEqual({
      token: 'test-token',
      connection: 'polling',
    });
  });

  it('calls Userpilot.reload when pathname changes', () => {
    const { rerender } = render(<UserPilot userPilotData={userPilotData} />);

    pathMock.mockReturnValue('/settings');
    rerender(<UserPilot userPilotData={userPilotData} />);
    expect(reloadMock).toHaveBeenCalled();
  });
});
