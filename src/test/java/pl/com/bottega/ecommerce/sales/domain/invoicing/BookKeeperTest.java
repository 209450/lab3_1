package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData;
    private ProductData productData1;
    private ProductData productData2;
    private Tax tax;
    private Id id;
    private Money defaultMoney;
    private ProductData defaultProdutData;
    private RequestItem defaultRequestItem;

    private TaxPolicy taxPolicy;

    @Before public void setUp() throws Exception {
        id = Id.generate();
        clientData = new ClientData(id, "Wojtek");
        invoiceRequest = new InvoiceRequest(clientData);
        bookKeeper = new BookKeeper(new InvoiceFactory());

        tax = new Tax(new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY), "Podatek");
        taxPolicy = Mockito.mock(TaxPolicy.class);
        Mockito.when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);

        defaultMoney = new Money(new BigDecimal(100), Money.DEFAULT_CURRENCY);
        defaultProdutData = new ProductData(Id.generate(), defaultMoney, "jojo", ProductType.FOOD, new Date());
        defaultRequestItem = new RequestItem(defaultProdutData, 1, defaultMoney);
    }

    @Test public void givenInvoiceWithOneElementReturnInvoiceWithOneElement() {
        invoiceRequest.add(defaultRequestItem);

        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicy).getItems().size(), is(1));
    }

    @Test public void givenInvoiceWithTwoElementsInvokeCalculateTaxMethodTwoTimes() {
        Money money = new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY);
        ProductData productData = new ProductData(Id.generate(), money, "jojo", ProductType.FOOD, new Date());
        RequestItem requestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(defaultRequestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(any(), any());
    }

    @Test public void givenInvoiceWithOneElementIssuanceReturnsElementValueWithTax() {
        invoiceRequest.add(defaultRequestItem);

        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicy).getGros().toString(), is("110,00 €"));
    }

    @Test public void givenInvoiceWithOneElementIssuanceReturnsElementValue() {
        invoiceRequest.add(defaultRequestItem);

        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicy).getNet().toString(), is("100,00 €"));
    }

    @Test public void givenInvoiceWithoutElementIssuanceMethodNotInvokeCalculateTaxMethod() {
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(0)).calculateTax(any(), any());
    }

    @Test public void givenInvoiceWithTwoElementsIssuanceReturnsElementValueSum() {
        Money money = new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY);
        ProductData productData = new ProductData(Id.generate(), money, "jojo", ProductType.FOOD, new Date());
        RequestItem requestItem = new RequestItem(productData, 1, money);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(defaultRequestItem);

        assertThat(bookKeeper.issuance(invoiceRequest, taxPolicy).getNet().toString(), is("110,00 €"));
    }

}
