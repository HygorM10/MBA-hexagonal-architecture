package br.com.fullcycle.hexagonal.application.usecases.partner;

import br.com.fullcycle.hexagonal.application.domain.partner.Partner;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.application.repository.InMemoryPartnerRepository;
import br.com.fullcycle.hexagonal.application.usecases.parter.CreatePartnerUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        // given
        final var exectedCNPJ = "41.536.538/0001-00";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        // when
        final var partnerRepository = new InMemoryPartnerRepository();
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var output = useCase.execute(createInput);

        // then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(exectedCNPJ, output.cnpj());
        Assertions.assertEquals(exectedEmail, output.email());
        Assertions.assertEquals(exectedName, output.name());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com CNPJ duplicado")
    public void testCreateWithDuplicatedCNPJShouldFail() {
        // given
        final var exectedCNPJ = "41.536.538/0001-00";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.newPartner(exectedName, exectedCNPJ, exectedEmail);

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        // when
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        // given
        final var exectedCNPJ = "41.536.538/0001-00";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var aPartner = Partner.newPartner(exectedName, "41.536.538/0002-00", exectedEmail);

        final var partnerRepository = new InMemoryPartnerRepository();
        partnerRepository.create(aPartner);

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        // when
        final var useCase = new CreatePartnerUseCase(partnerRepository);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

}