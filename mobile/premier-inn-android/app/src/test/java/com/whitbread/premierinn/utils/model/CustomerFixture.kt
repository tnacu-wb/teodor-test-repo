package com.whitbread.premierinn.utils.model

import com.whitbread.premierinn.businessbooker.domain.company.BookingAllowances
import com.whitbread.premierinn.businessbooker.domain.company.CellCode
import com.whitbread.premierinn.businessbooker.domain.company.Company
import com.whitbread.premierinn.businessbooker.domain.company.CompanyDetails
import com.whitbread.premierinn.businessbooker.domain.company.CompanyManagementDetails
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationAnswer
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationQuestion
import com.whitbread.premierinn.businessbooker.domain.company.PriceCapLocations
import com.whitbread.premierinn.businessbooker.domain.company.QuestionType
import com.whitbread.premierinn.businessbooker.domain.company.RequestCompany
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Passport
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.customer.entity.AccessLevel
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Business
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard

object CustomerFixture {
    @JvmOverloads
    fun aCustomer(
            id: String = "CUSTOMER01",
            fullName: FullName = FullName(title = "Mr", firstName = "Richie", lastName = "Rich"),
            address: Address = Address(line1 = "Money Lane", countryCode = "GB", postCode = "N1 7BD"),
            contact: Contact = Contact(email = "rr@rich.com", mobile = "05648383833"),
            nationality: String = "GB",
            passport: Passport = Passport("8998877788", "GB"),
            business: Boolean = false,
            bookingPreferences: BookingPreferences = BookingPreferences(mealPreference = 11,
                    roomCriteriaPreference = RoomCriteria(numberOfAdults = 2, numberOfChildren = 1,
                            numberOfInfants = 0, includeCot = true, roomType = RoomType.FAMILY)),
            paymentCard: PaymentCard = PaymentCard(number = "************1111",
                    expiryDate = "06/23",
                    holdersFullName = "Android Bot",
                    cardType = ""),
            carRegistration: String = "GBVHYI9889",
            customerID: String = "234234234",): Customer {
        return Customer(
                id,
                fullName,
                contact,
                address,
                nationality,
                passport,
                business,
                bookingPreferences,
                paymentCard,
                carRegistration,
                customerID)
    }

        @JvmOverloads
        fun bCustomer(
                id: String = "BBCUSTOMER01"): Customer {
                return aCustomer().copy(
                        customerAccountID = id,
                        guestHistoryNumber = id,
                        company = mockCompany(),
                        business = mockBusiness(),
                        companyId = "876"
                )
        }

        @JvmOverloads
        fun bCustomerWithNoAdditionalQuestions(
            id: String = "BBCUSTOMER01"): Customer {
            val companyDetails = CompanyDetails("Test BB company", "Test Company")
            val priceCapLocations =
                PriceCapLocations(
                    PriceDomain(0f, GBP),
                    PriceDomain(0f, GBP),
                    PriceDomain(0f, "EUR")
                )
            val upsellItems = listOf("11", "15", "12", "17", "135", "136", "137", "18")
            val bookingAllowances = BookingAllowances(
                priceCapLocations, upsellItems,
                allowAlcohol = false,
                allowCarParking = false,
                allowPremierSaverRates = true,
                allowIndividualCards = true
            )
            val requestedCompany = RequestCompany(
                    companyDetails,
                    null,
                    bookingAllowances,
                    null
                )

            val cellCodes = CellCode("1", "BFLEX")

            return aCustomer().copy(
                customerAccountID = id,
                guestHistoryNumber = id,
                company =  Company (requestedCompany, listOf(cellCodes),
                    true),
                business = mockBusiness(),
                companyId = "876")

        }

        private fun mockBusiness(): Business {
                return Business(AccessLevel.BOOKER, "1234", "4567", "987")
        }

        private fun mockCompany(): Company {
                val companyDetails = CompanyDetails("Test BB company", "Test Company")
                val priceCapLocations =
                        PriceCapLocations(
                                PriceDomain(0f, GBP),
                                PriceDomain(0f, GBP),
                                PriceDomain(0f, "EUR")
                        )
                val upsellItems = listOf("11", "15", "12", "17", "135", "136", "137", "18")
                val bookingAllowances = BookingAllowances(
                        priceCapLocations, upsellItems,
                        allowAlcohol = false,
                        allowCarParking = false,
                        allowPremierSaverRates = true,
                        allowIndividualCards = true
                )

                val managementInformationAnswerNoAns =
                        ManagementInformationAnswer(answerType = null, answers = emptyList())
                val purchaseOrderManagement = ManagementInformationQuestion(
                        "COae9001790",
                        "2",
                        mandatory = true,
                        managementHeader = "purchase",
                        location = "B",
                        active = true,
                        managementInformationAnswer = managementInformationAnswerNoAns,
                        questionType = QuestionType.PURCHASE_ORDER
                )
                val customerRefManagement = ManagementInformationQuestion(
                        "COae9001791",
                        "2",
                        mandatory = false,
                        managementHeader = "customerref",
                        location = "B",
                        active = false,
                        managementInformationAnswer = managementInformationAnswerNoAns,
                        questionType = QuestionType.USER_DEFINED
                )
                val managementInformationAnswerDropdown =
                        ManagementInformationAnswer(answerType = "U", answers = listOf("one", "two"))
                val managementInformationQuestionOne = ManagementInformationQuestion(
                        "COae9001797",
                        "Question one",
                        mandatory = false,
                        managementHeader = "header info one",
                        location = "B",
                        active = true,
                        managementInformationAnswer = managementInformationAnswerDropdown,
                        questionType = QuestionType.USER_DEFINED
                )
                val managementInformationAnswerTextField =
                        ManagementInformationAnswer(answerType = "F", answers = emptyList())
                val managementInformationQuestionTwo = ManagementInformationQuestion(
                        "COae9001798",
                        "Question two",
                        mandatory = false,
                        managementHeader = "header info two",
                        location = "B",
                        active = false,
                        managementInformationAnswer = managementInformationAnswerTextField,
                        questionType = QuestionType.USER_DEFINED
                )
                val managementInformationQuestionThree = ManagementInformationQuestion(
                        "COae9001798",
                        "Question two",
                        mandatory = false,
                        managementHeader = "header info two",
                        location = "R",
                        active = false,
                        managementInformationAnswer = managementInformationAnswerTextField,
                        questionType = QuestionType.USER_DEFINED
                )
                val companyManagementDetails = CompanyManagementDetails(
                        purchaseOrderManagement,
                        customerRefManagement,
                        listOf(managementInformationQuestionOne, managementInformationQuestionTwo,
                                managementInformationQuestionThree)
                )
                val requestCompany =
                        RequestCompany(
                                companyDetails,
                                null,
                                bookingAllowances,
                                companyManagementDetails
                        )
                val cellCodes = CellCode("1", "BFLEX")
                return Company (requestCompany, listOf(cellCodes), true)
        }
        }
