import { QueryClient } from '@tanstack/react-query';

import { getListOfEmployees } from './search-employees';

const token =
  'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InAzblU5b3M0RTBubGRMVF9ROHBnbSJ9.eyJodHRwczovL2NjdWkub3BlcmEud2hpdGJyZWFkLmRpZ2l0YWwvcm9sZSI6W10sIndiX2FjY291bnRfbG9jYWxlIjoiZW4iLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzc4NDYwNjNlLWFjZDEtNDkzMS04YzA3LTFjMzNkZGMyYTQyZiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMX2I0NWZkNzU1LWFhY2UtNGU5OS05MmIxLWRmN2NkZDUwOWU1MiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxMzYxLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoidHJhdmVsaW5nLmJnbEBtYWlsaW5hdG9yLmNvbSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vb3BlcmFDb21wYW55SWQiOiIyNTY5NjE2Iiwibmlja25hbWUiOiJ0cmF2ZWxpbmcuYmdsIiwicHJvZmlsZSI6eyJhY2Nlc3NMZXZlbCI6IlNVUEVSIiwiY29tcGFueUlkIjoiMzUwODYiLCJlbXBsb3llZUlkIjoiMSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6IjhSdHIxUktlaEFPb21rU2IifSwibmFtZSI6InRyYXZlbGluZy5iZ2xAbWFpbGluYXRvci5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvYzYyNjU3YzE4NmFiNWIzMjNjOWFhZWJkYzJiZjExY2M_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ0ci5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyMy0wMy0wN1QxNDo0MTo0Ni4wMDFaIiwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5zYW5kYm94LndoaXRicmVhZC5kaWdpdGFsLyIsImF1ZCI6IjhLT0NKa3o3MXBXRFlhamFNQUxhZWJKdVczQ0Nxb3ZzIiwiaWF0IjoxNjc4MjAwMTA3LCJleHAiOjE2NzgyMDE2MDcsInN1YiI6ImF1dGgwfDYzZWY0MzlkZDc0ZTZmOTZkYjAxZmEzYyIsImF0X2hhc2giOiJJalRkVEJhd2lHRmItRXVIUFNZVk93Iiwibm9uY2UiOiJaSGd3Sms2NVhkZ0g4QUZNV2pFeEpLZFNoZlRYMU5UeSJ9.E11UiJpwGnx5aj4WQvfdsjj5h2_h9MS-kiXMffYaPqk5x9QCegySS6kK0S_Z7rOKJG2QHtKH-wYyIyz-5moDA2u1iT4PLHtdwD4XoXtB-H8aKfvlpuMlCgeH9yoFrlk9NOJFcYiN8oNjL8hUymX4JOvZHOxwMbyJ-HNOcPtuWFd-HZqTrWlNXBu5XH459uDOhZGs1Ll425gbS8tP-Wa7F6liJG7046y4FVELwXZyoksKWFxmCxHrW8gEw3BCh4Ht4F_j2ORPbs0idt2VuNhp6NVuysCh0eks7CeyLLW951KCp40OW3P5P3KoK89T9ty0zpHXHlvCXKDDCmil962d7g';

const axiosRequestResp = [
  {
    id: '1',
    title: 'Miss',
    firstName: 'Boatyness',
    lastName: 'McBoatss',
    emailAddress: 'traveling.cgl@mailinator.com',
    textConfirmation: false,
    accessLevel: 'SUPER',
    employeeStatus: 'ACTIVE',
    guestHistoryNumber: 'G80686464',
    lockedForEditing: false,
  },
  {
    id: '2',
    title: 'Ms',
    firstName: 'Ella',
    lastName: 'test',
    emailAddress: 'booker.cgl@mailinator.com',
    textConfirmation: false,
    accessLevel: 'BOOKER',
    employeeStatus: 'ACTIVE',
    guestHistoryNumber: 'G80686539',
    lockedForEditing: false,
  },
  {
    id: '3',
    title: 'Ms',
    firstName: 'Ella',
    lastName: 'test',
    emailAddress: 'booker.cgl1@mailinator.com',
    textConfirmation: false,
    accessLevel: 'BOOKER',
    employeeStatus: 'ACTIVE',
    guestHistoryNumber: 'G80686542',
    lockedForEditing: false,
  },
  {
    id: '4',
    title: 'Ms',
    firstName: 'Ella',
    lastName: 'test',
    emailAddress: 'selfbooker.cgl@mailinator.com',
    textConfirmation: false,
    accessLevel: 'SELF',
    employeeStatus: 'ACTIVE',
    guestHistoryNumber: 'G80686543',
    lockedForEditing: false,
  },
  {
    id: '5',
    title: 'Ms',
    firstName: 'Ella',
    lastName: 'test',
    emailAddress: 'guest.cgl@mailinator.com',
    textConfirmation: false,
    accessLevel: 'STAYER',
    employeeStatus: 'ACTIVE',
    guestHistoryNumber: 'G80686551',
    lockedForEditing: false,
  },
];

const mockGetAuthCookie = jest.fn();
const mockGetLoggedInUserInfo = jest.fn();
jest.mock('./auth', () => ({
  ...jest.requireActual('./auth'),
  getAuthCookie: () => mockGetAuthCookie(),
  getLoggedInUserInfo: () => mockGetLoggedInUserInfo(),
}));

jest.mock('../hooks/use-request', () => ({
  ...jest.requireActual('../hooks/use-request'),
  axiosRequest: jest.fn().mockImplementation(() => axiosRequestResp),
}));

const mockProps = {
  awaitingApproval: false,
  bookingChannel: 'CBT',
  page: 1,
  searchCriteria: '',
  size: 10,
  queryClient: new QueryClient(),
};

describe('getListOfEmployees Method', () => {
  beforeEach(() => {
    mockGetAuthCookie.mockImplementation(() => token);
    mockGetLoggedInUserInfo.mockImplementation(() => ({
      cdhEmployeeId: 'cdhEmployeeId',
      sessionId: 'sessionId',
      cdhCompanyId: 'cdhCompanyId',
    }));
  });

  it('should return a list of employees', async () => {
    expect(await getListOfEmployees(mockProps)).toEqual(axiosRequestResp);
  });

  it('should return an empty list of employees if the user is not authenticated - auth cookie is empty', async () => {
    mockGetAuthCookie.mockImplementation(() => '');
    expect(await getListOfEmployees(mockProps)).toEqual([]);
  });

  it('should return an empty list of employees if the logged in user info is missing', async () => {
    mockGetLoggedInUserInfo.mockImplementation(() => ({
      cdhEmployeeId: '',
      sessionId: '',
      cdhCompanyId: '',
    }));
    expect(await getListOfEmployees(mockProps)).toEqual([]);
  });
});
