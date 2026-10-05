import { get, post } from '../../../../../apollo/client/rest-client';
import {
  deleteApplication,
  deleteApplicationCard,
  getApplicationCards,
  getApplicationDetails,
  getAppLookupData,
  getCompanyDetailsLookup,
  getPayApplications,
  initializeApplication,
  shareApplication,
  removeParticipant,
  submitApplication,
  appPreCheck,
  directDebit,
  updateResumeUrl,
  getDdSepaFormStatus
} from '../../../../../apollo/subgraphs/pay-app-entity-service/services/pay-app-service';
import { endpoints } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const headers = { 'Content-Type': 'application/json' };
const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
const context = { headers, res };

describe('getPayApplications', () => {
  it('should call the get function with correct parameters when getPayApplications is called', async () => {
    await getPayApplications({}, context);
    expect(get).toHaveBeenCalledWith(endpoints.PAY_APP, getPayApplications, {}, context);
  });

  it('should handle errors gracefully when getPayApplications throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(getPayApplications({}, context)).rejects.toThrow('Test error');
  });
});
describe('initializeApplication', () => {
  const initializeApplicationRequest = {
    email: 'test@email.com',
    ipAddress: '0.0.0.0',
    scheme: 'GB'
  };

  it('should call the post function with correct parameters when initializeApplication is called', async () => {
    await initializeApplication({ initializeApplicationRequest }, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.INITIALIZE_APPLICATION,
      initializeApplication,
      {
        email: 'test@email.com',
        ipAddress: '0.0.0.0',
        scheme: 'GB'
      },
      context
    );
  });

  it('should handle errors gracefully when initializeApplication throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(initializeApplication({ initializeApplicationRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('deleteApplication', () => {
  const deleteApplicationRequest = {
    applicationId: 'ID_123',
    applicationGuid: 'GUID_123',
    scheme: 'GB'
  };

  it('should call the post function with correct parameters when deleteApplication is called', async () => {
    await deleteApplication({ deleteApplicationRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.DELETE_APPLICATION,
      deleteApplication,
      {
        applicationId: 'ID_123',
        applicationGuid: 'GUID_123',
        scheme: 'GB'
      },
      context
    );
  });

  it('should handle errors gracefully when deleteApplication throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(deleteApplication({ deleteApplicationRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getAppLookupData', () => {
  const finalMap = { lookupNames: '', scheme: 'DE' };
  it('should call the get function with correct parameters when getAppLookupData is called', async () => {
    await getAppLookupData({ scheme: 'DE' }, context, {
      fieldNodes: [{ selectionSet: { selections: [{ name: { value: 'layoutEndpoint' } }] } }]
    });
    expect(get).toHaveBeenCalledWith(
      endpoints.APP_LOOKUP_DATA,
      getAppLookupData,
      finalMap,
      context
    );
  });

  it('should handle errors gracefully when getAppLookupData throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getAppLookupData({ scheme: 'DE' }, context, {
        fieldNodes: [{ selectionSet: { selections: [{ name: { value: 'layoutEndpoint' } }] } }]
      })
    ).rejects.toThrow('Test error');
  });
});

describe('getCompanyDetailsLookup', () => {
  const finalMap = {
    companyRegistrationNumber: 'TEST_123',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when getCompanyDetailsLookup is called', async () => {
    await getCompanyDetailsLookup({ companyRegistrationNumber: 'TEST_123', scheme: 'GB' }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.COMPANY_DETAILS_LOOKUP,
      getCompanyDetailsLookup,
      finalMap,
      context
    );
  });

  it('should handle errors gracefully when getCompanyDetailsLookup throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getCompanyDetailsLookup({ companyRegistrationNumber: 'TEST_123', scheme: 'GB' }, context)
    ).rejects.toThrow('Test error');
  });
});

