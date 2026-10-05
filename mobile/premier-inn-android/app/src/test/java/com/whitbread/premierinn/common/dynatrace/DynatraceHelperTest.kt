package com.whitbread.premierinn.common.dynatrace

import com.dynatrace.android.agent.DTXAction
import com.dynatrace.android.agent.Dynatrace
import com.dynatrace.android.agent.conf.DataCollectionLevel
import com.dynatrace.android.agent.conf.UserPrivacyOptions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class DynatraceHelperTest {

    private lateinit var dynatraceMock: MockedStatic<Dynatrace>

    @BeforeEach
    fun setUp() {
        dynatraceMock = mockStatic(Dynatrace::class.java)
    }

    @AfterEach
    fun tearDown() {
        dynatraceMock.close()
    }

    @Test
    fun `initDynatrace should apply correct user privacy options`() {
        val userPrivacyOptions = UserPrivacyOptions.builder()
            .withDataCollectionLevel(DataCollectionLevel.USER_BEHAVIOR)
            .withCrashReportingOptedIn(false)
            .withCrashReplayOptedIn(false)
            .build()

        DynatraceHelper.initDynatrace()

        dynatraceMock.verify { Dynatrace.applyUserPrivacyOptions(userPrivacyOptions) }
    }

    @Test
    fun `trackAction should log correct action with parameters`() {
        val actionName = "TestAction"
        val actionKey = "TestKey"
        val doubleValue = 123.45
        val mockAction = mock(DTXAction::class.java)

        whenever(Dynatrace.enterAction(actionName)).thenReturn(mockAction)

        DynatraceHelper.trackAction(doubleValue, actionName, actionKey)

        verify(mockAction).reportValue(actionKey, doubleValue)
        verify(mockAction).leaveAction()
    }
}
