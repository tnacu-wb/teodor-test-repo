package uk.co.whitbread.integrationtests.framework.reporting

import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping

/**
 * Optional boundary through which reusable provisioning reports stub-install evidence.
 *
 * The provisioning implementation owns when events are emitted, while callers own how those
 * events are retained or rendered. Journey tests provide a scenario sink; callers that do not
 * need evidence omit the sink without changing installation or cleanup behavior.
 */
internal interface StubInstallEvidenceSink {
    /**
     * Records one mapping after WireMock has registered it successfully.
     *
     * @param mockName stable logical mock identifier selected by the installation plan.
     * @param mapping mapping accepted by WireMock.
     * @param testId scenario ownership identifier carried by the mapping.
     */
    fun recordInstalledStub(
        mockName: String,
        mapping: StubMapping,
        testId: String,
    )

    /**
     * Records that a model installation plan intentionally produced no mappings.
     *
     * @param modelName logical scenario-data model evaluated by the plan.
     * @param testId scenario ownership identifier for the plan.
     */
    fun recordEmptyPlan(
        modelName: String,
        testId: String,
    )

    /**
     * Records that exclusions removed every stub the model selected, so nothing was installed.
     *
     * Distinct from [recordEmptyPlan] so the evidence stays truthful when the scenario installs
     * its overrides directly afterwards: the model did select stubs; the author excluded them.
     *
     * @param modelName logical scenario-data model evaluated by the plan.
     * @param excluded stub IDs whose exclusion emptied the plan.
     * @param testId scenario ownership identifier for the plan.
     */
    fun recordFullyExcludedPlan(
        modelName: String,
        excluded: Set<String>,
        testId: String,
    )
}
