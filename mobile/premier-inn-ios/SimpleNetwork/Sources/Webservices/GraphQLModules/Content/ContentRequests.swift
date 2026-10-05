//
//  HotelSearchRequests.swift
//  SimpleNetwork
//
//  Created by Louis Faria-Softly on 14/11/2022.
//  Copyright © 2022 Whitbread. All rights reserved.
//

import Foundation
import Alamofire

extension GraphQL {
    func getHotel(with hotelCode: String) throws -> Resource<Hotel> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.combinedHotelInfoAndCategoryLabelsQuery

        parameters[.variablesKey] = GraphQL.getHotelParameters(hotelCode: hotelCode)

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data -> Hotel in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDictionary = data[.dataKey] as? PIDictionary, dataDictionary.isEmpty == false else {
                // TODO: error handling
                throw GraphQLError.missingData
            }

            guard let hotelInformationDictionary = dataDictionary["hotelInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            var mappedLabels: PIDictionary? {
                guard let categoryLabelsDict = dataDictionary["categoryLabels"] as? PIDictionary,
                      let labelsString = categoryLabelsDict["labels"] as? String,
                      let labelData = labelsString.data(using: .utf8), let jsonString = try? JSONSerialization.jsonObject(
                          with: labelData,
                          options: .allowFragments
                      ) as? String, let labelJsonData = jsonString.data(using: .utf8),
                      let labelJson = try? JSONSerialization.jsonObject(with: labelJsonData) as? PIDictionary
                else { return nil }
                  return LabelsMapper.mapLabels(labelsType: .roomDisclaimer, from: labelJson)
            }

            let graphQLHotelDict = HotelInformationMapper.map(from: hotelInformationDictionary, disclaimer: mappedLabels)

