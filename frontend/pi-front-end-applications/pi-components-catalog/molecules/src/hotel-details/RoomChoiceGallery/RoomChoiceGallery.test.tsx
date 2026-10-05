import '@testing-library/jest-dom';
import { act } from '@testing-library/react';
import getConfig from 'next/config';

import { userEvent, render } from '../../utils/test-utils';
import RoomChoiceGalleryComponent from './RoomChoiceGallery';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

describe('RoomChoiceGallery component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render RoomChoiceGallery', async () => {
    const { getByTestId } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={false} />
    );
    expect(getByTestId('hdp-accessible-thumbnails')).toBeInTheDocument();
  });

  it('should open the modal when clicking the "See all photos" button', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={false} />
    );
    act(() => {
      userEvent.click(getByRole('button'));
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should open the modal when clicking a thumbnail', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={false} />
    );
    const thumbnail = getByRole('img', { name: 'hoteldetails.accessibleLoweredBathroom' });
    act(() => {
      userEvent.click(thumbnail);
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should show a hover state when hovering over a thumbnail', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={false} />
    );
    const image = getByRole('img', { name: 'hoteldetails.accessibleLoweredBathroom' });

    await act(async () => {
      await userEvent.hover(image);
    });
    expect(image).toHaveClass('darken');
    await act(async () => {
      await userEvent.unhover(image);
    });
    expect(image).not.toHaveClass('darken');
  });

  it('should render the single thumbnail', async () => {
    const { getByTestId } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={true} />
    );
    expect(getByTestId('hdp-accessible-singleThumbnails')).toBeInTheDocument();
  });

  it('should open the modal when clicking the single thumbnail on mobile', async () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'false',
      },
    }));
    const { getByTestId, getByRole } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={false} isLessThanLg={true} />
    );

    expect(getByTestId('hdp-accessible-singleThumbnails')).toBeInTheDocument();
    act(() => {
      userEvent.click(getByTestId('hdp-accessible-singleThumbnails'));
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should render the single thumbnail', async () => {
    const { getByTestId } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={true} isLessThanLg={true} />
    );
    expect(getByTestId('hdp-accessible-singleThumbnails')).toBeInTheDocument();
  });

  it('should open the modal when clicking the single thumbnail on mobile', async () => {
    const { getByTestId, getByRole } = render(
      <RoomChoiceGalleryComponent roomType="accessible" isLessThanMd={true} isLessThanLg={true} />
    );

    expect(getByTestId('hdp-accessible-singleThumbnails')).toBeInTheDocument();
    act(() => {
      userEvent.click(getByTestId('hdp-accessible-singleThumbnails'));
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should open the modal when clicking the "See all photos" button', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="twinRoom" isLessThanMd={false} isLessThanLg={false} />
    );
    act(() => {
      userEvent.click(getByRole('button'));
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should open the modal when clicking a thumbnail', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="twinRoom" isLessThanMd={false} isLessThanLg={false} />
    );
    const thumbnail = getByRole('img', { name: 'twinroom.improvedTwin.title' });
    act(() => {
      userEvent.click(thumbnail);
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });

  it('should show a hover state when hovering over a thumbnail', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent roomType="twinRoom" isLessThanMd={false} isLessThanLg={false} />
    );
    const image = getByRole('img', { name: 'twinroom.improvedTwin.title' });

    await act(async () => {
      await userEvent.hover(image);
    });
    expect(image).toHaveClass('darken');
    await act(async () => {
      await userEvent.unhover(image);
    });
    expect(image).not.toHaveClass('darken');
  });

  it('should render the single thumbnail for premier plus acccessible room ', async () => {
    const { getByTestId } = render(
      <RoomChoiceGalleryComponent
        roomType="premierPlusAccessible"
        isLessThanMd={true}
        isLessThanLg={true}
      />
    );
    expect(getByTestId('hdp-premierPlusAccessible-singleThumbnails')).toBeInTheDocument();
  });

  it('should open the modal when clicking a thumbnail for premier plus accessible room', async () => {
    const { getByRole } = render(
      <RoomChoiceGalleryComponent
        roomType="premierPlusAccessible"
        isLessThanMd={false}
        isLessThanLg={false}
      />
    );
    const thumbnail = getByRole('img', { name: 'hoteldetails.accessibleLoweredBathroom' });
    act(() => {
      userEvent.click(thumbnail);
    });
    expect(getByRole('dialog')).toBeInTheDocument();
    userEvent.click(getByRole('button', { name: 'Close' }));
  });
});
