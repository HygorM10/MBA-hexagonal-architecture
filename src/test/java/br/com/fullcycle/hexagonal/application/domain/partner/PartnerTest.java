package br.com.fullcycle.hexagonal.application.domain.partner;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PartnerTest {

    @Test
    @DisplayName("Deve instanciar um partner")
    public void testCreatePartner() {
        // given
        final var exectedCnpj = "41.536.538/0001-00";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";

        // when
        final var actualPartner = Partner.newPartner(exectedName, exectedCnpj, exectedEmail);

        // then
        Assertions.assertNotNull(actualPartner.partnerId());
        Assertions.assertEquals(exectedCnpj, actualPartner.cnpj().value());
        Assertions.assertEquals(exectedEmail, actualPartner.email().value());
        Assertions.assertEquals(exectedName, actualPartner.name().value());
    }

    @Test
    @DisplayName("Não deve instanciar um partner com cnpj inválido")
    public void testCreatePartnerWithInvalidCpf() {
        // given
        final var expectedError = "Invalid value for Cnpj";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner("John Doe", "123456.789-01", "john.doe@gmail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um partner com nome inválido")
    public void testCreatePartnerWithInvalidName() {
        // given
        final var expectedError = "Invalid value for Name";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner(null, "41.536.538/0001-00", "john.doe@gmail.com")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

    @Test
    @DisplayName("Não deve instanciar um partner com email inválido")
    public void testCreatePartnerWithInvalidEmail() {
        // given
        final var expectedError = "Invalid value for Email";

        // when
        final var actualError = Assertions.assertThrows(
                ValidationException.class,
                () -> Partner.newPartner("John Doe", "41.536.538/0001-00", "john.doe@gmail")
        );

        // then
        Assertions.assertEquals(expectedError, actualError.getMessage());
    }

}
