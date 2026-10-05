import { format } from 'date-fns';

import { analytics } from '../../services/analyticsService';
import { FormFields } from './types';

export const updateAnalytics = ({
  adults,
  adultsByEnquiry,
  children,
  childrenByEnquiry,
  consent,
  date,
  emailAddress,
  firstname,
  highchair,
  lastname,
  privacyStatement,
  specialRequest,
  wheelchair,
}: FormFields) => {
  analytics.update({
    restaurants: {
      adults: +adultsByEnquiry || +adults,
      children: +childrenByEnquiry || +children,
      guests: Number(+adultsByEnquiry || +adults) + Number(+childrenByEnquiry || +children),
      date: format(new Date(date), 'dd/MM/yyyy'),
      isUserDataEmail: Boolean(emailAddress),
      isUserDataForeName: Boolean(firstname),
      isUserDataSurName: Boolean(lastname),
      highChairs: highchair,
      largeGroupsEnquiry: +adultsByEnquiry + +childrenByEnquiry > 16,
      additionalRequirements: Boolean(specialRequest),
      wheelChairAccess: wheelchair,
      requestsComments: Boolean(specialRequest),
      isUserDataConfirmTerms: privacyStatement,
      isUserDataConfirmInput: consent,
      allowMarketing: consent,
    },
  });
};
