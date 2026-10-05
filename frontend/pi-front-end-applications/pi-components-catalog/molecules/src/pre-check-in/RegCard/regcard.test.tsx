import { PDFDocument } from 'pdf-lib';

import { Nationality } from '../types';
import { generateRegCard } from './';

jest.mock('pdf-lib', () => {
  const mockPDFDocument = {
    embedFont: jest.fn().mockResolvedValue({
      font: jest.fn(),
      widthOfTextAtSize: jest.fn().mockReturnValue(10),
      heightAtSize: jest.fn().mockReturnValue(10),
    }),
    addPage: jest.fn().mockReturnValue({
      getSize: jest.fn().mockReturnValue({ width: 100, height: 100 }),
      drawText: jest.fn(),
      drawLine: jest.fn().mockReturnValue({}),
      drawImage: jest.fn().mockReturnValue({
        width: 100,
        height: 100,
      }),
    }),
    save: jest.fn().mockResolvedValue(new Uint8Array(10)),
    embedPng: jest.fn().mockResolvedValue({
      scale: jest.fn().mockReturnValue({
        width: 100,
        height: 100,
      }),
    }),
  };

  return {
    PDFDocument: {
      create: jest.fn().mockResolvedValue(mockPDFDocument),
    },
    StandardFonts: {
      Helvetica: 'Helvetica',
      HelveticaBold: 'HelveticaBold',
    },
    rgb: jest.fn(),
  };
});

const nationalities: Nationality[] = [
  {
    value: 'GB',
    label: 'United Kingdom (the)',
    image: '/content/dam/global/flags/United-Kingdom.png',
    countryName: 'United Kingdom (the)',
  },
  {
    value: 'DE',
    label: 'Germany',
    image: '/content/dam/global/flags/Germany.png',
    countryName: 'Germany',
  },
];

describe('generateRegCard', () => {
  it('should generate a registration card PDF', async () => {
    const t = (id: string) => id;
    const transactionid = '1234567890';
    const data = {
      bookingReference: '123456',
      hotelName: 'WHITBREAD',
      arrivalDate: new Date('2022-01-01'),
      departureDate: new Date('2022-01-05'),
      firstName: 'John',
      lastName: 'Smith',
      address: '123 Main St',
      postalCode: '12345',
      city: 'Holborn',
      dateOfBirth: new Date('1990-01-01'),
      nationality: { label: 'German', value: 'DE' },
      passport: 'ABCD1234',
      country: 'Germany',
      noOfRooms: 1,
      roomNo: 0,
      dependents: [],
    };
    const signatureUrl =
      'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABjElEQVR42mNkYGD4z0A8';

    const hotelAddress = 'Holborn';

    const result = await generateRegCard({
      t,
      transactionid,
      data,
      signatureUrl,
      hotelAddress,
      nationalities,
    });

    expect(result).toBeDefined();
    expect(result).not.toBeNull();
  });

  it('should add a new page when index exceeds 70', async () => {
    const t = (id: string) => id;
    const transactionid = '1234567890';
    const data = {
      bookingReference: '123456',
      hotelName: 'WHITBREAD',
      arrivalDate: new Date('2022-01-01'),
      departureDate: new Date('2022-01-05'),
      firstName: 'John',
      lastName: 'Smith',
      address: '123 Main St',
      postalCode: '12345',
      city: 'Holborn',
      dateOfBirth: new Date('1990-01-01'),
      nationality: { label: 'German', value: 'DE' },
      passport: 'ABCD1234',
      country: 'Germany',
      noOfRooms: 1,
      roomNo: 0,
      dependents: Array(100).fill({
        firstname: 'John',
        lastname: 'Doe',
        dateofbirth: new Date('2000-01-01'),
        passport: 'XYZ1234',
        nationality: { label: 'American', value: 'US' },
      }),
    };
    const signatureUrl =
      'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABjElEQVR42mNkYGD4z0A8';

    const hotelAddress = 'Holborn';
    const result = await generateRegCard({
      t,
      transactionid,
      data,
      signatureUrl,
      hotelAddress,
      nationalities,
    });

    expect(result).toBeDefined();
    expect(result).not.toBeNull();

    const mockPDFDocument = await PDFDocument.create();
    expect(mockPDFDocument.addPage).toHaveBeenCalledTimes(22);
  });

  it('should add a new page when index exceeds 70', async () => {
    const t = (id: string) => id;
    const transactionid = '1234567890';
    const data = {};
    const signatureUrl =
      'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABjElEQVR42mNkYGD4z0A8';

    const hotelAddress = 'Holborn';
    const result = await generateRegCard({
      t,
      transactionid,
      data,
      signatureUrl,
      hotelAddress,
      nationalities,
    });

    expect(result).toBeDefined();
    expect(result).not.toBeNull();

    const mockPDFDocument = await PDFDocument.create();
    expect(mockPDFDocument.addPage).toHaveBeenCalledTimes(23);
  });
});
