package uk.co.whitbread.marketing.model;

import uk.co.whitbread.marketing.model.permissionmanagement.Customer;
import uk.co.whitbread.marketing.model.permissionmanagement.SourceDetails;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;

public class SourceSystemFixture {

	public static UpdatePreferencesRequest buildRequestSourceSystemWEB() {

		SourceDetails sourceDetails = SourceDetails.builder().channel("WEB").locale("UK")
				.journey("PERMISSIONCENTRE").build();
		UpdatePreferencesRequest updatePreferencesRequest = buildBasePreferencesRequest();
		updatePreferencesRequest.setSourceDetails(sourceDetails);
		return updatePreferencesRequest;

	}

	public static UpdatePreferencesRequest buildRequestSourceSystemMobileiOS() {

		SourceDetails sourceDetails = SourceDetails.builder().channel("APPS_IOS").locale("DE").journey("SIGNUP")
				.build();

		UpdatePreferencesRequest updatePreferencesRequest = buildBasePreferencesRequest();
		updatePreferencesRequest.setSourceDetails(sourceDetails);
		return updatePreferencesRequest;

	}

	public static UpdatePreferencesRequest buildRequestSourceSystemMobileAndroid() {

		SourceDetails sourceDetails = SourceDetails.builder().channel("APPS_ANDROID").locale("UK").journey("NEWSLETTERSIGNUP")
				.build();

		UpdatePreferencesRequest updatePreferencesRequest = buildBasePreferencesRequest();
		updatePreferencesRequest.setSourceDetails(sourceDetails);
		return updatePreferencesRequest;
	}

	public static UpdatePreferencesRequest buildRequestSourceSystemBB() {

		SourceDetails sourceDetails = SourceDetails.builder().channel("BB").locale("DE").journey("SIGNUP").build();

		UpdatePreferencesRequest updatePreferencesRequest = buildBasePreferencesRequest();
		updatePreferencesRequest.setSourceDetails(sourceDetails);
		return updatePreferencesRequest;
	}

	public static UpdatePreferencesRequest buildBasePreferencesRequest() {

		Customer customer = Customer.builder().nationality("GB").countryOfResidence("DE").firstName("Liam")
				.lastName("Wilson").title("Mr").userId("liam.wilson1234").language("en").build();
		UpdatePreferencesRequest updatePreferencesRequest = UpdatePreferencesRequest.builder().secondPartyOptIn(true)
				.thirdPartyVendorsOptIn(false).brandCodes(new String[] { "PINN" }).optIn(true).doubleOptIn(true)
				.customer(customer).build();

		return updatePreferencesRequest;
	}

}