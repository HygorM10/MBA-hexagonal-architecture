package br.com.fullcycle.hexagonal.application.usecases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.services.CustomerService;

import java.util.Objects;
import java.util.Optional;

public class GetCustomerByIdUseCase extends UseCase<GetCustomerByIdUseCase.Input, Optional<GetCustomerByIdUseCase.Output>> {

    private final CustomerService customerService;

    public GetCustomerByIdUseCase(CustomerService customerService) {
        this.customerService = Objects.requireNonNull(customerService);
    }

    @Override
    public Optional<Output> execute(Input input) {
        return customerService.findById(input.id)
                .map(customer -> new Output(customer.getId(), customer.getCpf(), customer.getEmail(), customer.getName()));
    }

    public record Input(Long id) {
    }

    public record Output(Long id, String cpf, String email, String name) {
    }
}
