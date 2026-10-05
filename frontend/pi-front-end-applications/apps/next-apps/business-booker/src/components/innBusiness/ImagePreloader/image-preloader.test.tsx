import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { ImagePreloader } from './image-preloader';

const mockMenuLabels = {
  bookings: {
    label: 'Bookings',
    icon: '/content/dam/global/icons/common/suitcase.svg',
    iconActive: '/content/dam/global/icons/common/suitcase-solid.svg',
  },
  spending: {
    label: 'Spending',
    icon: '/content/dam/global/icons/common/chart-bars.svg',
    iconActive: '/content/dam/global/icons/common/chart-bars-solid.svg',
  },
  home: {
    label: 'Home',
    icon: '/content/dam/global/icons/common/home.svg',
    iconActive: '/content/dam/global/icons/common/home-solid.svg',
  },
  manage: {
    label: 'Manage',
    icon: '/content/dam/global/icons/common/chart-pie.svg',
    iconActive: '/content/dam/global/icons/common/chart-pie-solid.svg',
    options: {},
  },
  contact: {
    label: 'Contact',
    icon: '/content/dam/global/icons/common/contact.svg',
    iconActive: '/content/dam/global/icons/common/contact-solid.svg',
  },
};

const mockArrowIcons = {
  collapse: '/content/dam/global/icons/common/chevron-left.svg',
  expand: '/content/dam/global/icons/common/chevron-right.svg',
};

const mockProps = {
  menuLabels: mockMenuLabels,
  collapseIcons: mockArrowIcons,
  languages: [
    {
      code: 'gb',
      language: 'English',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
      url: '/gb/en/inn-business/home.html',
    },
    {
      code: 'de',
      language: 'Deutsch',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
      url: '/de/de/inn-business/home.html',
    },
  ],
};

describe('ImagePreloader Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ImagePreloader component', () => {
    const { getByTestId } = render(<ImagePreloader {...mockProps} />);

    const imagePreloader = getByTestId('ImagePreloader');
    expect(imagePreloader).toBeInTheDocument();
  });
});
