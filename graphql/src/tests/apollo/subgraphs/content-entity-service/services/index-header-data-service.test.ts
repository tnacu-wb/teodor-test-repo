jest.mock('../../../../../apollo/client/rest-client');
jest.mock('../../../../../apollo/exception/error-handler', () => ({
  handleError: jest.fn()
}));

jest.mock('../../../../../apollo/utils/base-utils', () => ({
  addFieldsToMap: jest.fn((fields: any[], map: any) => {
    fields.forEach((f) => {
      if (f.required || typeof f.value !== 'undefined') map[f.key] = f.value;
    });
  }),
  isFieldRequested: jest.fn((fieldName: string, info: any) => {
    return info?.fieldName === fieldName;
  })
}));

jest.mock('../../../../../apollo/subgraphs/content-entity-service/services/base-service', () => ({
  endpoints: {
    HEADER_INFORMATION: {
      endpoint: '/v1/content/header/data',
      flowCode: 'DIGITAL_CON_004',
      axiosClient: {}
    },
    IN_BUSINESS_HEADER: {
      endpoint: '/v1/content/innb/header',
      flowCode: 'DIGITAL_CON_004',
      axiosClient: {}
    }
  }
}));

import { get } from '../../../../../apollo/client/rest-client';
import { handleError } from '../../../../../apollo/exception/error-handler';
import * as baseUtils from '../../../../../apollo/utils/base-utils';
import {
  getHeaderInformation,
  getInBusinessHeader
} from '../../../../../apollo/subgraphs/content-entity-service/services/index-header-data-service';

const mockedGet = get as jest.MockedFunction<any>;
const mockedHandleError = handleError as jest.MockedFunction<any>;

