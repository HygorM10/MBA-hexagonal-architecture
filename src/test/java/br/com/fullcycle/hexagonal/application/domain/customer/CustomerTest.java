package br.com.fullcycle.hexagonal.application.domain.customer;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CustomerTest {

    @Test
    @DisplayName("Deve instanciar um cliente")
    public void testCreateCustomer() {
        // given
        final var exectedCpf = "123.456.789-01";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";

        // when
        final var actualCustomer = Customer.newCustomer(exectedName, exectedCpf, exectedEmail);

        // then
        Assertions.assertNotNull(actualCustomer.customerId());
        Assertions.assertEquals(exectedCpf, actualCustomer.cpf().value());
        Assertions.assertEquals(exectedEmail, actualCustomer.email().value());
        Assertions.assertEquals(exectedName, actualCustomer.name().value());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com cpf inválido")
    public void testCreateCustomerWithInvalidCpf() {
        // given
        final var expectedError = "Invalid value for Cpf";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Customer.newCustomer("John Doe", "123456.789-01", "john.doe@gmail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com nome inválido")
    public void testCreateCustomerWithInvalidName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Customer.newCustomer(null, "123.456.789-01", "john.doe@gmail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um cliente com email inválido")
    public void testCreateCustomerWithInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Customer.newCustomer("John Doe", "123.456.789-01", "john.doe@gmail")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

}
