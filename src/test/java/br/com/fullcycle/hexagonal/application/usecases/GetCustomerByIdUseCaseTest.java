package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

class GetCustomerByIdUseCaseTest {

    @Test
    @DisplayName("Deve obter um cliente por id")
    public void testGetById() {
        // given
        final var exectedName = "12345678901";
        final var exectedCpf = "john.doe@gmail.com";
        final var exectedEmail = "John Doe";
        final var expectedId = UUID.randomUUID().getMostSignificantBits();

        final var aCustomer = new Customer();
        aCustomer.setId(expectedId);
        aCustomer.setCpf(exectedCpf);
        aCustomer.setEmail(exectedEmail);
        aCustomer.setName(exectedName);

        final var input = new GetCustomerByIdUseCase.Input(expectedId);

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        when(customerService.findById(expectedId)).thenReturn(Optional.of(aCustomer));

        final var useCase = new GetCustomerByIdUseCase(customerService);
        final var output = useCase.execute(input).get();

        // then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(exectedCpf, output.cpf());
        Assertions.assertEquals(exectedEmail, output.email());
        Assertions.assertEquals(exectedName, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um cliente não existente por id")
    public void testGetByIdWithInvalidId() {
        // given
        final var expectedId = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetCustomerByIdUseCase.Input(expectedId);

        // when
        final var customerService = Mockito.mock(CustomerService.class);
        when(customerService.findById(expectedId)).thenReturn(Optional.empty());

        final var useCase = new GetCustomerByIdUseCase(customerService);
        final var output = useCase.execute(input);

        // then
        Assertions.assertTrue(output.isEmpty());
    }

}