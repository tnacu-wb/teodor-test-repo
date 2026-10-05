export interface EmployeesParams {
  companyId: string;
  searchCriteria?: string;
  bookingChannel?: string;
  awaitingApproval?: Boolean;
  size: number;
  page?: number;
  pageToken?: string;
}
