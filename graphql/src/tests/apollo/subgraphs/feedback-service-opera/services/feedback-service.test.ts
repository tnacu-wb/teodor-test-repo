import { endpoints } from '../../../../../apollo/subgraphs/feedback-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { addFeedback } from '../../../../../apollo/subgraphs/feedback-service-opera/services/feedback-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('addFeedback', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const feedback = {
    title: 'Test Feedback Form',
    whb_contacttype: 130570009,
    whb_reasonforcontact: 3,
    whb_summary: 'the bad was broken',
    whb_wfbookingrefifapplicable: 'GAA0360432',
    whb_wfcontactnumber: '+40123456789',
    whb_wfdateofstayifapplicable: '2024-06-25',
    whb_wfemailaddress: 'test@email.com',
    whb_wffeedbacktype: 130570001,
    whb_wffirstname: 'Test',
    whb_wfhotelname: 'Test',
    whb_wfididntbookthroughpremierinncom: false,
    whb_wflastname: 'Test',
    whb_wfpostcode: '123456',
    whb_wfreasonforfeedback: 'Bed',
    whb_wfreported: true,
    whb_wfsleep: true,
    whb_wfsource: 'http://localhost'
  };

  it('should call the post function with correct parameters when adding feedback', async () => {
    await addFeedback({ feedback }, context);
    const serviceEndpoint = {
      ...endpoints.ADD_FEEDBACK
    };

    expect(post).toHaveBeenCalledWith(serviceEndpoint, addFeedback, feedback, context);
  });

  it('should handle errors gracefully when adding feedback fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(addFeedback({ feedback }, context)).rejects.toThrow('Test error');
  });
});
