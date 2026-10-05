import '@testing-library/jest-dom';
import { HotelBrand, PurposeOfStay, PurposeOfStayAnalytics } from '@whitbread-eos/api';
import { FieldsType, FORM_FIELD_TYPES, FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { analytics, getAuthCookie, useAuthToken } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import AnonRFS from './AnonRFS.component';

const mockUpdateReasonForStay = jest.fn();
const basketReferenceId = 'GAA1828883';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAuthCookie: jest.fn().mockImplementation(() => ''),
  useAuthToken: jest.fn(() => ({ token: '', isAuth0Enabled: false, isLoading: false })),
  useCustomLocale: jest.fn().mockImplementation(() => ({ language: 'en' })),
  analytics: {
    update: jest.fn(),
  },
}));

const formField: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'anonRfs',
  label: 'anonRfs',
  props: {
    basketReferenceId,
    updateReasonForStay: mockUpdateReasonForStay,
  },
};

const getProps = (basketReferenceId: string, hotelBrand?: string): FormDynamicFieldCompProps => {
  return {
    formField: {
      ...formField,
      props: {
        ...formField.props,
        basketReferenceId,
        hotelBrand,
      },
    },
    field: { name: 'anonRfs', value: '', onChange: jest.fn(), onBlur: jest.fn() },
    handleSetValue: jest.fn(),
    handleResetField: jest.fn(),
    getValues: jest.fn(),
    reset: jest.fn(),
  };
};

describe('<AnonRFS />', () => {
  beforeEach(() => {
    mockUpdateReasonForStay.mockClear();
    window.localStorage.removeItem('formDetails');
    getAuthCookie.mockImplementation(() => '');
    (useAuthToken as jest.Mock).mockReturnValue({
      token: '',
      isAuth0Enabled: false,
      isLoading: false,
    });
  });
  it('should not call updateReasonForStay and update analytics bookingReasonForStay when hotelBrand is PID', () => {
    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.PID)} />);
    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
    expect(analytics.update).not.toHaveBeenCalled();
  });
  it('should call updateReasonForStay with default value and update analytics bookingReasonForStay when hotelBrand is HUB', () => {
    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.HUB)} />);
    expect(mockUpdateReasonForStay).toHaveBeenCalledWith(PurposeOfStay.LEISURE);
    expect(analytics.update).toHaveBeenCalledWith({
      bookingReasonForStay: PurposeOfStayAnalytics.LEISURE,
    });
  });
  it('should call updateReasonForStay with value retrieved from localStorage and update analytics bookingReasonForStay', () => {
    window.localStorage.setItem(
      'formDetails',
      JSON.stringify({ basketReferenceId: 'GAA1828883', reasonForStay: PurposeOfStay.BUSINESS })
    );
    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.PID)} />);
    expect(mockUpdateReasonForStay).toHaveBeenCalledWith(PurposeOfStay.BUSINESS);
    expect(analytics.update).toHaveBeenCalledWith({
      bookingReasonForStay: PurposeOfStayAnalytics.BUSINESS,
    });
  });
  it('should not call updateReasonForStay when basketReferenceId is not set', () => {
    render(<AnonRFS {...getProps('')} />);
    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
  });
  it('should not call updateReasonForStay when id_token_cookie is set', () => {
    getAuthCookie.mockImplementation(() => 'testjwt');
    (useAuthToken as jest.Mock).mockReturnValue({
      token: 'testjwt',
      isAuth0Enabled: false,
      isLoading: false,
    });
    render(<AnonRFS {...getProps(basketReferenceId)} />);
    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
  });

  it('should not replay the stored reason for stay for an Auth0-authenticated user', () => {
    // Auth0 issues an access token and no legacy cookie. Reading the cookie directly made
    // this user look anonymous, so the stored value was replayed over their own choice.
    window.localStorage.setItem(
      'formDetails',
      JSON.stringify({ basketReferenceId, reasonForStay: PurposeOfStay.BUSINESS })
    );
    getAuthCookie.mockImplementation(() => '');
    (useAuthToken as jest.Mock).mockReturnValue({
      token: 'an-auth0-access-token',
      isAuth0Enabled: true,
      isLoading: false,
    });

    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.PI)} />);

    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
  });

  it('should wait for the Auth0 token before treating the user as anonymous', () => {
    window.localStorage.setItem(
      'formDetails',
      JSON.stringify({ basketReferenceId, reasonForStay: PurposeOfStay.BUSINESS })
    );
    getAuthCookie.mockImplementation(() => '');
    (useAuthToken as jest.Mock).mockReturnValue({
      token: '',
      isAuth0Enabled: true,
      isLoading: true,
    });

    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.PI)} />);

    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
  });

  it('should not call updateReasonForStay with default value when localStorage formDetails is malformed and PI hotel', () => {
    window.localStorage.setItem('formDetails', 'not json');
    render(<AnonRFS {...getProps(basketReferenceId, HotelBrand.PI)} />);
    expect(mockUpdateReasonForStay).not.toHaveBeenCalled();
  });
});
