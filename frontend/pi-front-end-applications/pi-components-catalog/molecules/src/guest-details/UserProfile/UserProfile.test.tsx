import '@testing-library/jest-dom';
import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import {
  UserDataContext,
  useAuthToken,
  useAuth0User,
  useRestQueryRequest,
} from '@whitbread-eos/utils';

import { render, waitFor } from '../../utils/test-utils';
import UserProfile from './UserProfile';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const useQueryResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: {},
  data: {
    contactDetail: {
      title: 'Mr',
      firstName: 'firstname',
      lastName: 'lastname',
      email: 'email',
      mobile: 'mobile',
      address: {
        companyName: 'whitbread',
        countryCode: 'GB',
        countryCodeISO: 'GB',
        line1: 'Street',
        line2: 'Street 2',
        line3: 'Street 3',
        line4: 'Street 4',
        postCode: '123',
        type: '',
      },
    },
  },
};

const invalidCountryCodeResponse = {
  contactDetail: {
    title: 'Mr',
    firstName: 'firstname',
    lastName: 'lastname',
    email: 'email',
    mobile: 'mobile',
    address: {
      companyName: 'whitbread',
      countryCode: '',
      countryCodeISO: '',
      line1: 'Street',
      line2: 'Street 2',
      line3: 'Street 3',
      line4: 'Street 4',
      postCode: '123',
      type: '',
    },
  },
};

const businessUseResponse = {
  businessUse: true,
  contactDetail: {
    title: 'Mr',
    firstName: 'firstname',
    lastName: 'lastname',
    email: 'email',
    mobile: 'mobile',
    address: {
      companyName: 'whitbread',
      countryCode: 'GB',
      countryCodeISO: 'GB',
      line1: 'Street',
      line2: 'Street 2',
      line3: 'Street 3',
      line4: 'Street 4',
      postCode: '123',
      type: '',
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestQueryRequest: jest.fn(() => useQueryResponse),
  useAuthToken: jest.fn(() => ({
    token: 'mock-token',
    isAuth0Enabled: false,
    isLoading: false,
  })),
  useAuth0User: jest.fn(() => ({
    user: null,
  })),
}));