describe('getApplicationDetails', () => {
  const finalMap = {
    applicationId: 'ID_123',
    applicationGuid: 'GUID_123',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when getApplicationDetails is called', async () => {
    await getApplicationDetails(
      { applicationId: 'ID_123', applicationGuid: 'GUID_123', scheme: 'GB' },
      context
    );
    expect(get).toHaveBeenCalledWith(
      endpoints.APPLICATION_DETAILS,
      getApplicationDetails,
      finalMap,
      context
    );
  });

  it('should handle errors gracefully when getApplicationDetails throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getApplicationDetails(
        { applicationId: 'ID_123', applicationGuid: 'GUID_123', scheme: 'GB' },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('shareApplication', () => {
  const shareApplicationRequest = {
    applicationId: '9999990',
    applicationGuid: '12u7gjk-9118-4683-8286-771b90169f00',
    employeeId: '1891413769',
    email: 'luciantesttwo@yopmail.com',
    fullName: 'Lucian TestTwo'
  };

  it('should call the post function with correct parameters when shareApplication is called', async () => {
    await shareApplication({ shareApplicationRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.SHARE_APPLICATION,
      shareApplication,
      {
        applicationId: '9999990',
        applicationGuid: '12u7gjk-9118-4683-8286-771b90169f00',
        employeeId: '1891413769',
        email: 'luciantesttwo@yopmail.com',
        fullName: 'Lucian TestTwo'
      },
      context
    );
  });

  it('should handle errors gracefully when shareApplication throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);
    await expect(shareApplication({ shareApplicationRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('removeParticipant', () => {
  const removeParticipantRequest = {
    applicationId: 'APP_456',
    applicationGuid: 'GUID_789',
    employeeId: 123
  };

  it('should call the post function with correct parameters when removeParticipant is called', async () => {
    await removeParticipant({ removeParticipantRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.REMOVE_PARTICIPANT,
      removeParticipant,
      {
        applicationId: 'APP_456',
        applicationGuid: 'GUID_789',
        employeeId: 123
      },
      context
    );
  });

  it('should handle errors gracefully when removeParticipant throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(removeParticipant({ removeParticipantRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('deleteApplicationCard', () => {
  const deleteApplicationCardRequest = {
    cardId: 'CARD_123',
    applicationId: 'APP_456',
    applicationGuid: 'GUID_789'
  };

  it('should call the post function with correct parameters when deleteApplicationCard is called', async () => {
    await deleteApplicationCard({ deleteApplicationCardRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.DELETE_APPLICATION_CARD,
      deleteApplicationCard,
      {
        cardId: 'CARD_123',
        applicationId: 'APP_456',
        applicationGuid: 'GUID_789'
      },
      context
    );
  });

  it('should handle errors gracefully when deleteApplicationCard throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(deleteApplicationCard({ deleteApplicationCardRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getApplicationCards', () => {
  const finalMap = {
    applicationGuid: 'GUID_123',
    scheme: 'GB',
    page: 1,
    maxDisplayRows: 12
  };

  it('should call the get function with correct parameters when getApplicationCards is called', async () => {
    await getApplicationCards(
      { applicationGuid: 'GUID_123', scheme: 'GB', page: 1, maxDisplayRows: 12 },
      context
    );
    expect(get).toHaveBeenCalledWith(
      endpoints.GET_APPLICATION_CARDS,
      getApplicationCards,
      finalMap,
      context
    );
  });

  it('should handle errors gracefully when getApplicationCards throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getApplicationCards(
        { applicationGuid: 'GUID_123', scheme: 'GB', page: 1, maxDisplayRows: 12 },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('submitApplication', () => {
  const submitApplicationRequest = {
    applicationGuid: 'GUID_123',
    applicationId: 'APP_456',
    scheme: 'GB',
    hostedPageGuid: 'GUID_789',
    registrationQuestion: 'Test question',
    registrationAnswer: 'Test answer',
    termsAndConditionAccepted: 'Y',
    directDebit: false
  };

  it('should call the post function with correct parameters when submitApplication is called', async () => {
    await submitApplication({ submitApplicationRequest }, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.SUBMIT_APPLICATION,
      submitApplication,
      submitApplicationRequest,
      context
    );
  });

  it('should handle errors gracefully when submitApplication throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(submitApplication({ submitApplicationRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('appPreCheck', () => {
  const finalMap = {
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when appPreCheck is called', async () => {
    await appPreCheck({ scheme: 'GB' }, context);
    expect(get).toHaveBeenCalledWith(endpoints.APP_PRE_CHECK, appPreCheck, finalMap, context);
  });

  it('should handle errors gracefully when appPreCheck throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(appPreCheck({ scheme: 'GB' }, context)).rejects.toThrow('Test error');
  });
});

describe('directDebit', () => {
  const directDebitRequest = {
    applicationId: 'APP_456',
    applicationGuid: 'GUID_123',
    resumeUrl: 'resumeUrl',
    directDebitOption: 'DIRECT'
  };

  it('should call the post function with correct parameters when directDebit is called', async () => {
    await directDebit({ directDebitRequest }, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.DIRECT_DEBIT,
      directDebit,
      directDebitRequest,
      context
    );
  });

  it('should handle errors gracefully when directDebit throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(directDebit({ directDebitRequest }, context)).rejects.toThrow('Test error');
  });
});

describe('updateResumeUrl', () => {
  const updateResumeUrlRequest = {
    applicationId: 'APP_456',
    applicationGuid: 'GUID_123',
    resumeUrl: 'resumeUrl'
  };

  it('should call the post function with correct parameters when updateResumeUrl is called', async () => {
    await updateResumeUrl({ updateResumeUrlRequest }, context);
    expect(post).toHaveBeenCalledWith(
      endpoints.UPDATE_RESUME_URL,
      updateResumeUrl,
      updateResumeUrlRequest,
      context
    );
  });

  it('should handle errors gracefully when updateResumeUrl throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(updateResumeUrl({ updateResumeUrlRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getDdSepaFormStatus', () => {
  const finalMap = {
    hostedPageGuid: 'GUID_123',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters when getDdSepaFormStatus is called', async () => {
    await getDdSepaFormStatus({ hostedPageGuid: 'GUID_123', scheme: 'GB' }, context);
    expect(get).toHaveBeenCalledWith(
      endpoints.GET_DD_SEPA_FORM_STATUS,
      getDdSepaFormStatus,
      finalMap,
      context
    );
  });

  it('should handle errors gracefully when getDdSepaFormStatus throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getDdSepaFormStatus({ hostedPageGuid: 'GUID_123', scheme: 'GB' }, context)
    ).rejects.toThrow('Test error');
  });
});
