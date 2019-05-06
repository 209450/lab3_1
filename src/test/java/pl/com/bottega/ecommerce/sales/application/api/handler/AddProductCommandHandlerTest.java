package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import static org.junit.Assert.*;

public class AddProductCommandHandlerTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ProductRepository productRepository;
    @Mock private SuggestionService suggestionService;

    @Mock private ClientRepository clientRepository;
    @Mock private SystemContext systemContext;
    @Mock private AddProductCommandHandler addProductCommandHandler;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository,
                productRepository,suggestionService,clientRepository,systemContext);
    }


}
