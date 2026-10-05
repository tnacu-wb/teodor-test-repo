package uk.co.whitbread.piba.account.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = WorldlineClientFallbackFactory.class)
class WorldlineClientFallbackFactoryTest {

    @Autowired
    private WorldlineClientFallbackFactory worldlineClientFallbackFactory;

    private Throwable mockThrowable;

    @BeforeEach
    void setup() {
        mockThrowable = mock(Throwable.class);
    }

    @Test
    void getHotelAccountClient_returnNull() {
        assertThat(worldlineClientFallbackFactory
                .create(mockThrowable)
                .getAccountInfo("companyNo", "{user}", "en-GB", "ip", "GUID-0000")).isNull();
    }
}