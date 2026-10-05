// Re-export mock data from utils source (not compiled) to avoid circular dependencies in tests
export {
  mockPackagesData,
  mockBookingConfirmationAmend,
  mockStayRules,
  mockEmployeeStayRules,
  mockRoomOccupancyLimitations,
  mockedGetStaticContent,
} from '../../../../../../pi-components-catalog/utils/src/mockData/amend';
