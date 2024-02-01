package br.com.fullcycle.hexagonal.application.usecases.event;

import br.com.fullcycle.hexagonal.application.domain.customer.Customer;
import br.com.fullcycle.hexagonal.application.domain.customer.CustomerId;
import br.com.fullcycle.hexagonal.application.domain.event.Event;
import br.com.fullcycle.hexagonal.application.domain.event.EventId;
import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repository.InMemoryCustomerRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryEventRepository;
import br.com.fullcycle.hexagonal.application.repository.InMemoryTicketRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static br.com.fullcycle.hexagonal.infrastructure.models.TicketStatus.PENDING;

class SubscribeCustomerToEventUseCaseTest {

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    public void testReserveTicket() {
        //given
        final var expectedTicketsSize = 1;

        final var aPartner = Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);
        final var aCustomer = Customer.newCustomer("Hygor Doe", "123.456.789-01", "hygor.doe@gmail.com");

        final var customerId = aCustomer.customerId().value();
        final var eventId = anEvent.eventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(customerId, eventId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        customerRepository.create(aCustomer);
        eventRepository.create(anEvent);

        //when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var output = useCase.execute(subscribeInput);

        //then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertNotNull(output.ticketId());
        Assertions.assertNotNull(output.reservationDate());
        Assertions.assertEquals(PENDING.name(), output.ticketStatus());

        final var actualEvent = eventRepository.eventOfId(anEvent.eventId());
        Assertions.assertEquals(expectedTicketsSize, actualEvent.get().allTickets().size());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um cliente que não existe")
    public void testReserveTicketWithoutCustomer() {
        //given
        final var expectedError = "Customer not found";

        final var aPartner = Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);

        final var customerId = CustomerId.unique().value();
        final var eventId = EventId.unique().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(customerId, eventId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        eventRepository.create(anEvent);

        //when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve comprar um ticket de um evento que não existe")
    public void testReserveTicketWithoutEvent() {
        //given
        final var expectedError = "Event not found";

        final var aCustomer = Customer.newCustomer("Hygor Doe", "123.456.789-01", "hygor.doe@gmail.com");

        final var customerId = aCustomer.customerId().value();
        final var eventId = EventId.unique().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(customerId, eventId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        customerRepository.create(aCustomer);

        //when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um mesmo cliente não pode comprar mais de um ticket por evento")
    public void testReserveTicketMoreThanOnce() {
        //given
        final var expectedError = "Email already registered";

        final var aPartner = Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 10, aPartner);
        final var aCustomer = Customer.newCustomer("Hygor Doe", "123.456.789-01", "hygor.doe@gmail.com");

        final var customerId = aCustomer.customerId().value();
        final var eventId = anEvent.eventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(customerId, eventId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var ticket = anEvent.reserveTicket(aCustomer.customerId());

        customerRepository.create(aCustomer);

        eventRepository.create(anEvent);
        ticketRepository.create(ticket);

        //when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Um cliente não pode comprar de um evento que não há mais cadeiras")
    public void testReserveTicketWithoutSlots() {
        //given
        final var expectedError = "Event sold out";

        final var aPartner = Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail.com");
        final var anEvent = Event.newEvent("Disney on Ice", "2021-01-01", 1, aPartner);
        final var aCustomer = Customer.newCustomer("Hygor Doe", "123.456.789-01", "hygor.doe@gmail.com");
        final var aCustomer2 = Customer.newCustomer("Vanessa Doe", "123.456.789-01", "vanessa.doe@gmail.com");

        final var customerId = aCustomer.customerId().value();
        final var eventId = anEvent.eventId().value();

        final var subscribeInput = new SubscribeCustomerToEventUseCase.Input(customerId, eventId);

        final var customerRepository = new InMemoryCustomerRepository();
        final var eventRepository = new InMemoryEventRepository();
        final var ticketRepository = new InMemoryTicketRepository();

        final var ticket = anEvent.reserveTicket(aCustomer2.customerId());

        customerRepository.create(aCustomer);
        customerRepository.create(aCustomer2);
        eventRepository.create(anEvent);
        ticketRepository.create(ticket);

        //when
        final var useCase = new SubscribeCustomerToEventUseCase(customerRepository, eventRepository, ticketRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(subscribeInput));

        //then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

}