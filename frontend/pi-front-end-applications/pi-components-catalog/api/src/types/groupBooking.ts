export type validateGroupBookingParams = {
  t: (id: string) => string;
  currentLang: string | undefined;
  isSchoolYouthEnabled?: boolean;
  hideCompanyName?: boolean;
  hideReasonForVisit?: boolean;
  reasonForVisitOther?: boolean;
};
