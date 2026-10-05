import { get } from '../../../../../../src/apollo/client/rest-client';
import { getRoomSubstitutionLimitation } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/room-substitution-rule-service';
import { endpoints } from '../../../../../../src/apollo/subgraphs/rules-agent-entity-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

const context = {};

describe('getRoomSubstitutionLimitation', () => {
  it('should return paypal rule when given hotel IDs', async () => {
    const expectedResponse = {
      substitutionList: [
        {
          type: 'DBLWIN',
          specialRequest: 'SING',
          silent: true,
          accessibleSpecialRequest: null,
          codePackage: null
        },
        {
          type: 'WINCMB',
          specialRequest: 'SING',
          silent: true,
          accessibleSpecialRequest: null,
          codePackage: null
        }
      ]
    };
    const mockResponse = {
      requestDetails: {
        adults: 1,
        children: 0,
        roomType: 'DB',
        pms: 'OP',
        channel: 'DISTR'
      },
      generatedAt: '2025-02-05T19:29:25.314021694',
      'substitution-list': [
        {
          type: 'DBLWIN',
          silent: true,
          specialRequest: 'SING',
          codePackage: null,
          accessibleSpecialRequest: null
        },
        {
          type: 'WINCMB',
          silent: true,
          specialRequest: 'SING',
          codePackage: null,
          accessibleSpecialRequest: null
        }
      ]
    };
    const roomSubstitutionCriteria = {
      roomSubstitutionCriteria: {
        adults: 1,
        children: 0,
        roomType: 'DB',
        pms: 'OP',
        channel: 'PI'
      }
    };
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);
    const result = await getRoomSubstitutionLimitation(roomSubstitutionCriteria, context);

    expect(result).toEqual(expectedResponse);
    expect(get).toHaveBeenCalledWith(
      endpoints.ROOM_SUBSTITUTION_LIMITATIONS,
      getRoomSubstitutionLimitation,
      {
        adults: 1,
        children: 0,
        roomType: 'DB',
        pms: 'OP',
        channel: 'PI'
      },
      context
    );
  });
});
