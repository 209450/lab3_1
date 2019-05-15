package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.mockito.stubbing.Answer;
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
import pl.com.bottega.ecommerce.sharedkernel.exceptions.DomainOperationException.DomainOperationException;
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
    private SystemContext systemContext;
    private AddProductCommandHandler addProductCommandHandler;

    private Product product;
    private AddProductCommand addProductCommand;
    private Reservation reservation;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        systemContext = new SystemContext();
        addProductCommandHandler = new AddProductCommandHandler(reservationRepository, productRepository, suggestionService,
                clientRepository, systemContext);

        addProductCommand = new AddProductCommand(Id.generate(), Id.generate(), 5);
        ClientData clientData = new ClientData(Id.generate(), "AAA");
        Reservation reservation = new Reservation(addProductCommand.getOrderId(), Reservation.ReservationStatus.OPENED, clientData,
                new Date());
        product = new Product(addProductCommand.getProductId(), new Money(new BigDecimal(100), Money.DEFAULT_CURRENCY), "Wazon",
                ProductType.STANDARD);

        Mockito.when(reservationRepository.load(any())).thenReturn(reservation);
        Mockito.when(productRepository.load(any())).thenReturn(product);
        Mockito.when(clientRepository.load(any())).thenReturn(new Client());
        Mockito.when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
        Mockito.doNothing().when(reservationRepository).save(any());
    }

    @Test public void givenProductThenClientRepositoryLoadMethodShouldNotBeCast() {
        addProductCommandHandler.handle(addProductCommand);
        Mockito.verify(clientRepository, times(0)).load(any());
    }

    @Test public void givenProductThenSuggestionServiceSuggestEquivalentShouldNotBeCast() {
        addProductCommandHandler.handle(addProductCommand);
        Mockito.verify(suggestionService, times(0)).suggestEquivalent(any(), any());
    }

    @Test public void notGivenProductThenClientRepositoryLoadMethodShouldBeCastOnce() {
        product.markAsRemoved();
        Mockito.when(suggestionService.suggestEquivalent(any(), any()))
               .thenReturn(new Product(Id.generate(), product.getPrice(), product.getName(), product.getProductType()));
        addProductCommandHandler.handle(addProductCommand);
        Mockito.verify(clientRepository, times(1)).load(any());
    }

    @Test public void notGivenProductThenSuggestionServiceSuggestEquivalentMethodShouldBeCastOnce() {
        product.markAsRemoved();
        Mockito.when(suggestionService.suggestEquivalent(any(), any()))
               .thenReturn(new Product(Id.generate(), product.getPrice(), product.getName(), product.getProductType()));
        addProductCommandHandler.handle(addProductCommand);
        Mockito.verify(suggestionService, times(1)).suggestEquivalent(any(), any());
    }

}
