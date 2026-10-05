import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { RegistrationRole, BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';

import { RolesCheck } from './roles-check';

describe('RolesCheck Component', () => {
  it('renders children when no user roles are passed', () => {
    const { container } = render(
      <RolesCheck>
        <div>Child Component</div>
      </RolesCheck>
    );
    expect(container.firstChild).toBeNull();
  });

  it('renders children when user roles are not excluded', () => {
    const userRoles = {
      wl: [RegistrationRole.AccountHolder],
      bb: [BUSINESS_BOOKER_USER_ROLES.STAYER],
    };

    const { getByText } = render(
      <RolesCheck userRoles={userRoles}>
        <div>Child Component</div>
      </RolesCheck>
    );

    expect(getByText('Child Component')).toBeInTheDocument();
  });

  it('does not render children when user roles are excluded for wl', () => {
    const userRoles = {
      wl: [RegistrationRole.CardHolder],
    };

    const excludedForRoles = {
      wl: [RegistrationRole.CardHolder, RegistrationRole.AccountHolder],
    };

    const { container } = render(
      <RolesCheck userRoles={userRoles} excludedForRoles={excludedForRoles}>
        <div>Child Component</div>
      </RolesCheck>
    );

    expect(container.firstChild).toBeNull();
  });

  it('does not render children when user roles are excluded for bb', () => {
    const userRoles = {
      bb: [BUSINESS_BOOKER_USER_ROLES.BOOKER],
    };

    const excludedForRoles = {
      bb: [BUSINESS_BOOKER_USER_ROLES.STAYER, BUSINESS_BOOKER_USER_ROLES.BOOKER],
    };

    const { container } = render(
      <RolesCheck userRoles={userRoles} excludedForRoles={excludedForRoles}>
        <div>Child Component</div>
      </RolesCheck>
    );

    expect(container.firstChild).toBeNull();
  });

  it('renders children when user roles are partially excluded', () => {
    const userRoles = {
      wl: [RegistrationRole.AccountHolder],
      bb: [BUSINESS_BOOKER_USER_ROLES.STAYER],
    };

    const excludedForRoles = {
      wl: [RegistrationRole.CardHolder],
      bb: [BUSINESS_BOOKER_USER_ROLES.SUPER],
    };

    const { getByText } = render(
      <RolesCheck userRoles={userRoles} excludedForRoles={excludedForRoles}>
        <div>Child Component</div>
      </RolesCheck>
    );

    expect(getByText('Child Component')).toBeInTheDocument();
  });
});
