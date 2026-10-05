import { analytics } from '@whitbread-eos/utils';
import { ControllerRenderProps } from 'react-hook-form';

import {
  fieldProps,
  getNationality,
  showDependencyPassportField,
  renderDropdownStyles,
  handleAccordionToggle,
  scrollIntoViewOnError,
  handleNationalityChange,
  showPassportField,
  getGuestDetailsObject,
  GuestDetailKeys,
  getPaymentParams,
  generateDependentsData,
  onIframeLoad,
  isCanvasEmpty,
  addScrollEvent,
  handleIframeHeight,
  handleRedirect,
} from './common';

jest.useFakeTimers();

describe('common test cases', () => {
  it('should return the field properties', () => {
    const field = {
      name: 'testField',
      onBlur: jest.fn(),
      value: 'testValue',
      onChange: jest.fn(),
    };
    const props = fieldProps(field);

    expect(props.name).toBe(field.name);
    expect(props.onBlur).toBe(field.onBlur);
    expect(props.value).toBe(field.value);

    const str = 'PremierInn';
    props.onChange(str);

    expect(field.onChange).toHaveBeenCalledWith(str);
  });
  it('check the nationality value', () => {
    const values = { dependents: [{ nationality: { value: 'de' } }] };
    const index = 0;
    expect(showDependencyPassportField(values, index)).toBe(true);
  });

  it('should return false if dependent nationality is undefined', () => {
    const values = {
      dependents: [{ nationality: undefined }],
    };
    const index = 0;
    const result = showDependencyPassportField(values, index);
    expect(result).toBe(true);
  });

  it('should return the nationality based on the field', () => {
    const field: ControllerRenderProps<any, any> = {
      value: 'de',
      onChange: jest.fn(),
      onBlur: jest.fn(),
      name: 'nationality',
      ref: jest.fn(),
    };
    const nationalities = [
      { value: 'us', label: 'United States' },
      { value: 'de', label: 'Germany' },
      { value: 'fr', label: 'France' },
    ];

    const result = getNationality(field, nationalities);
    expect(result).toEqual({ value: 'de', label: 'Germany' });
  });

  it('should return the correct styles when hasError is true', () => {
    const result = renderDropdownStyles(true);
    expect(result).toEqual({
      border: '1px solid',
      borderColor: 'var(--chakra-colors-error)',
      borderRadius: 'var(--chakra-radii-md)',
    });
  });
  it('should return the correct styles when hasError is false', () => {
    const result = renderDropdownStyles(false);
    expect(result).toEqual({
      border: '1px solid',
      borderColor: 'lightGrey1',
      borderRadius: 'var(--chakra-radii-md)',
    });
  });

  it('should set accordion index to 0 if the current index is 1', () => {
    const setAccordionIndexMock = jest.fn();
    handleAccordionToggle(1, setAccordionIndexMock);
    expect(setAccordionIndexMock).toHaveBeenCalledWith(0);
  });

  it('should set accordion index to 1 if the current index is 0', () => {
    const setAccordionIndexMock = jest.fn();
    handleAccordionToggle(0, setAccordionIndexMock);
    expect(setAccordionIndexMock).toHaveBeenCalledWith(1);
  });

  it('showPassportField should return true when values[field].value exists and is not "DE"', () => {
    const values = {
      nationality: { value: 'US' },
    };
    const field = 'nationality';
    const result = showPassportField(values, field);
    expect(result).toBe(true);
  });

  it('showPassportField should return false when values[field].value is "DE"', () => {
    const values = {
      nationality: { value: 'DE' },
    };
    const field = 'nationality';
    const result = showPassportField(values, field);
    expect(result).toBe(false);
  });

  it('handleNationalityChange should call field.onChange with the correct fieldProp', () => {
    const fieldProp = { value: 'DE' };
    const field = {
      onChange: jest.fn(),
    };
    const fieldToReset = 'fieldToReset';
    const handleSetValue = jest.fn();

    handleNationalityChange(fieldProp, field, fieldToReset, handleSetValue);

    expect(field.onChange).toHaveBeenCalledWith(fieldProp);
    expect(handleSetValue).toHaveBeenCalled();
  });

  it('handleNationalityChange should call handleSetValue with fieldToReset when value is german', () => {
    const fieldProp = { value: 'de' };
    const field = {
      onChange: jest.fn(),
    };
    const fieldToReset = 'fieldToReset';
    const handleSetValue = jest.fn();

    handleNationalityChange(fieldProp, field, fieldToReset, handleSetValue);

    expect(field.onChange).toHaveBeenCalledWith(fieldProp);
    expect(handleSetValue).toHaveBeenCalledWith('fieldToReset', '');
  });

  it('should call handleSetValue when value is non german', () => {
    const fieldProp = { value: 'uk' };
    const field = {
      onChange: jest.fn(),
    };
    const fieldToReset = 'fieldToReset';
    const handleSetValue = jest.fn();

    handleNationalityChange(fieldProp, field, fieldToReset, handleSetValue);

    expect(field.onChange).toHaveBeenCalledWith(fieldProp);
    expect(handleSetValue).toHaveBeenCalled();
  });
});

