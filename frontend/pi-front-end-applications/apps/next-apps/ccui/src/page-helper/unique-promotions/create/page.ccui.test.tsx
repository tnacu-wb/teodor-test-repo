import '@testing-library/jest-dom';
import React from 'react';

import { render, screen, fireEvent, waitFor } from '../../../utils/test-utils';
import { onSubmitCreatePromotionForm } from './common';
import { createPromotionFormConfig } from './formConfig/createPromotionFormConfig';
import CreatePromotionCodeCcui from './page.ccui';

const mockReplace = jest.fn();
jest.mock('next/router', () => {
  const actual = jest.requireActual('next/router');
  return { ...actual, useRouter: () => ({ replace: mockReplace }) };
});

const toastMock = jest.fn();

const setItemSpy = jest.spyOn(Storage.prototype, 'setItem');

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');
  return {
    ...actual,
    useToast: () => toastMock,
  };
});

jest.mock('next/dynamic', () => {
  return (loader: any) => {
    if (typeof loader === 'function') {
      loader();
    }

    const MockForm = ({ onSubmit, ...props }: any) => (
      <form data-testid="mock-form" onSubmit={onSubmit} {...props} />
    );

    MockForm.displayName = 'MockForm';
    return MockForm;
  };
});

jest.mock('./common', () => {
  const actual = jest.requireActual('./common');
  return {
    ...actual,
    getPromoErrorMessage: jest.fn(() => '1011'),
    onSubmitCreatePromotionForm: jest.fn(),
    cancelReturnStyles: { 'data-testid': 'cancel-btn' },
    regFormInit: {
      campaignName: '',
      operaPromoCode: '',
      batchCount: 0,
      prefix: '',
      expiryDate: '',
      requestedBy: '',
    },
  };
});

const mockMutate = jest.fn();
let mockUseMutationRequest: any = jest.fn(() => ({
  mutation: { mutate: mockMutate },
  data: null,
  isError: false,
  error: null,
  isSuccess: false,
}));

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    usePromoTranslation: () => ({ cancelAndReturnText: 'Cancel and Return' }),
    useCustomLocale: () => ({ country: 'uk', language: 'en' }),
    useMutationRequest: () => mockUseMutationRequest(),
  };
});

jest.mock('./formConfig/createPromotionFormConfig', () => {
  const actual = jest.requireActual('./formConfig/createPromotionFormConfig');
  return { ...actual, createPromotionFormConfig: jest.fn((props: any) => ({ ...props })) };
});

jest.mock('./common', () => {
  const actual = jest.requireActual('./common');
  return {
    ...actual,
    onSubmitCreatePromotionForm: jest.fn(),
    cancelReturnStyles: { 'data-testid': 'cancel-btn' },
    regFormInit: {
      campaignName: '',
      operaPromoCode: '',
      batchCount: 0,
      prefix: '',
      expiryDate: '',
      requestedBy: '',
    },
  };
});

describe('CreatePromotionCodeCcui', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    setItemSpy.mockClear();
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: mockMutate },
      data: null,
      isError: false,
      error: null,
      isSuccess: false,
    }));
  });

  it('renders the page and form correctly', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);
    expect(screen.getByText('Cancel and Return')).toBeInTheDocument();
    expect(screen.getByTestId('mock-form')).toBeInTheDocument();
  });

  it('calls router.replace when cancel is clicked', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);
    fireEvent.click(screen.getByText('Cancel and Return'));
    expect(mockReplace).toHaveBeenCalledWith('/uk/en/unique-promotions/list');
  });

  it('calls onSubmitCreatePromotionForm when form is submitted', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);
    fireEvent.submit(screen.getByTestId('mock-form'));
    expect(onSubmitCreatePromotionForm).toHaveBeenCalled();
  });

  it('redirects after successful mutation with batch', async () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: false,
    }));

    const { rerender } = render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: { createPromoBatch: { status: 'COMPLETED' } },
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: true,
    }));

    rerender(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    await waitFor(() => {
      expect(mockReplace).toHaveBeenCalledWith('/uk/en/unique-promotions/list');
    });
  });

  it('does NOT redirect if mutation is successful but batch is missing', async () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: {},
      isError: false,
      error: null,
      isSuccess: true,
    }));
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);
    await new Promise((r) => setTimeout(r, 0));
    expect(mockReplace).not.toHaveBeenCalled();
  });

  it('renders form even if mutation fails', () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isError: true,
      error: new Error('API error'),
      isSuccess: false,
    }));
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);
    expect(screen.getByTestId('mock-form')).toBeInTheDocument();
  });

  it('initializes requestedBy from user email', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'user@example.com' }} />);
    expect(createPromotionFormConfig).toHaveBeenCalledWith(
      expect.objectContaining({
        defaultValues: expect.objectContaining({ requestedBy: 'user@example.com' }),
      })
    );
  });

  it('updates formDetails when getFormState is called', async () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    const configArgs = (createPromotionFormConfig as jest.Mock).mock.calls[0][0];

    const mockState = {
      campaignName: 'Test Campaign',
      operaPromoCode: 'PROMO123',
      batchCount: 10,
      prefix: 'PRE',
      expiryDate: '2026-01-01',
      requestedBy: 'test@example.com',
    };

    configArgs.getFormState(mockState);

    await waitFor(() => {
      expect(createPromotionFormConfig).toHaveBeenLastCalledWith(
        expect.objectContaining({
          defaultValues: expect.objectContaining({
            campaignName: 'Test Campaign',
          }),
        })
      );
    });
  });

  it('uses translated value when translation key exists', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    const configCall = (createPromotionFormConfig as jest.Mock).mock.calls[0][0];
    const translateFn = configCall.t;

    expect(translateFn('cancelAndReturnText')).toBe('Cancel and Return');
  });

  it('falls back to id when translation key does not exist', () => {
    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    const configCall = (createPromotionFormConfig as jest.Mock).mock.calls[0][0];
    const translateFn = configCall.t;

    expect(translateFn('someUnknownKey')).toBe('someUnknownKey');
  });

  it('does not show toast on first render after loading completes', async () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: false,
    }));

    render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    await waitFor(() => {
      expect(toastMock).not.toHaveBeenCalled();
    });
  });

  it('stores session flag and redirects after successful mutation', async () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: false,
    }));

    const { rerender } = render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: {
        createPromoBatch: { status: 'COMPLETED' },
      },
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: true,
    }));

    rerender(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    await waitFor(() => {
      expect(setItemSpy).toHaveBeenCalledWith('promoBatchCreated', 'true');
      expect(mockReplace).toHaveBeenCalledWith('/uk/en/unique-promotions/list');
    });
  });

  it('shows error toast for non-ignored error code', async () => {
    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isLoading: false,
      isError: false,
      error: null,
      isSuccess: false,
    }));

    const { rerender } = render(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    mockUseMutationRequest = jest.fn(() => ({
      mutation: { mutate: jest.fn() },
      data: null,
      isLoading: false,
      isError: true,
      error: new Error('API error'),
      isSuccess: false,
    }));

    rerender(<CreatePromotionCodeCcui user={{ email: 'test@example.com' }} />);

    await waitFor(() => {
      expect(toastMock).toHaveBeenCalledWith(
        expect.objectContaining({
          status: 'warning',
        })
      );
    });
  });
});
