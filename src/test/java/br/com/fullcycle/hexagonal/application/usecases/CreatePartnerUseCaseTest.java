package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CreatePartnerUseCaseTest {

    @Test
    @DisplayName("Deve criar um parceiro")
    public void testCreatePartner() {
        // given
        final var exectedCNPJ = "41536538000100";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByCnpj(exectedCNPJ)).thenReturn(Optional.empty());
        when(partnerService.findByEmail(exectedEmail)).thenReturn(Optional.empty());
        when(partnerService.save(any())).thenAnswer(a -> {
            var partner = a.getArgument(0, Partner.class);
            partner.setId(UUID.randomUUID().getMostSignificantBits());
            return partner;
        });

        final var useCase = new CreatePartnerUseCase(partnerService);
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
        final var exectedCNPJ = "41536538000100";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        final var aPartner = new Partner();
        aPartner.setId(UUID.randomUUID().getMostSignificantBits());
        aPartner.setCnpj(exectedCNPJ);
        aPartner.setEmail(exectedEmail);
        aPartner.setName(exectedName);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByCnpj(exectedCNPJ)).thenReturn(Optional.of(aPartner));

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

    @Test
    @DisplayName("Não deve cadastrar um parceiro com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() {
        // given
        final var exectedCNPJ = "41536538000100";
        final var exectedEmail = "john.doe@gmail.com";
        final var exectedName = "John Doe";
        final var expectedError = "Partner already exists";

        final var createInput = new CreatePartnerUseCase.Input(exectedCNPJ, exectedEmail, exectedName);

        final var aPartner = new Partner();
        aPartner.setId(UUID.randomUUID().getMostSignificantBits());
        aPartner.setCnpj(exectedCNPJ);
        aPartner.setEmail(exectedEmail);
        aPartner.setName(exectedName);

        // when
        final var partnerService = Mockito.mock(PartnerService.class);
        when(partnerService.findByCnpj(exectedCNPJ)).thenReturn(Optional.empty());
        when(partnerService.findByEmail(exectedEmail)).thenReturn(Optional.of(aPartner));

        final var useCase = new CreatePartnerUseCase(partnerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        // then
        Assertions.assertEquals(expectedError, actualException.getMessage());
    }

}