import '@testing-library/jest-dom';
import { fireEvent, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { Landing } from './landing';

let mockSearchParamType = '';

declare global {
  interface Window {
    // satellite required for adobe analytics
    __satelliteLoaded: boolean;
    _satellite: any;
  }
}

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => {
    return '/';
  },
  useSearchParams: () => {
    return {
      get: () => mockSearchParamType,
    };
  },
}));

const getLocaleByPathnameMock = jest.fn().mockReturnValue('en');

let mockResult: any = { applicationGUID: '12345', applicationId: '67890' };

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  formatIBAssetsUrl: jest.fn(() => '/'),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  getLocaleByPathname: jest.fn(() => getLocaleByPathnameMock()),
  initializePayApplication: jest.fn(() => Promise.resolve(mockResult)),
  updateResumeUrl: jest.fn(() => Promise.resolve(true)),
}));

const mockLandingProps: any = {
  userDetails: {
    contactDetail: {},
  },
};

const mockProps = {
  icons: {},
  header: null,
  initialState: {},
  initialStepId: PayApplicationStep.LANDING,
  steps: [
    {
      id: PayApplicationStep.LANDING,
      component: <Landing {...mockLandingProps} />,
    },
  ],
};

describe('Landing component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Landing component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should start the application when clicking on the button', () => {
    window.__satelliteLoaded = true;
    window._satellite = {
      track: jest.fn(),
    };
    const { getByTestId } = render(<Wizard {...mockProps} />);

    fireEvent.click(getByTestId('footer-button'));
    expect(getByTestId('wizard-page')).toBeInTheDocument();
    expect(window._satellite.track).toHaveBeenCalledWith('startApplication');
  });

  it('should render different text when on piba euro', () => {
    mockSearchParamType = 'euro';
    const { getByTestId } = render(<Wizard {...mockProps} />);
    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should render switch to de InnBusiness Pay without piba euro is not toggle', () => {
    getLocaleByPathnameMock.mockReturnValue({ locale: LOCALES.EN });

    const { getByTestId } = render(
      <Wizard
        icons={{}}
        header={null}
        initialState={{}}
        initialStepId={PayApplicationStep.LANDING}
        steps={[
          {
            id: PayApplicationStep.LANDING,
            component: (
              <Landing userDetails={{ contactDetail: {} } as any} isPibaEuroEnabled={false} />
            ),
          },
        ]}
      />
    );

    expect(getByTestId('landing-bottom-container')).toBeInTheDocument();
  });

  it('should render a toast if there is an error', () => {
    mockResult = null;
    const { getByTestId } = render(<Wizard {...mockProps} />);

    fireEvent.click(getByTestId('footer-button'));
  });
});
