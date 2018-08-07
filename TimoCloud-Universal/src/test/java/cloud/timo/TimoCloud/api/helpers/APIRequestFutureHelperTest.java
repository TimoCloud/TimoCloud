package cloud.timo.TimoCloud.api.helpers;

import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestFuture;
import cloud.timo.TimoCloud.api.async.APIResponse;
import cloud.timo.TimoCloud.communication.CommunicationTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class APIRequestFutureHelperTest extends CommunicationTest {

    @Mock
    private APIRequest apiRequest;
    @Mock
    private APIResponse apiResponse;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void callRequestComplete() {
        APIRequestFuture apiRequestFuture = new APIRequestFuture(apiRequest);
        APIRequestFutureHelper.callRequestComplete(apiRequestFuture, apiResponse);
        assertEquals(apiResponse, apiRequestFuture.getResponse());
    }
}