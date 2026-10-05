import '@testing-library/jest-dom/extend-expect';
import { render, fireEvent, waitFor } from '@testing-library/react';

import { AddCorrespondenceButton } from './add-correspondence-button';

const mockProps = {
  icons: {},
  onClick: jest.fn(),
  buttonStyle: 'button-style',
  buttonIconStyle: 'button-icon-style',
  baseDataTestId: 'CorrespondenceButton',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

describe('AddCorrespondenceButton', () => {
  it('renders the button with the correct text and icon', async () => {
    const { getByTestId, getByText } = render(<AddCorrespondenceButton {...mockProps} />);

    const button = getByTestId(`${mockProps.baseDataTestId}-button-add-correspondence`);
    const icon = getByTestId(`${mockProps.baseDataTestId}-button-add-correspondence-icon`);
    const address = getByText(`companyDetails.correspondence.address`);

    await waitFor(async () => {
      expect(button).toBeInTheDocument();
      expect(icon).toBeInTheDocument();
      expect(address).toBeInTheDocument();
    });
  });

  it('calls onClick when the button is clicked', async () => {
    const { onClick } = mockProps;
    const { getByTestId } = render(<AddCorrespondenceButton {...mockProps} />);
    const button = getByTestId(`${mockProps.baseDataTestId}-button-add-correspondence`);

    await waitFor(async () => {
      expect(button).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(button);
    });
    expect(onClick).toHaveBeenCalledTimes(1);
  });
});
