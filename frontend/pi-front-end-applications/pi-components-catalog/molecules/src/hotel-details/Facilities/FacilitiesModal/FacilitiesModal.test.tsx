import '@testing-library/jest-dom';

import { fireEvent, render, within } from '../../../utils/test-utils';
import FacilitiesModal from './FacilitiesModal.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockFacilities = [
  {
    code: 'DIS',
    description: 'Barrierefreie Zimmer',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
    isVisible: true,
    name: 'Barrierefreie Zimmer',
    weight: 0,
  },
  {
    code: 'LFT',
    description: 'Fahrstuhl',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LFT.svg',
    isVisible: false,
    name: 'Fahrstuhl',
    weight: 1,
  },
  {
    code: 'ACO',
    description: 'Klimatisierte Zimmer',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/ACO.svg',
    isVisible: false,
    name: 'Klimatisierte Zimmer',
    weight: 1,
  },
  {
    code: 'WIA',
    description: 'Kostenloses WLAN',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/WIA.svg',
    isVisible: true,
    name: 'Kostenloses WLAN',
    weight: 1,
  },
  {
    code: 'FAM',
    description: 'Familienzimmer',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/FAM.svg',
    isVisible: true,
    name: 'Familienzimmer',
    weight: 2,
  },
  {
    code: 'LUG',
    description: 'Gepäckaufbewahrung',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/LUG.svg',
    isVisible: false,
    name: 'Gepäckaufbewahrung',
    weight: 2,
  },
  {
    code: 'WET',
    description: 'Barrierefreie Nasszelle',
    icon: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/WET.svg',
    isVisible: true,
    name: 'Barrierefreie Nasszelle',
    weight: 5,
  },
];

export const mockHotelFacilities = mockFacilities;

export const mockRoomFacilities = {
  hotelInformation: {
    roomConfiguration: {
      tabItems: [
        {
          facilities: mockFacilities,
        },
      ],
    },
  },
};

const facilitiesModalProps = {
  facilities: mockHotelFacilities.slice(0, 4),
  isModalVisible: true,
  onModalClose: jest.fn(),
};

describe('FacilitiesModal', () => {
  it('should render FacilitiesModal', () => {
    const { getByTestId } = render(<FacilitiesModal {...facilitiesModalProps} />);
    expect(getByTestId('facilities-modal-ModalHeader')).toBeInTheDocument();
  });

  it('should display the list of facility icons', () => {
    const { getAllByTestId } = render(<FacilitiesModal {...facilitiesModalProps} />);
    // We are rendering 4 facilities, but there are 5 svg-containers, due to the X button in the modal header
    expect(getAllByTestId('svg-container').length).toEqual(5);
  });

  it('should display the facility title', () => {
    const { getAllByTestId } = render(<FacilitiesModal {...facilitiesModalProps} />);
    expect(getAllByTestId('facility-title').length).toEqual(4);
  });

  it('should display the facility description', () => {
    const { getAllByTestId } = render(<FacilitiesModal {...facilitiesModalProps} />);
    expect(getAllByTestId('facility-description').length).toEqual(4);
  });

  it('should not display the facility description if none provided', () => {
    const facilities = mockHotelFacilities.slice(0, 4).map((hotelFacility) => ({
      icon: hotelFacility.icon,
      name: hotelFacility.name,
      code: hotelFacility.code,
      weight: hotelFacility.weight,
      isVisible: hotelFacility.isVisible,
    }));
    const { queryAllByTestId } = render(
      <FacilitiesModal {...facilitiesModalProps} facilities={facilities} />
    );
    expect(queryAllByTestId('facility-description').length).toEqual(0);
  });

  it('should not display modal if we pass isModalVisible false', () => {
    const { queryByTestId } = render(
      <FacilitiesModal {...facilitiesModalProps} isModalVisible={false} />
    );
    expect(queryByTestId('facilities-modal')).toBeFalsy();
  });

  it('should call onModalClose when clicking the X button', () => {
    const { getByTestId, queryByTestId } = render(<FacilitiesModal {...facilitiesModalProps} />);
    fireEvent.click(getByTestId('facilities-modal-ModalCloseButton'));
    expect(queryByTestId('facilities-modal')).toBeFalsy();
  });

  it('should render modal content inside the provided container', () => {
    const containerElement = document.createElement('div');
    document.body.appendChild(containerElement);

    render(
      <FacilitiesModal
        {...facilitiesModalProps}
        containerRef={{
          current: containerElement,
        }}
      />
    );

    expect(
      within(containerElement).getByTestId('facilities-modal-ModalContent')
    ).toBeInTheDocument();

    containerElement.remove();
  });
});
