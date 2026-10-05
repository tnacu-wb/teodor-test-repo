import '@testing-library/jest-dom';
import { FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN } from '@whitbread-eos/api';
import type { ImageItem } from '@whitbread-eos/api';
import { isIVMEnabled } from '@whitbread-eos/utils';

import { fireEvent, render } from '../../utils/test-utils';
import HotelGallery from './HotelGallery';

const mockUseFeatureToggle = jest.fn();
const mockUseStaticHotelInformation = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
  isIVMEnabled: jest.fn(),
  useFeatureToggle: () => mockUseFeatureToggle(),
}));

const mockGalleryImages = [
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
    alt: 'Hotel Image Thumbnail 1',
    caption: 'Accessible Bedroom',
    iconSrc: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 2',
    caption: 'This is another text',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 3',
    caption: '',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 4',
    caption: '',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 5',
    caption: '',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 6',
    caption: '',
  },
  {
    thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL 3.jpg',
    alt: 'Hotel Image Thumbnail 7',
    caption: '',
  },
];

describe('HotelGallery component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: mockGalleryImages,
    });
    mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN]: false });
  });

  it('should render HotelGallery', () => {
    const { getByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(getByTestId('thumbnails')).toBeInTheDocument();
  });

  it('should show a loading state', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: mockGalleryImages,
      isLoading: true,
    });
    const { getByText } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should show an error message if error prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: mockGalleryImages,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should open the modal when clicking the "See all photos" button', () => {
    (isIVMEnabled as jest.Mock).mockImplementation(() => true);
    const { getByRole, queryByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    fireEvent.click(getByRole('button'));
    expect(getByRole('dialog')).toBeInTheDocument();
    expect(queryByTestId(`ImageGalleryModal-Content`)).not.toBeInTheDocument();
    fireEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should open the modal when clicking a thumbnail', () => {
    mockUseFeatureToggle.mockReturnValueOnce({ FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN: true });
    const { getByRole, queryByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    const thumbnail = getByRole('img', { name: 'Hotel Image Thumbnail 1' });
    fireEvent.click(thumbnail);
    expect(getByRole('dialog')).toBeInTheDocument();
    expect(queryByTestId(`ImageGalleryModal-Content`)).not.toBeInTheDocument();
    fireEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should render 3 thumbnails if more than 3 images', () => {
    const { getAllByRole } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(getAllByRole('img').length).toEqual(3);
  });

  it('should render 3 placeholder thumbnails if there are no images', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: [] as ImageItem[],
    });
    const { getAllByTestId, queryAllByRole } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(queryAllByRole('img').length).toEqual(0);
    expect(getAllByTestId('emptyThumbnail').length).toEqual(3);
  });

  it('should render the correct amount of placeholder thumbnails if less than 3 images', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: [
        {
          thumbnailSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
          imageSrc: '/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/LONHOL-EXTERNAL-1.jpg',
          alt: 'Hotel Image Thumbnail 1',
          caption: 'Accessible Bedroom',
          iconSrc: '/etc.clientlibs/pi/clientlibs/icons/resources/facilities/codes/DIS.svg',
        },
      ],
    });
    const { getAllByRole, getAllByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(getAllByRole('img').length).toEqual(1);
    expect(getAllByTestId('emptyThumbnail').length).toEqual(2);
  });

  it('should show a hover state when hovering over a thumbnail', () => {
    const { getByRole } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    const image = getByRole('img', { name: 'Hotel Image Thumbnail 1' });

    fireEvent.mouseEnter(image);
    expect(image).toHaveClass('darken');
    fireEvent.mouseLeave(image);
    expect(image).not.toHaveClass('darken');
  });

  it('should render the mobile component', () => {
    const { getByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={true} />
    );

    expect(getByTestId('singleThumbnail')).toBeInTheDocument();
  });

  it('should render nothing if there are no images on mobile', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: [] as ImageItem[],
    });
    const { queryByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={true} />
    );

    expect(queryByTestId('singleThumbnail')).not.toBeInTheDocument();
  });

  it('should render nothing when no gallery images are given', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      galleryImages: [] as ImageItem[],
    });
    const { queryByTestId } = render(
      <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
    );
    expect(queryByTestId('singleThumbnail')).not.toBeInTheDocument();
  });

  describe('Flag overlay', () => {
    it('should not render flag overlay when hotelFlags.flagOverlay is not set', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
        hotelFlags: null,
      });
      const { queryByAltText } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      expect(queryByAltText('overlay')).not.toBeInTheDocument();
    });

    it('should not render flag overlay when isEnabled is false', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
        hotelFlags: {
          isEnabled: false,
          flagOverlay: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Hotel Flag Text',
            backgroundImage: null,
          },
        },
      });
      const { queryByText } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      expect(queryByText('Hotel Flag Text')).not.toBeInTheDocument();
    });

    it('should render flag overlay text when flagOverlay is set and isEnabled is true', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
        hotelFlags: {
          isEnabled: true,
          flagOverlay: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Hotel Flag Text',
            backgroundImage: null,
          },
        },
      });
      const { getByText } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      expect(getByText('Hotel Flag Text')).toBeInTheDocument();
    });

    it('should render overlay image when flagOverlay.backgroundImage is provided and isEnabled is true', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
        hotelFlags: {
          isEnabled: true,
          flagOverlay: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Hotel Flag Text',
            backgroundImage: '/images/flag-overlay.png',
          },
        },
      });
      const { getByAltText } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      expect(getByAltText('overlay')).toBeInTheDocument();
    });

    it('should not render overlay image when flagOverlay.backgroundImage is not provided', () => {
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
        hotelFlags: {
          isEnabled: true,
          flagOverlay: {
            backgroundColour: '#123456',
            textColour: '#ffffff',
            text: 'Hotel Flag Text',
            backgroundImage: null,
          },
        },
      });
      const { queryByAltText } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      expect(queryByAltText('overlay')).not.toBeInTheDocument();
    });
  });

  describe('Image gallery redesign enabled', () => {
    beforeEach(() => {
      jest.clearAllMocks();
      mockUseStaticHotelInformation.mockReturnValue({
        galleryImages: mockGalleryImages,
      });
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_BB_CCUI_HDP_IMAGE_GALLERY_REDESIGN]: true });
    });

    it('should open image gallery modal when clicking the "See all photos" button', () => {
      (isIVMEnabled as jest.Mock).mockImplementation(() => true);
      const { getByRole, getByTestId } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );

      fireEvent.click(getByRole('button'));

      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getByTestId(`ImageGalleryModal-Content`)).toBeInTheDocument();
    });

    it('should open image gallery modal when clicking a thumbnail', () => {
      const { getByRole, getByTestId } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      const thumbnail = getByRole('img', { name: 'Hotel Image Thumbnail 1' });

      fireEvent.click(thumbnail);

      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getByTestId(`ImageGalleryModal-Content`)).toBeInTheDocument();
    });

    it('should open image gallery modal when clicking a single thumbnail in smaller screens', () => {
      const { getByRole, getByTestId } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={true} />
      );

      const thumbnail = getByTestId('singleThumbnail');
      fireEvent.click(thumbnail);

      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getByTestId(`ImageGalleryModal-Content`)).toBeInTheDocument();
    });

    it('calls onModalClose when Back is clicked', () => {
      const { getByRole, getByTestId, getByText, queryByRole, queryByTestId } = render(
        <HotelGallery thumbnailSectionHeight="18.75rem" isLessThanLg={false} />
      );
      const thumbnail = getByRole('img', { name: 'Hotel Image Thumbnail 1' });

      fireEvent.click(thumbnail);
      expect(getByRole('dialog')).toBeInTheDocument();
      expect(getByTestId(`ImageGalleryModal-Content`)).toBeInTheDocument();

      const backButton = getByText('hdp.imageGallery.modal.back');
      fireEvent.click(backButton);

      expect(queryByRole('dialog')).toBeInTheDocument();
      expect(queryByTestId(`ImageGalleryModal-Content`)).toBeInTheDocument();
    });
  });
});