describe('index-header-data-service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('getHeaderInformation', () => {
    it('should fetch header information with required parameters', async () => {
      const mockHeaderData = {
        content: { some: 'content' },
        form: { some: 'form' },
        datePicker: { some: 'datePicker' },
        results: { some: 'results' },
        announcement: { some: 'announcement' },
        config: { some: 'config' },
        contactBanner: {
          type: 'info',
          text: 'Test contact banner',
          date: '31st March 2026',
          enabledPages: ['/gb/en/contact-us.html']
        }
      };

      mockedGet.mockResolvedValueOnce(mockHeaderData);

      const args = {
        language: 'en',
        country: 'gb',
        businessBooker: false
      };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getHeaderInformation(args, context, info);

      expect((baseUtils as any).addFieldsToMap).toHaveBeenCalled();
      expect(mockedGet).toHaveBeenCalled();
      expect(result).toEqual({
        content: mockHeaderData.content,
        form: mockHeaderData.form,
        datePicker: mockHeaderData.datePicker,
        results: mockHeaderData.results,
        announcement: mockHeaderData.announcement,
        config: mockHeaderData.config,
        contactBanner: mockHeaderData.contactBanner
      });
    });

    it('should include contactBanner in response', async () => {
      const mockHeaderData = {
        content: { some: 'content' },
        form: { some: 'form' },
        datePicker: { some: 'datePicker' },
        results: { some: 'results' },
        announcement: null,
        config: { some: 'config' },
        contactBanner: {
          type: 'warning',
          text: 'Service unavailable',
          date: '01st April 2026',
          enabledPages: ['/gb/en/contact-us.html', '/de/de/konkat.html']
        }
      };

      mockedGet.mockResolvedValueOnce(mockHeaderData);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getHeaderInformation(args, context, info);

      expect(result.contactBanner).toEqual({
        type: 'warning',
        text: 'Service unavailable',
        date: '01st April 2026',
        enabledPages: ['/gb/en/contact-us.html', '/de/de/konkat.html']
      });
    });

    it('should return null contactBanner when not provided', async () => {
      const mockHeaderData = {
        content: { some: 'content' },
        form: { some: 'form' },
        datePicker: { some: 'datePicker' },
        results: { some: 'results' },
        announcement: null,
        config: { some: 'config' }
      };

      mockedGet.mockResolvedValueOnce(mockHeaderData);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getHeaderInformation(args, context, info);

      expect(result.contactBanner).toBeNull();
    });

    it('should call handleError when get rejects', async () => {
      const error = new Error('API Error');
      mockedGet.mockRejectedValueOnce(error);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      await getHeaderInformation(args, context, info);

      expect(mockedHandleError).toHaveBeenCalledWith(error, args);
    });

    it('should fetch innBusinessHeader when requested', async () => {
      const mockHeaderData = {
        content: { some: 'content' },
        form: { some: 'form' },
        datePicker: { some: 'datePicker' },
        results: { some: 'results' },
        announcement: null,
        config: { some: 'config' },
        contactBanner: {
          type: 'info',
          text: 'Test contact banner',
          date: '31st March 2026',
          enabledPages: ['/gb/en/contact-us.html']
        }
      };

      const mockInnBusinessHeader = {
        header: 'business header data'
      };

      mockedGet.mockResolvedValueOnce(mockHeaderData);
      mockedGet.mockResolvedValueOnce(mockInnBusinessHeader);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = { fieldName: 'innBusinessHeader' };

      (baseUtils as any).isFieldRequested.mockImplementation(
        (fieldName: string) => fieldName === 'innBusinessHeader'
      );

      const result = await getHeaderInformation(args, context, info);

      expect(result.innBusinessHeader).toEqual(mockInnBusinessHeader);
      expect(result.contactBanner).toEqual(mockHeaderData.contactBanner);
    });
  });

  describe('getInBusinessHeader', () => {
    it('should fetch in-business header information', async () => {
      const mockInBusinessHeader = {
        header: 'business header data',
        contactBanner: {
          type: 'info',
          text: 'Business contact banner',
          date: '31st March 2026',
          enabledPages: ['/gb/en/business-contact.html']
        }
      };

      mockedGet.mockResolvedValueOnce(mockInBusinessHeader);

      const args = {
        language: 'en',
        country: 'gb'
      };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getInBusinessHeader(args, context, info);

      expect((baseUtils as any).addFieldsToMap).toHaveBeenCalled();
      expect(mockedGet).toHaveBeenCalled();
      expect(result).toEqual(mockInBusinessHeader);
    });

    it('should return contactBanner nested inside content for InnB header', async () => {
      const mockInBusinessHeader = {
        content: {
          header: { image: 'logo.svg', imageAlt: 'Inn Business' },
          global: { today: 'Today', tomorrow: 'Tomorrow' },
          countries: [{ code: 'gb', language: 'English' }],
          form: { where: 'Where to?' },
          authentication: { myProfile: 'My Profile', logoutButton: 'Log out' },
          contactBanner: {
            type: 'info',
            text: '31st March 2026 We can not currently process Visa card transactions.',
            date: '31st March 2026',
            enabledPages: ['/gb/en/contact-us.html', '/de/de/konkat.html']
          }
        },
        layout: { menu: {}, help: {} }
      };

      mockedGet.mockResolvedValueOnce(mockInBusinessHeader);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getInBusinessHeader(args, context, info);

      expect(result.content.contactBanner).toEqual({
        type: 'info',
        text: '31st March 2026 We can not currently process Visa card transactions.',
        date: '31st March 2026',
        enabledPages: ['/gb/en/contact-us.html', '/de/de/konkat.html']
      });
    });

    it('should return null contactBanner inside content when not provided by InnB API', async () => {
      const mockInBusinessHeader = {
        content: {
          header: { image: 'logo.svg', imageAlt: 'Inn Business' },
          global: { today: 'Today' },
          countries: [],
          form: { where: 'Where to?' },
          authentication: { myProfile: 'My Profile', logoutButton: 'Log out' },
          contactBanner: null
        },
        layout: { menu: {} }
      };

      mockedGet.mockResolvedValueOnce(mockInBusinessHeader);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      const result = await getInBusinessHeader(args, context, info);

      expect(result.content.contactBanner).toBeNull();
    });

    it('should call handleError when get rejects for in-business header', async () => {
      const error = new Error('API Error');
      mockedGet.mockRejectedValueOnce(error);

      const args = { language: 'en', country: 'gb' };
      const context = { traceId: 't-1' };
      const info = {};

      await getInBusinessHeader(args, context, info);

      expect(mockedHandleError).toHaveBeenCalledWith(error, args);
    });
  });
});
