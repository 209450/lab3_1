package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.net.IDN;
import java.util.Currency;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;

public class AddProductCommandHandlerTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ProductRepository productRepository;
    @Mock private SuggestionService suggestionService;

    @Mock private ClientRepository clientRepository;
    @Mock private SystemContext systemContext;
    @Mock private AddProductCommandHandler addProductCommandHandler;

    private AddProductCommand addProductCommand;
    private Reservation reservation;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                clientRepository, systemContext);

        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 5);
        ClientData clientData = new ClientData(Id.generate(), "AAA");
        Reservation reservation = new Reservation(addProductCommand.getOrderId(), Reservation.ReservationStatus.OPENED, clientData,
                new Date());
        Product product = new Product(addProductCommand.getProductId(), new Money(new BigDecimal(100), Money.DEFAULT_CURRENCY), "Wazon",
                ProductType.STANDARD);

        Mockito.when(reservationRepository.load(any())).thenReturn(reservation);
        Mockito.when(productRepository.load(any())).thenReturn(product);
        Mockito.when(clientRepository.load(any())).thenReturn(new Client());
        Mockito.when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
        Mockito.doNothing().when(reservationRepository).save(any());
    }

    @Test public void givenProductThenClientRepositoryLoadMethodShouldNotBeCast() {
        Mockito.verify(clientRepository,times(0)).load(any());
    }

    @Test public void givenProductThenSuggestionServiceSuggestEquivalentShouldNotBeCast() {
        Mockito.verify(suggestionService,times(0)).suggestEquivalent(any(),any());
    }
}
