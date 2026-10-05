import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { act } from 'react-dom/test-utils';

import {
  AddBulkEmployees,
  groupErrorsByCode,
  getBulkUploadFailureMessages,
} from './add-bulk-employees';

const mockProps = {
  icons: {},
  token: '',
  companyId: '',
};

const bulkUploadEmployeesMock = jest.fn();

const tMock = (key: string) => {
  const translations: Record<string, string> = {
    'userMgmt.employee.bulkUpload.failure':
      "The file couldn't be processed. Please try again or choose a different file.",
    'userMgmt.employee.bulkUpload.uploadFile.partiallyProcessed':
      'The file was partially processed. Please make the required changes and try again.',
    'userMgmt.employee.alreadyExists': 'Cannot create employee as it already exists for row:',
    'userMgmt.employee.emailAddress.empty': 'Email address field is empty for row:',
    'userMgmt.employee.emailAddress.invalid': 'Email address field is invalid for row:',
    'userMgmt.employee.title.empty': 'Title field is empty for row:',
    'userMgmt.employee.firstName.empty': 'First name field is empty for row:',
    'userMgmt.employee.lastName.empty': 'Last name field is empty for row:',
    'userMgmt.employee.textConfirmation.empty': 'Text confirmation field is empty for row:',
    'userMgmt.employee.accessLevel.empty': 'Access level is empty for row:',
  };
  return translations[key] || key;
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
  getLocaleByPathname: () => {
    return LOCALES.EN;
  },
  getPathForLocale: () => {
    return '/';
  },
  analytics: {
    update: jest.fn(),
    remove: jest.fn(),
  },
  renderSanitizedHtml: jest.fn((html: string) => html),
  formatIBAssetsUrl: () => {
    return '/';
  },
  useTranslation: () => {
    return {
      t: tMock,
    };
  },
  getCountriesList: () => {
    return;
  },
  getCountryName: () => {
    return 'United Kingdom (the)';
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  bulkUploadEmployees: (...args: unknown[]) => bulkUploadEmployeesMock(...args),
}));