describe('scrollIntoViewOnError and addScrollEvent test cases', () => {
  it('should call scrollIntoView on the first error element', () => {
    document.body.innerHTML = `<div class="error">Error</div>`;
    const scrollIntoViewMock = jest.fn();
    Element.prototype.scrollIntoView = scrollIntoViewMock;
    scrollIntoViewOnError();
    jest.runAllTimers();
    expect(scrollIntoViewMock).toHaveBeenCalledWith({ behavior: 'smooth', block: 'center' });
  });
});

describe('addScrollEvent', () => {
  it('should add click event listener to review button', () => {
    document.body.innerHTML = `<div id="reg-form-submit-btn">Submit</div>`;
    const reviewBtn = document.getElementById('reg-form-submit-btn') as HTMLElement;
    const addEventListenerMock = jest.spyOn(reviewBtn, 'addEventListener');
    addScrollEvent();
    reviewBtn.click();

    expect(addEventListenerMock).toHaveBeenCalledWith('click', expect.any(Function));
  });
});

describe('getGuestDetailsObject', () => {
  it('should return the correct guest details object', () => {
    const key = GuestDetailKeys.firstName;
    const value = 'Whitbread';
    const t = jest.fn((key) => key);

    const result = getGuestDetailsObject(key, value, t);

    expect(result).toEqual({ key: 'precheckin.regcard.firstname', value: 'Whitbread' });
  });
});

describe('getPaymentParams', () => {
  it('should return undefined if bookingConfirmation is not provided', () => {
    const billingAddress = {
      street: '123 Main St',
      city: 'New York',
      country: 'US',
    };
    const language = 'en';
    const country = 'US';

    const result = getPaymentParams({
      billingAddress,
      bookingConfirmation: undefined,
      language,
      country,
    });

    expect(result).toBeUndefined();
  });
  it('should return a truthy value if bookingConfirmation is provided', () => {
    const billingAddress = {
      street: '123 Main St',
      city: 'New York',
      country: 'US',
    };
    const language = 'en';
    const country = 'US';
    const bookingConfirmation: any = {
      hotelId: '12345',
      reservationByIdList: [
        {
          roomStay: {
            adultsNumber: 2,
            arrivalDate: new Date('2022-01-01'),
            departureDate: new Date('2022-01-02'),
          },
          billing: {
            address: {
              addressType: 'HOME',
            },
          },
        },
      ],
      hotelName: 'Test Hotel',
    };

    const result = getPaymentParams({
      billingAddress,
      bookingConfirmation,
      language,
      country,
    });

    expect(result).toBeTruthy();
  });
});

describe('generateDependentsData', () => {
  it('should generate dependent guest data with correct labels', () => {
    const nationalities = [
      { value: 'us', label: 'United States', image: 'us.png' },
      { value: 'de', label: 'Germany', image: 'de.png' },
      { value: 'fr', label: 'France', image: 'fr.png' },
    ];
    const t = jest.fn((key) => key);
    const dependents = [
      {
        firstname: 'John',
        lastname: 'Doe',
        dateofbirth: new Date('1990-01-01'),
        nationality: { value: 'us', label: '' },
        passport: 'ABCD1234',
      },
      {
        firstname: 'Jane',
        lastname: 'Doe',
        dateofbirth: new Date('1992-05-10'),
        nationality: { value: 'de', label: '' },
        passport: 'EFGH5678',
      },
    ];

    const result = generateDependentsData(nationalities, t, dependents);

    expect(result).toEqual([
      {
        title: 'precheckin.guest 1',
        rows: [
          { key: 'precheckin.regcard.firstname', value: 'John' },
          { key: 'precheckin.regcard.lastname', value: 'Doe' },
          { key: 'precheckin.additionalfields.dateofbirth', value: '01 Jan 1990' },
          { key: 'precheckin.additionalfields.nationalities', value: 'United States' },
          { key: 'precheckin.details.passport', value: 'ABCD1234' },
        ],
      },
      {
        title: 'precheckin.guest 2',
        rows: [
          { key: 'precheckin.regcard.firstname', value: 'Jane' },
          { key: 'precheckin.regcard.lastname', value: 'Doe' },
          { key: 'precheckin.additionalfields.dateofbirth', value: '10 May 1992' },
          { key: 'precheckin.additionalfields.nationalities', value: 'Germany' },
          { key: 'precheckin.details.passport', value: 'EFGH5678' },
        ],
      },
    ]);
  });

  it('should handle missing nationality label', () => {
    const nationalities = [
      { value: 'us', label: 'United States', image: 'us.png' },
      { value: 'de', label: 'Germany', image: 'de.png' },
      { value: 'fr', label: 'France', image: 'fr.png' },
    ];
    const t = jest.fn((key) => key);
    const dependents = [
      {
        firstname: 'John',
        lastname: 'Doe',
        dateofbirth: new Date('1990-01-01'),
        nationality: { value: 'us', label: '' },
        passport: 'ABCD1234',
      },
      {
        firstname: 'Jane',
        lastname: 'Doe',
        dateofbirth: new Date('1992-05-10'),
        nationality: { value: 'it', label: '' },
        passport: 'EFGH5678',
      },
    ];

    const result = generateDependentsData(nationalities, t, dependents);

    expect(result).toEqual([
      {
        title: 'precheckin.guest 1',
        rows: [
          { key: 'precheckin.regcard.firstname', value: 'John' },
          { key: 'precheckin.regcard.lastname', value: 'Doe' },
          { key: 'precheckin.additionalfields.dateofbirth', value: '01 Jan 1990' },
          { key: 'precheckin.additionalfields.nationalities', value: 'United States' },
          { key: 'precheckin.details.passport', value: 'ABCD1234' },
        ],
      },
      {
        title: 'precheckin.guest 2',
        rows: [
          { key: 'precheckin.regcard.firstname', value: 'Jane' },
          { key: 'precheckin.regcard.lastname', value: 'Doe' },
          { key: 'precheckin.additionalfields.dateofbirth', value: '10 May 1992' },
          { key: 'precheckin.additionalfields.nationalities', value: '' },
          { key: 'precheckin.details.passport', value: 'EFGH5678' },
        ],
      },
    ]);
  });

  it('should handle missing dependents', () => {
    const nationalities = [
      { value: 'us', label: 'United States', image: 'us.png' },
      { value: 'de', label: 'Germany', image: 'de.png' },
      { value: 'fr', label: 'France', image: 'fr.png' },
    ];
    const t = jest.fn((key) => key);
    const dependents = undefined;

    const result = generateDependentsData(nationalities, t, dependents);

    expect(result).toEqual([]);
  });
});

