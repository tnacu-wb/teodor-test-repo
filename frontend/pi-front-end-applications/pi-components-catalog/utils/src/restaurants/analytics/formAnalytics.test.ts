import '@testing-library/jest-dom';

import { updateAnalytics } from './formAnalytics';
import { FormFields } from './types';

// Mock the analytics service
jest.mock('../../services/analyticsService', () => ({
  analytics: {
    update: jest.fn(),
  },
}));

const mockAnalytics = jest.requireMock('../../services/analyticsService');

describe('formAnalytics', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('updateAnalytics', () => {
    const baseFormFields: FormFields = {
      adults: 2,
      adultsByEnquiry: '',
      children: 1,
      childrenByEnquiry: '',
      consent: false,
      date: '2024/01/15',
      emailAddress: 'test@example.com',
      firstname: 'John',
      highchair: 0,
      lastname: 'Doe',
      occasionId: 'occasion-123',
      privacyStatement: false,
      siteId: 'site-123',
      specialRequest: '',
      telephoneNumber: '07123456789',
      time: '18:00',
      wheelchair: false,
    };

    it('should call analytics.update with correct data for standard booking', () => {
      updateAnalytics(baseFormFields);

      expect(mockAnalytics.analytics.update).toHaveBeenCalledWith({
        restaurants: {
          adults: 2,
          children: 1,
          guests: 3,
          date: '15/01/2024',
          isUserDataEmail: true,
          isUserDataForeName: true,
          isUserDataSurName: true,
          highChairs: 0,
          largeGroupsEnquiry: false,
          additionalRequirements: false,
          wheelChairAccess: false,
          requestsComments: false,
          isUserDataConfirmTerms: false,
          isUserDataConfirmInput: false,
          allowMarketing: false,
        },
      });
    });

    it('should use adultsByEnquiry and childrenByEnquiry when provided', () => {
      const enquiryFormFields: FormFields = {
        ...baseFormFields,
        adultsByEnquiry: '4',
        childrenByEnquiry: '2',
      };

      updateAnalytics(enquiryFormFields);

      expect(mockAnalytics.analytics.update).toHaveBeenCalledWith({
        restaurants: {
          adults: 4,
          children: 2,
          guests: 6,
          date: '15/01/2024',
          isUserDataEmail: true,
          isUserDataForeName: true,
          isUserDataSurName: true,
          highChairs: 0,
          largeGroupsEnquiry: false,
          additionalRequirements: false,
          wheelChairAccess: false,
          requestsComments: false,
          isUserDataConfirmTerms: false,
          isUserDataConfirmInput: false,
          allowMarketing: false,
        },
      });
    });

    it('should calculate guests correctly as sum of adults and children', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        adults: 5,
        children: 3,
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.guests).toBe(8);
    });

    it('should set largeGroupsEnquiry to true when total guests > 16', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        adultsByEnquiry: '10',
        childrenByEnquiry: '8',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.largeGroupsEnquiry).toBe(true);
      expect(call.restaurants.guests).toBe(18);
    });

    it('should set largeGroupsEnquiry to false when total guests <= 16', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        adultsByEnquiry: '10',
        childrenByEnquiry: '6',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.largeGroupsEnquiry).toBe(false);
      expect(call.restaurants.guests).toBe(16);
    });

    it('should format date correctly as dd/MM/yyyy', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        date: '2024/12/25',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.date).toBe('25/12/2024');
    });

    it('should set isUserDataEmail to true when emailAddress is provided', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        emailAddress: 'user@example.com',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataEmail).toBe(true);
    });

    it('should set isUserDataEmail to false when emailAddress is empty', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        emailAddress: '',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataEmail).toBe(false);
    });

    it('should set isUserDataForeName to true when firstname is provided', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        firstname: 'Jane',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataForeName).toBe(true);
    });

    it('should set isUserDataSurName to true when lastname is provided', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        lastname: 'Smith',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataSurName).toBe(true);
    });

    it('should include highchair count', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        highchair: 2,
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.highChairs).toBe(2);
    });

    it('should set wheelChairAccess to true when wheelchair is true', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        wheelchair: true as any, // Type assertion for test
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.wheelChairAccess).toBe(true);
    });

    it('should set additionalRequirements to true when specialRequest is provided', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        specialRequest: 'Window seat please',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.additionalRequirements).toBe(true);
    });

    it('should set requestsComments to true when specialRequest is provided', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        specialRequest: 'Gluten-free options needed',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.requestsComments).toBe(true);
    });

    it('should set both additionalRequirements and requestsComments to false when specialRequest is empty', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        specialRequest: '',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.additionalRequirements).toBe(false);
      expect(call.restaurants.requestsComments).toBe(false);
    });

    it('should set isUserDataConfirmTerms to true when privacyStatement is true', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        privacyStatement: true as any, // Type assertion for test
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataConfirmTerms).toBe(true);
    });

    it('should set isUserDataConfirmInput to true when consent is true', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        consent: true as any, // Type assertion for test
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.isUserDataConfirmInput).toBe(true);
    });

    it('should set allowMarketing to true when consent is true', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        consent: true as any, // Type assertion for test
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.allowMarketing).toBe(true);
    });

    it('should handle complete form with all fields filled', () => {
      const completeFormFields: FormFields = {
        adults: 4,
        adultsByEnquiry: '',
        children: 2,
        childrenByEnquiry: '',
        consent: true as any,
        date: '2024/06/15',
        emailAddress: 'complete@example.com',
        firstname: 'Complete',
        highchair: 1,
        lastname: 'User',
        occasionId: 'occ-456',
        privacyStatement: true as any,
        siteId: 'site-456',
        specialRequest: 'Allergy to nuts',
        telephoneNumber: '07987654321',
        time: '19:30',
        wheelchair: true as any,
      };

      updateAnalytics(completeFormFields);

      expect(mockAnalytics.analytics.update).toHaveBeenCalledWith({
        restaurants: {
          adults: 4,
          children: 2,
          guests: 6,
          date: '15/06/2024',
          isUserDataEmail: true,
          isUserDataForeName: true,
          isUserDataSurName: true,
          highChairs: 1,
          largeGroupsEnquiry: false,
          additionalRequirements: true,
          wheelChairAccess: true,
          requestsComments: true,
          isUserDataConfirmTerms: true,
          isUserDataConfirmInput: true,
          allowMarketing: true,
        },
      });
    });

    it('should prioritize enquiry values over standard values', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        adults: 2,
        adultsByEnquiry: '10',
        children: 1,
        childrenByEnquiry: '5',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.adults).toBe(10);
      expect(call.restaurants.children).toBe(5);
      expect(call.restaurants.guests).toBe(15);
    });

    it('should handle zero values correctly', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        adults: 0,
        children: 0,
        highchair: 0,
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.adults).toBe(0);
      expect(call.restaurants.children).toBe(0);
      expect(call.restaurants.guests).toBe(0);
      expect(call.restaurants.highChairs).toBe(0);
    });

    it('should handle date at year boundary correctly', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        date: '2024/01/01',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.date).toBe('01/01/2024');
    });

    it('should handle date at year end correctly', () => {
      const formFields: FormFields = {
        ...baseFormFields,
        date: '2024/12/31',
      };

      updateAnalytics(formFields);

      const call = mockAnalytics.analytics.update.mock.calls[0][0];
      expect(call.restaurants.date).toBe('31/12/2024');
    });
  });
});
