import { getDdSepaFormStatusQuery, Scheme } from '@whitbread-eos/api';

import { GqlResponse } from '../..';
import { executeGraphQLQuery } from '../../..';

const getDdSepaFormStatus = async (token: string, hostedPageGuid: string, scheme: Scheme) => {
  const response = await executeGraphQLQuery(
    getDdSepaFormStatusQuery(),
    {
      hostedPageGuid: hostedPageGuid,
      scheme: scheme,
    },
    (result: GqlResponse) => result?.data?.getDdSepaFormStatus,
    token,
    true,
    false
  );

  return response;
};

export default getDdSepaFormStatus;
