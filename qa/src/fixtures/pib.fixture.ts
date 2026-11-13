import {
  AncillariesBBPage,
	AmendBookingPage,
	AccessRestrictedPage,
	ActivateYourAccountPage,
	ARequestHasBeenSentPage,
	AddEditEmployeePage,
	AddEditCentrallyStoredPage,
	AddNewEmployeesViaBulkUploadPage,
	BookingAlertsPage,
	BookingAllowancesPage,
	BookingHistoryPage,
	CompanyDetailsPage,
	ContactCentrePage,
	ContactUsPage,
	CardManagementPage,
	ConfirmBookingPage,
	ConfirmYourEmailPage,
	ConfirmationPage,
	CostCentresPage,
	CreateYourInnBusinessAccountPage,
	CreateInnBusinessPayPage,
	EditInnBusinessPayPage,
	EmployeeQuestionsPage,
	ForgottenPasswordPage,
	HotelDetailsPage,
	HomePage,
	InviteEmployeesToAddThemselvesSection,
	LoginIbPage,
	LoginAndSecurityDetailsPage,
	LinkAnInnBusinessPayAccountPage,
	ManageEmployeesPage,
	MaintenanceErrorPage,
	MyProfilePage,
	PaymentDetailsPage,
	PaymentPage,
	PaymentSixCardSol3dSecureHostPage,
	RegisterIBPayAccountPage,
	ResetPasswordPage,
	SecurityQuestionPage,
	SetAPasswordPage,
	SetResetMemorableWordPage,
	ApplyForInnBusinessPayPage,
	EmergencyReportPage,
	ManagementInformationReportPage,
	OutOfPolicyReportPage,
	PayApplicationPage,
	SpendingAndReportingPage,
	StatementsInvoicesAndPaymentsPage,
	ThisEmailAlreadyHasAnAccountPage,
	TransactionsPage,
	WelcomeToInnBusinessPage,
	LinkCodePage,
	RegisterPage,
	UserListPage,
	WelcomePage,
	WorldlinePage,
} from '../pages/pib';
import { BookingConfirmationPage, ChooseYourBathroomPage } from '../pages/shared';
import { test as base } from './base.fixture';

// ─── PIB Fixture Types ──────────────────────────────────────────────────────────

/**
 * PIB-specific page object fixtures.
 * Each fixture creates a fresh page object instance scoped to the test.
 */
type Pages = {
	ancillariesBBPage: AncillariesBBPage;
	amendBookingPage: AmendBookingPage;
	hotelDetailsPage: HotelDetailsPage;
	accessRestrictedPage: AccessRestrictedPage;
	activateYourAccountPage: ActivateYourAccountPage;
	aRequestHasBeenSentPage: ARequestHasBeenSentPage;
	addEditEmployeePage: AddEditEmployeePage;
	addEditCentrallyStoredCardPage: AddEditCentrallyStoredPage;
	addNewEmployeesViaBulkUploadPage: AddNewEmployeesViaBulkUploadPage;
	bookingAlertsPage: BookingAlertsPage;
	bookingAllowancesPage: BookingAllowancesPage;
	bookingHistoryPage: BookingHistoryPage;
	bookingConfirmationPage: BookingConfirmationPage;
	chooseYourBathroomPage: ChooseYourBathroomPage;
	confirmBookingPage: ConfirmBookingPage;
	companyDetailsPage: CompanyDetailsPage;
	contactCentrePage: ContactCentrePage;
	contactUsPage: ContactUsPage;
	cardManagementPage: CardManagementPage;
	confirmYourEmailPage: ConfirmYourEmailPage;
	confirmationPage: ConfirmationPage;
	costCentresPage: CostCentresPage;
	createYourInnBusinessAccountPage: CreateYourInnBusinessAccountPage;
	createInnBusinessPayCardPage: CreateInnBusinessPayPage;
	editInnBusinessPayCardPage: EditInnBusinessPayPage;
	employeeQuestionsPage: EmployeeQuestionsPage;
	forgottenPasswordPage: ForgottenPasswordPage;
	homePage: HomePage;
	inviteEmployeesPage: InviteEmployeesToAddThemselvesSection;
	loginIbPage: LoginIbPage;
	loginAndSecurityDetailsPage: LoginAndSecurityDetailsPage;
	linkAnInnBusinessPayAccountPage: LinkAnInnBusinessPayAccountPage;
	manageEmployeesPage: ManageEmployeesPage;
	maintenanceErrorPage: MaintenanceErrorPage;
	myProfilePage: MyProfilePage;
	paymentDetailsPage: PaymentDetailsPage;
	paymentPage: PaymentPage;
	paymentSixCardSol3dSecureHostPage: PaymentSixCardSol3dSecureHostPage;
	registerIBPayAccountPage: RegisterIBPayAccountPage;
	resetPasswordPage: ResetPasswordPage;
	securityQuestionPage: SecurityQuestionPage;
	setAPasswordPage: SetAPasswordPage;
	setResetMemorableWordPage: SetResetMemorableWordPage;
	applyForInnBusinessPayPage: ApplyForInnBusinessPayPage;
	emergencyReportPage: EmergencyReportPage;
	managementInformationReportPage: ManagementInformationReportPage;
	outOfPolicyReportPage: OutOfPolicyReportPage;
	payApplicationPage: PayApplicationPage;
	spendingAndReportingPage: SpendingAndReportingPage;
	statementsInvoicesAndPaymentsPage: StatementsInvoicesAndPaymentsPage;
	thisEmailAlreadyHasAnAccountPage: ThisEmailAlreadyHasAnAccountPage;
	transactionsPage: TransactionsPage;
	welcomeToInnBusinessPage: WelcomeToInnBusinessPage;
	worldlineLinkCodePage: LinkCodePage;
	worldlineRegisterPage: RegisterPage;
	worldlineUserListPage: UserListPage;
	worldlineWelcomePage: WelcomePage;
	worldlinePage: WorldlinePage;
};

