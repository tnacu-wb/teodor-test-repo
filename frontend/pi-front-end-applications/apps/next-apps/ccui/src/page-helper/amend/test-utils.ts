// Re-export mock data from utils source to avoid circular dependencies in tests
// Import from source instead of compiled package
export {
  mockPackagesData,
  mockBookingConfirmationAmend,
  mockStayRules,
  mockEmployeeStayRules,
  mockRoomOccupancyLimitations,
  mockedGetStaticContent,
} from '../../../../../../pi-components-catalog/utils/src/mockData/amend';