describe('onIframeLoad', () => {
  it('should update analytics with the provided load time', () => {
    const time = '500ms';
    const updateMock = jest.spyOn(analytics, 'update');

    onIframeLoad(time);

    expect(updateMock).toHaveBeenCalledWith({ paymentLoadTime: time });
  });
});

describe('isCanvasEmpty', () => {
  it('should return true if the canvas is empty', () => {
    const canvas = document.createElement('canvas');
    canvas.width = 2;
    canvas.height = 2;

    const mockGetImageData = jest.fn(() => ({
      data: new Uint8ClampedArray([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]),
    }));

    HTMLCanvasElement.prototype.getContext = jest.fn(() => ({
      getImageData: mockGetImageData,
    })) as any;

    const result = isCanvasEmpty(canvas);

    expect(result).toBe(true);
  });

  it('should return false if the canvas is not empty', () => {
    const canvas = document.createElement('canvas');
    canvas.width = 2;
    canvas.height = 2;

    const mockGetImageData = jest.fn(() => ({
      data: new Uint8ClampedArray([0, 0, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]),
    }));

    HTMLCanvasElement.prototype.getContext = jest.fn(() => ({
      getImageData: mockGetImageData,
    })) as any;

    const result = isCanvasEmpty(canvas);

    expect(result).toBe(false);
  });
});

describe('handleIframeHeight', () => {
  const createMockIframe = () => {
    const mockIframe = document.createElement('iframe');
    mockIframe.id = 'paymentFrame';
    mockIframe.style.height = '';
    mockIframe.addEventListener = jest.fn().mockImplementation((event, callback) => {
      if (event === 'load' && typeof callback === 'function') {
        callback();
      }
    });
    return mockIframe;
  };
  let originalGetElementById: typeof document.getElementById;

  beforeEach(() => {
    originalGetElementById = document.getElementById;
  });

  afterEach(() => {
    document.getElementById = originalGetElementById;
    jest.clearAllMocks();
  });

  test('sets iframe height correctly when isSuccess is true', () => {
    const mockIframe = createMockIframe();
    document.getElementById = jest.fn().mockReturnValue(mockIframe);
    handleIframeHeight(true);
    expect(mockIframe.style.height).toBe(
      `${mockIframe.scrollHeight + mockIframe.scrollHeight / 2 - 60}px`
    );
  });

  test('does not set iframe height when isSuccess is false', () => {
    const mockIframe = createMockIframe();
    document.getElementById = jest.fn().mockReturnValue(mockIframe);
    handleIframeHeight(false);
    expect(mockIframe.style.height).toBe('');
  });

  test('does nothing if iframe is not found', () => {
    document.getElementById = jest.fn().mockReturnValue(null);
    handleIframeHeight(true);
    expect(document.getElementById).toHaveBeenCalledWith('paymentFrame');
  });
});

describe('handleRedirect', () => {
  it('should call push with the correct path', () => {
    const mockPush = jest.fn();
    const path = '/some-path';
    handleRedirect(path, mockPush);
    expect(mockPush).toHaveBeenCalledWith(path);
  });
});
