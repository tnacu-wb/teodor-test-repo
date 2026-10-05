import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import LiveAssist from './LiveAssist.component';

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useUserData: () => mockCustomLocale(),
}));
describe('LiveAssist ', () => {
  it('should not render the LiveAssist component if not logged in', function () {
    mockCustomLocale.mockReturnValue({ isLoggedIn: false });
    const LPMcontainer = document.createElement('div');
    LPMcontainer.className = 'LPMcontainer';
    const lpChat = document.createElement('div');
    lpChat.id = 'lpChat';
    document.body.appendChild(LPMcontainer);
    document.body.appendChild(lpChat);
    render(<LiveAssist />);
    expect(LPMcontainer).toHaveStyle('visibility: hidden');
    expect(lpChat).toHaveStyle('visibility: hidden');
  });

  it('should render the LiveAssist component if logged in', function () {
    mockCustomLocale.mockReturnValue({ isLoggedIn: true });
    const LPMcontainer = document.createElement('div');
    LPMcontainer.className = 'LPMcontainer';
    const lpChat = document.createElement('div');
    lpChat.id = 'lpChat';
    document.body.appendChild(LPMcontainer);
    document.body.appendChild(lpChat);
    render(<LiveAssist />);
    expect(LPMcontainer).toHaveStyle('visibility: visible');
    expect(lpChat).toHaveStyle('visibility: visible');
  });
});
