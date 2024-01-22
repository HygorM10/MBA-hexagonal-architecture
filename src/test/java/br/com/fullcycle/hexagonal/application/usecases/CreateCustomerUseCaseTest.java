package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CreateCustomerUseCaseTest {

    @Test
    @DisplayName("Deve criar um cliente")
    public void testCreateCustomer() {
        // given
        final var exectedName = "12345678901";
        final var exectedCpf = "john.doe@gmail.com";
        final var exectedEmail = "John Doe";

        final var createInput = new CreateCustomerUseCase.Input(exectedCpf, exectedEmail, exectedName);

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        when(customerService.findByCpf(exectedCpf)).thenReturn(Optional.empty());
        when(customerService.findByEmail(exectedEmail)).thenReturn(Optional.empty());
        when(customerService.save(any())).thenAnswer(a -> {
            var customer = a.getArgument(0, Customer.class);
            customer.setId(UUID.randomUUID().getMostSignificantBits());
            return customer;
        });

        final var useCase = new CreateCustomerUseCase(customerService);
        final var output = useCase.execute(createInput);

        // then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(exectedCpf, output.cpf());
        Assertions.assertEquals(exectedEmail, output.email());
        Assertions.assertEquals(exectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() {
        // given
        final var exectedName = "12345678901";
        final var exectedCpf = "john.doe@gmail.com";
        final var exectedEmail = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(exectedCpf, exectedEmail, exectedName);

        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(exectedCpf);
        aCustomer.setEmail(exectedEmail);
        aCustomer.setName(exectedName);

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        when(customerService.findByCpf(exectedCpf)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        // given
        final var exectedName = "12345678901";
        final var exectedCpf = "john.doe@gmail.com";
        final var exectedEmail = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(exectedCpf, exectedEmail, exectedName);

        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(exectedCpf);
        aCustomer.setEmail(exectedEmail);
        aCustomer.setName(exectedName);

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        when(customerService.findByCpf(exectedCpf)).thenReturn(Optional.empty());
        when(customerService.findByEmail(exectedEmail)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

}