            return try Hotel(dictionary: graphQLHotelDict)
        }
    }

    func getHotelBySlug(slug: String) throws -> Resource<Hotel> {
         guard let url = baseURL?.appendingPathComponent(.graphQLPath)
             else { throw WebserviceError.invalidPath(.graphQLPath) }

         var parameters = PIDictionary()
         parameters[.queryKey] = GraphQL.hotelInfoBySlugQuery
         parameters[.variablesKey] = GraphQL.slugBasedHotelInformationsVariables(slug: slug)

         return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
             guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
             guard let dataDict = data["data"] as? PIDictionary,
                   let hotelInfoDict = dataDict["hotelInformationBySlug"] as? PIDictionary
             else { throw GraphQLError.missingData }

             let graphQLHotelDict = HotelInformationMapper.map(from: hotelInfoDict, disclaimer: nil)
             return try Hotel(dictionary: graphQLHotelDict)
         }
     }

    func headerInformation() throws -> Resource<HeaderInformation> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.headerInformationQuery
        parameters[.variablesKey] = GraphQL.headerInformationVariables()

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let headerInformationDict = dataDict["headerInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let headerInformationData = try JSONSerialization.data(
                    withJSONObject: headerInformationDict,
                    options: .prettyPrinted
                )

                let decoder = JSONDecoder()
                return try decoder.decode(HeaderInformation.self, from: headerInformationData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func roomTypeInformation() throws -> Resource<[RoomTypeInformation]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.roomTypeInformationQueryStandalone
        parameters[.variablesKey] = GraphQL.roomTypeInformationVariables()

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let roomTypeInformationDict = dataDict["roomTypeInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let roomTypeInformationData = try JSONSerialization.data(
                    withJSONObject: roomTypeInformationDict,
                    options: .prettyPrinted
                )

                let decoder = JSONDecoder()
                return try decoder.decode([RoomTypeInformation].self, from: roomTypeInformationData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func ratesInformation(
        ratePlans: [String],
        hotelCode: String,
        hotelBrand: HotelBrand?
    ) throws -> Resource<[RateInformation]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.ratesInformationQueryStandalone
        parameters[.variablesKey] = GraphQL.ratesInformationVariables(
            ratePlans: ratePlans,
            hotelCode: hotelCode,
            hotelBrand: hotelBrand
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let ratesInformationDict = dataDict["ratesInformation"] as? PIDictionary
                else { throw RequestsManagerError.unexpectedResponseError }
            guard let rateClassificationsDict = ratesInformationDict["rateClassifications"] as? [PIDictionary]
                else { throw RequestsManagerError.unexpectedResponseError }

            do {
                let ratesInformationData = try JSONSerialization.data(
                    withJSONObject: rateClassificationsDict,
                    options: .prettyPrinted
                )

                let decoder = JSONDecoder()
                return try decoder.decode([RateInformation].self, from: ratesInformationData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func getRestrictions() throws -> Resource<[Restrictions]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.maxRestrictionsCombinedQuery

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }

            let sortedDict = RestrictionsMapper.map(input: dataDict)
            do {
                let restrictionsData = try JSONSerialization.data(withJSONObject: sortedDict, options: .prettyPrinted)

                let decoder = JSONDecoder()
                return try decoder.decode([Restrictions].self, from: restrictionsData)
            } catch {
                throw DashboardComponentError.decodeError
            }
        }
    }

    func getCountries() throws -> Resource<[Country]> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.countriesQuery
        parameters[.variablesKey] = GraphQL.getCountriesVariables()

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let countries = (dataDict["countries"] as? PIDictionary)?["countries"] as? [PIDictionary]
                else { throw ResponseParserError.keyNotFound("countries") }

            do {
                let countriesData = try JSONSerialization.data(withJSONObject: countries, options: .prettyPrinted)
                let decoder = JSONDecoder()
                return try decoder.decode([Country].self, from: countriesData)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }

    func getCategoryLabels(labelType: LabelsConfig) throws -> Resource<CategoryLabels> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }
        var parameters = PIDictionary()
        parameters[.queryKey] = GraphQL.getCategoryLabelsQuery
        parameters[.variablesKey] = GraphQL.getCategoryLabelsVariables(
            category: labelType.category,
            labels: labelType.labels
        )
        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let categoryLabelsDict = dataDict["categoryLabels"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("categoryLabels") }
            guard let labelsString = categoryLabelsDict["labels"] as? String
                else { throw ResponseParserError.keyNotFound("labels") }
            guard let labelData = labelsString.data(using: .utf8) else { throw RequestsManagerError.unexpectedResponseError }
            let jsonString = try? JSONSerialization.jsonObject(with: labelData, options: .allowFragments) as? String
            guard let labelJsonData = jsonString?.data(using: .utf8),
                  let labelJson = try JSONSerialization.jsonObject(with: labelJsonData) as? PIDictionary,
                  let mappedLabels = LabelsMapper.mapLabels(labelsType: labelType, from: labelJson)
            else { throw RequestsManagerError.unexpectedResponseError }
            return CategoryLabels(labels: mappedLabels)
        }
    }

    func homepageAppsContent(
        channel: Channel,
        subchannel: String,
        language: String,
        country: String
    ) throws -> Resource<HomepageAppsContent> {
        guard let url = baseURL?.appendingPathComponent(.graphQLPath)
            else { throw WebserviceError.invalidPath(.graphQLPath) }

        var parameters = PIDictionary()

        parameters[.queryKey] = GraphQL.homepageAppsContent
        parameters[.variablesKey] = GraphQL.homepageContentVariables(
            channel: channel,
            subchannel: subchannel,
            language: language,
            country: country
        )

        return resource(url: url, parameters: parameters, method: .post, encoding: JSONEncoding.default) { data in
            guard let data = data as? PIDictionary else { throw RequestsManagerError.unexpectedResponseError }
            guard let dataDict = data["data"] as? PIDictionary else { throw ResponseParserError.keyNotFound("data") }
            guard let homepageAppsContentDict = dataDict["homepageAppsContent"] as? PIDictionary
                else { throw ResponseParserError.keyNotFound("homepageAppsContent") }

            do {
                let homepageAppsContent = try JSONSerialization.data(
                    withJSONObject: homepageAppsContentDict,
                    options: .prettyPrinted
                )
                let decoder = JSONDecoder()
                return try decoder.decode(HomepageAppsContent.self, from: homepageAppsContent)
            } catch {
                throw GraphQLError.decodeError
            }
        }
    }
}
