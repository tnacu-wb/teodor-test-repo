package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;
import uk.co.whitbread.dashboard.domain.ports.primary.DashboardInPort;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper.DashboardElementMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper.RetrieveDashboardRequestMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.in.RetrieveDashboardRequestDto;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.out.DashboardElementDto;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

  public static final String AUTH = "AUTH";
  public static final String LOCAL = "local";
  public static final boolean RECENT_SEARCHES = true;
  public static final String CUSTOMER_ID = "test@best.com";
  @InjectMocks
  DashboardController dashboardControllerUnderTest;

  @Mock
  private DashboardInPort dashboardInPort;

  @Mock
  private RetrieveDashboardRequestMapper retrieveDashboardRequestMapper;

  @Mock
  private DashboardElementMapper dashboardElementMapper;

  @Test
  void retrieveDashboard_ShouldReturnListDashboard() {
    // Arrange
    final List<DashboardElement> dashboardElements = new ArrayList<>();
    final DashboardElement dashboardElement = new DashboardElement(DashboardType.PAST_SEARCHES, null);
    dashboardElements.add(dashboardElement);

    final RetrieveDashboardRequestDto retrieveDashboardRequestDto = mock(RetrieveDashboardRequestDto.class);
    final RetrieveDashboardRequest retrieveDashboardRequest = mock(RetrieveDashboardRequest.class);

    when(retrieveDashboardRequestMapper.toModel((retrieveDashboardRequestDto))).thenReturn(retrieveDashboardRequest);
    when(dashboardInPort.retrieveDashboard(any(), any(), any(), anyBoolean(), any(), any())).thenReturn(dashboardElements);
    when(dashboardElementMapper.toDto(dashboardElement)).thenReturn(new DashboardElementDto());

    // Act
    final List<DashboardElementDto> dashboardElementDtoList = dashboardControllerUnderTest.retrieveDashboard(AUTH,
        null, LOCAL, RECENT_SEARCHES,
        CUSTOMER_ID, retrieveDashboardRequestDto);

    // Assert
    Assertions.assertThat(dashboardElementDtoList).isNotNull();
  }




}
