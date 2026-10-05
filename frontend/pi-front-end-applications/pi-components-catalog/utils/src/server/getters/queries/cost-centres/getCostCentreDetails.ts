import { getCostCenterDetailsQuery } from '@whitbread-eos/api';

import { executeGraphQLQuery } from '../../../';

const getCostCentreDetails = async (token: string, tetheredUserGuid: string) => {
  return await executeGraphQLQuery(
    getCostCenterDetailsQuery(),
    {
      tetheredUserGuid: tetheredUserGuid,
    },
    (result: any) => result.data.getCostCentreDetails,
    token,
    true,
    false
  );
};

export default getCostCentreDetails;