declare global {
	var pibPages: Pages;
}

/**
 * PIB-specific test fixture type.
 * Extends the base fixture with all PIB page object fixtures.
 */
type PIBFixtures = {
	pages: Pages;
};

// ─── PIB Extended Test ──────────────────────────────────────────────────────────

/**
 * PIB test fixture — use this in all premierinnbusiness.com test specs.
 *
 * Inherits base fixtures (appPage with dialog handling) and adds
 * all PIB page objects as injectable fixtures.
 *
 * Usage in specs:
 *   import { test, expect } from '@fixtures/pib.fixture';
 *
 *   test('PIB: search hotel', async () => {
 *     // Add PIB page objects when they are implemented.
 *   });
 */
export const test = base.extend<PIBFixtures>({
	pages: async ({ page }, use) => {
		global.page = page;

		const pages = {
			ancillariesBBPage: new AncillariesBBPage(),
			amendBookingPage: new AmendBookingPage(),
			hotelDetailsPage: new HotelDetailsPage(),
			accessRestrictedPage: new AccessRestrictedPage(),
			activateYourAccountPage: new ActivateYourAccountPage(),
			aRequestHasBeenSentPage: new ARequestHasBeenSentPage(),
			addEditEmployeePage: new AddEditEmployeePage(),
			addEditCentrallyStoredCardPage: new AddEditCentrallyStoredPage(),
			addNewEmployeesViaBulkUploadPage: new AddNewEmployeesViaBulkUploadPage(),
			bookingAlertsPage: new BookingAlertsPage(),
			bookingAllowancesPage: new BookingAllowancesPage(),
			bookingHistoryPage: new BookingHistoryPage(),
			bookingConfirmationPage: new BookingConfirmationPage(),
			chooseYourBathroomPage: new ChooseYourBathroomPage(),
			confirmBookingPage: new ConfirmBookingPage(),
			companyDetailsPage: new CompanyDetailsPage(),
			contactCentrePage: new ContactCentrePage(),
			contactUsPage: new ContactUsPage(),
			cardManagementPage: new CardManagementPage(),
			confirmYourEmailPage: new ConfirmYourEmailPage(),
			confirmationPage: new ConfirmationPage(),
			costCentresPage: new CostCentresPage(),
			createYourInnBusinessAccountPage: new CreateYourInnBusinessAccountPage(),
			createInnBusinessPayCardPage: new CreateInnBusinessPayPage(),
			editInnBusinessPayCardPage: new EditInnBusinessPayPage(),
			employeeQuestionsPage: new EmployeeQuestionsPage(),
			forgottenPasswordPage: new ForgottenPasswordPage(),
			homePage: new HomePage(),
			inviteEmployeesPage: new InviteEmployeesToAddThemselvesSection(),
			loginIbPage: new LoginIbPage(),
			loginAndSecurityDetailsPage: new LoginAndSecurityDetailsPage(),
			linkAnInnBusinessPayAccountPage: new LinkAnInnBusinessPayAccountPage(),
			manageEmployeesPage: new ManageEmployeesPage(),
			maintenanceErrorPage: new MaintenanceErrorPage(),
			myProfilePage: new MyProfilePage(),
			paymentDetailsPage: new PaymentDetailsPage(),
			paymentPage: new PaymentPage(),
			paymentSixCardSol3dSecureHostPage: new PaymentSixCardSol3dSecureHostPage(),
			registerIBPayAccountPage: new RegisterIBPayAccountPage(),
			resetPasswordPage: new ResetPasswordPage(),
			securityQuestionPage: new SecurityQuestionPage(),
			setAPasswordPage: new SetAPasswordPage(),
			setResetMemorableWordPage: new SetResetMemorableWordPage(),
			applyForInnBusinessPayPage: new ApplyForInnBusinessPayPage(),
			emergencyReportPage: new EmergencyReportPage(),
			managementInformationReportPage: new ManagementInformationReportPage(),
			outOfPolicyReportPage: new OutOfPolicyReportPage(),
			payApplicationPage: new PayApplicationPage(),
			spendingAndReportingPage: new SpendingAndReportingPage(),
			statementsInvoicesAndPaymentsPage: new StatementsInvoicesAndPaymentsPage(),
			thisEmailAlreadyHasAnAccountPage: new ThisEmailAlreadyHasAnAccountPage(),
			transactionsPage: new TransactionsPage(),
			welcomeToInnBusinessPage: new WelcomeToInnBusinessPage(),
			worldlineLinkCodePage: new LinkCodePage(),
			worldlineRegisterPage: new RegisterPage(),
			worldlineUserListPage: new UserListPage(),
			worldlineWelcomePage: new WelcomePage(),
			worldlinePage: new WorldlinePage(),
		};
		global.pibPages = pages as Pages;
		await use(pages);
	},
});

export { expect } from '@playwright/test';
