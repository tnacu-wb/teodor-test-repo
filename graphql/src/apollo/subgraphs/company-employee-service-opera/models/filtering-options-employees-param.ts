export interface FilteringOptionsEmployeesParam {
  companyId: string;
  searchCriteria?: string;
  bookingChannel?: string;
  awaitingApproval?: boolean;
  size: number;
  page?: number;
  pageToken?: string;
  shouldFilterEmployees?: boolean;
}