describe('AddBulkEmployees Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render AddBulkEmployees component', async () => {
    const { getByTestId } = render(<AddBulkEmployees {...mockProps} />);

    expect(getByTestId('Add-Employees-Bulk-page')).toBeInTheDocument();
  });

  it('should render AddBulkEmployees component and add file', async () => {
    const { getByTestId } = render(<AddBulkEmployees {...mockProps} />);

    const fileInput = getByTestId('FileInput');
    expect(fileInput).toBeInTheDocument();

    const uploadButton = getByTestId('Bulk-Upload-File');
    expect(uploadButton).toBeInTheDocument();

    await act(async () => {
      const files = [new File([], '')];

      fireEvent.change(fileInput, { target: { files } });
      fireEvent.click(uploadButton);
    });

    await waitFor(() => {
      expect(getByTestId('Bulk-Upload-Submit')).toBeInTheDocument();
    });
  });

  it('shows error messages when upload fails', async () => {
    const createMockResponse = (body: unknown, ok = false, status = 400) => ({
      ok,
      status,
      clone: () => ({
        json: jest.fn().mockResolvedValue(body),
        text: jest.fn().mockResolvedValue(JSON.stringify(body)),
      }),
      json: jest.fn().mockResolvedValue(body),
      text: jest.fn().mockResolvedValue(JSON.stringify(body)),
    });

    bulkUploadEmployeesMock.mockResolvedValue(
      createMockResponse([
        { errorCode: '282', errorDescription: '2' },
        { errorCode: '282', errorDescription: '3' },
        { errorCode: '283', errorDescription: '4' },
        { errorCode: '285', errorDescription: '5' },
      ])
    );

    const { getByTestId, getByText } = render(<AddBulkEmployees {...mockProps} />);

    const fileInput = getByTestId('FileInput');
    act(() => {
      fireEvent.change(fileInput, { target: { files: [new File([''], 'bulk.xlsx')] } });
    });

    const submitButton = getByTestId('Bulk-Upload-Submit');
    await act(async () => {
      fireEvent.click(submitButton);
    });

    expect(bulkUploadEmployeesMock).toHaveBeenCalled();
    expect(
      getByText("The file couldn't be processed. Please try again or choose a different file.")
    ).toBeInTheDocument();
    expect(
      getByText('Cannot create employee as it already exists for row: 2, 3')
    ).toBeInTheDocument();
    expect(getByText('Email address field is empty for row: 4')).toBeInTheDocument();
    expect(getByText('Title field is empty for row: 5')).toBeInTheDocument();
  });

  it('shows partially processed error message when upload fails but the file was partially processed', async () => {
    const createMockResponse = (body: unknown, ok = false, status = 400) => ({
      ok,
      status,
      clone: () => ({
        json: jest.fn().mockResolvedValue(body),
        text: jest.fn().mockResolvedValue(JSON.stringify(body)),
      }),
      json: jest.fn().mockResolvedValue(body),
      text: jest.fn().mockResolvedValue(JSON.stringify(body)),
    });

    bulkUploadEmployeesMock.mockResolvedValue(
      createMockResponse([
        { errorCode: '282', errorDescription: '2' },
        { errorCode: '282', errorDescription: '3' },
        { errorCode: '283', errorDescription: '4' },
        { errorCode: '283', errorDescription: '5' },
        { errorCode: '293', errorDescription: '' },
      ])
    );

    const { getByTestId, getByText, queryByText } = render(<AddBulkEmployees {...mockProps} />);

    const fileInput = getByTestId('FileInput');
    act(() => {
      fireEvent.change(fileInput, { target: { files: [new File([''], 'bulk.xlsx')] } });
    });

    const submitButton = getByTestId('Bulk-Upload-Submit');
    expect(submitButton).not.toBeDisabled();
    await act(async () => {
      fireEvent.click(submitButton);
    });
    await waitFor(() => {
      expect(submitButton).toBeDisabled();
    });
    expect(bulkUploadEmployeesMock).toHaveBeenCalled();
    expect(
      getByText('The file was partially processed. Please make the required changes and try again.')
    ).toBeInTheDocument();
    expect(
      getByText('Cannot create employee as it already exists for row: 2, 3')
    ).toBeInTheDocument();
    expect(getByText('Email address field is empty for row: 4, 5')).toBeInTheDocument();

    expect((fileInput as HTMLInputElement).value).toBe('');

    const sameFile = new File([''], 'bulk.xlsx');
    await act(async () => {
      fireEvent.change(fileInput, { target: { files: [sameFile] } });
    });

    await waitFor(() => {
      expect(submitButton).not.toBeDisabled();
    });
    expect(
      queryByText(
        'The file was partially processed. Please make the required changes and try again.'
      )
    ).not.toBeInTheDocument();
  });
});

describe('groupErrorsByCode', () => {
  it('should group errors by their error codes', () => {
    const errors = [
      { errorCode: '282', errorDescription: '4' },
      { errorCode: '283', errorDescription: '6' },
      { errorCode: '282', errorDescription: '5' },
      { errorCode: '284', errorDescription: '7' },
    ];

    const grouped = groupErrorsByCode(errors);

    expect(grouped).toEqual({
      '282': ['4', '5'],
      '283': ['6'],
      '284': ['7'],
    });
  });
});

describe('getBulkUploadFailureMessages', () => {
  it('should return failure messages based on grouped errors', () => {
    const groupedErrors = {
      '282': ['2', '3'],
      '283': ['4'],
      '284': ['5'],
      '286': ['7'],
    };

    const messages = getBulkUploadFailureMessages(groupedErrors, tMock);

    expect(messages).toEqual([
      'Cannot create employee as it already exists for row: 2, 3',
      'Email address field is empty for row: 4',
      'Email address field is invalid for row: 5',
      'First name field is empty for row: 7',
    ]);
  });
});
