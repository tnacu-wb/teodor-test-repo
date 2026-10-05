import { gql } from 'graphql-request';

export const GET_GLOBAL_CONFIG_QUERY = gql`
  query getGlobalConfig($brand: String, $channel: Channel!, $country: String, $language: String) {
    globalConfig(brand: $brand, channel: $channel, country: $country, language: $language) {
      maxRoomsLim {
        maxRooms
        maxRoomsAmend
      }
      roomClassConfig {
        code
        order
        availableUpgrades
      }
      roomUpgradeOptions {
        priceText
        primaryButtonText
        secondaryButtonText
        roomUpgrades {
          description
          heading
          imageUrl
          roomClass
        }
      }
      promotionsConfig {
        metaPromoRateMappingDtos {
          rate
          code
        }
      }
    }
  }
`;