describe('User Profile ', () => {
  const reset = jest.fn();
  const formField = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'userProfile',
    label: 'userProfile',
    props: {
      currentLang: 'de',
      setIsLocationRequired: jest.fn(),
      setCountryOfResidence: jest.fn(),
    },
  };
  const field = {
    name: 'userProfile',
    value: '',
    onChange: jest.fn(),
    onBlur: jest.fn(),
  };
  const getValues = jest.fn();
  const getComponent = () => {
    return <UserProfile formField={formField} field={field} getValues={getValues} reset={reset} />;
  };
  beforeEach(() => {
    reset.mockReset();
  });
  it('should call handleSetValue function', () => {
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        {getComponent()}
      </UserDataContext.Provider>
    );
    expect(reset).toHaveBeenCalled();
  });

  it('should call handleSetValue function for Business Use true', () => {
    useQueryResponse.data = businessUseResponse;
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        {getComponent()}
      </UserDataContext.Provider>
    );
    expect(reset).toHaveBeenCalled();
  });

  it('should update country code properly for DE site', async () => {
    useQueryResponse.data = invalidCountryCodeResponse;
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        {getComponent()}
      </UserDataContext.Provider>
    );
    await waitFor(() => {
      expect(reset).toHaveBeenCalledWith(
        expect.objectContaining({
          countryCode: 'DE',
        })
      );
    });
  });

  it('should update country code properly for UK site', async () => {
    formField.props.currentLang = 'en';
    useQueryResponse.data = invalidCountryCodeResponse;
    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        {getComponent()}
      </UserDataContext.Provider>
    );
    await waitFor(() => {
      expect(reset).toHaveBeenCalledWith(
        expect.objectContaining({
          countryCode: 'GB',
        })
      );
    });
  });

  it('should not call handleSetValue function', () => {
    useQueryResponse.isSuccess = false;

    render(
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        {getComponent()}
      </UserDataContext.Provider>
    );
    expect(reset).not.toHaveBeenCalled();
  });

  it('should not call handleUserProfileUpdate when isRemovePIIDataFromLocalStorageEnabled is true and basketReferenceId is present', () => {
    useQueryResponse.isSuccess = true;
    (useRestQueryRequest as jest.Mock).mockReturnValue(useQueryResponse);

    const formFieldWithPIIFlag = {
      ...formField,
      props: {
        ...formField.props,
        isRemovePIIDataFromLocalStorageEnabled: true,
        defaultValues: { basketReferenceId: 'AJK-test-basket-reference-id' },
      },
    };

    render(
      <UserDataContext.Provider value={{ isLoggedIn: true, setIsLoggedIn: () => {} }}>
        <UserProfile
          formField={formFieldWithPIIFlag}
          field={field}
          getValues={getValues}
          reset={reset}
        />
      </UserDataContext.Provider>
    );

    expect(reset).not.toHaveBeenCalled();
  });

  it('should call handleUserProfileUpdate when isRemovePIIDataFromLocalStorageEnabled is true but basketReferenceId is absent', async () => {
    useQueryResponse.isSuccess = true;
    (useRestQueryRequest as jest.Mock).mockReturnValue(useQueryResponse);

    const formFieldWithPIIFlagNoBasket = {
      ...formField,
      props: {
        ...formField.props,
        isRemovePIIDataFromLocalStorageEnabled: true,
        defaultValues: { basketReferenceId: '' },
      },
    };

    render(
      <UserDataContext.Provider value={{ isLoggedIn: true, setIsLoggedIn: () => {} }}>
        <UserProfile
          formField={formFieldWithPIIFlagNoBasket}
          field={field}
          getValues={getValues}
          reset={reset}
        />
      </UserDataContext.Provider>
    );

    await waitFor(() => {
      expect(reset).toHaveBeenCalled();
    });
  });

  describe('Auth0 integration', () => {
    beforeEach(() => {
      useQueryResponse.isSuccess = true;
      (useRestQueryRequest as jest.Mock).mockReturnValue(useQueryResponse);
      (useAuthToken as jest.Mock).mockReturnValue({
        token: 'mock-token',
        isAuth0Enabled: false,
        isLoading: false,
      });
      (useAuth0User as jest.Mock).mockReturnValue({ user: null });
    });

    it('should use Auth0 user email when Auth0 is enabled', async () => {
      (useAuthToken as jest.Mock).mockReturnValue({
        token: 'sample',
        isAuth0Enabled: true,
        isLoading: false,
      });
      (useAuth0User as jest.Mock).mockReturnValue({
        user: { email: 'auth0user@example.com' },
      });

      render(
        <UserDataContext.Provider value={{ isLoggedIn: true, setIsLoggedIn: jest.fn() }}>
          {getComponent()}
        </UserDataContext.Provider>
      );

      await waitFor(() => {
        expect(reset).toHaveBeenCalled();
      });
    });

    it('should not fetch user data while token is still loading', () => {
      (useAuthToken as jest.Mock).mockReturnValue({
        token: null,
        isAuth0Enabled: true,
        isLoading: true,
      });
      (useAuth0User as jest.Mock).mockReturnValue({ user: null });
      (useRestQueryRequest as jest.Mock).mockReturnValue({
        isSuccess: false,
        data: undefined,
        isFetching: false,
      });

      render(
        <UserDataContext.Provider value={{ isLoggedIn: true, setIsLoggedIn: jest.fn() }}>
          {getComponent()}
        </UserDataContext.Provider>
      );

      expect(reset).not.toHaveBeenCalled();
    });

    it('should fall back to legacy token email when Auth0 is disabled', async () => {
      (useAuthToken as jest.Mock).mockReturnValue({
        token: 'sample',
        isAuth0Enabled: false,
        isLoading: false,
      });
      (useAuth0User as jest.Mock).mockReturnValue({ user: null });

      render(
        <UserDataContext.Provider value={{ isLoggedIn: true, setIsLoggedIn: jest.fn() }}>
          {getComponent()}
        </UserDataContext.Provider>
      );

      await waitFor(() => {
        expect(reset).toHaveBeenCalled();
      });
    });
  });
});
