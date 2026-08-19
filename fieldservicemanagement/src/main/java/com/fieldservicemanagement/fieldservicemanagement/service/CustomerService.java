package com.fieldservicemanagement.fieldservicemanagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerRequestDTO;
import com.fieldservicemanagement.fieldservicemanagement.dto.CustomerResponseDTO;
import com.fieldservicemanagement.fieldservicemanagement.entity.Customer;
import com.fieldservicemanagement.fieldservicemanagement.exception.CustomerNotFoundException;
import com.fieldservicemanagement.fieldservicemanagement.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public CustomerResponseDTO saveCustomer(CustomerRequestDTO requestDTO) {

        Customer customer = new Customer();

        customer.setCustomerName(requestDTO.getCustomerName());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhone(requestDTO.getPhone());
        customer.setCompanyName(requestDTO.getCompanyName());
        customer.setAddress(requestDTO.getAddress());

        Customer savedCustomer = customerRepository.save(customer);

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();

        responseDTO.setId(savedCustomer.getId());
        responseDTO.setCustomerName(savedCustomer.getCustomerName());
        responseDTO.setEmail(savedCustomer.getEmail());
        responseDTO.setPhone(savedCustomer.getPhone());
        responseDTO.setCompanyName(savedCustomer.getCompanyName());
        responseDTO.setAddress(savedCustomer.getAddress());

        return responseDTO;
    }

    public List<CustomerResponseDTO> getAllCustomers() {

    return customerRepository.findAll()
            .stream()
            .map(customer -> {

                CustomerResponseDTO responseDTO = new CustomerResponseDTO();

                responseDTO.setId(customer.getId());
                responseDTO.setCustomerName(customer.getCustomerName());
                responseDTO.setEmail(customer.getEmail());
                responseDTO.setPhone(customer.getPhone());
                responseDTO.setCompanyName(customer.getCompanyName());
                responseDTO.setAddress(customer.getAddress());

                return responseDTO;
            })
            .toList();
}

    public CustomerResponseDTO getCustomerById(Long id) {

    Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new CustomerNotFoundException(
                    "Customer not found with id: " + id));

    CustomerResponseDTO responseDTO = new CustomerResponseDTO();

    responseDTO.setId(customer.getId());
    responseDTO.setCustomerName(customer.getCustomerName());
    responseDTO.setEmail(customer.getEmail());
    responseDTO.setPhone(customer.getPhone());
    responseDTO.setCompanyName(customer.getCompanyName());
    responseDTO.setAddress(customer.getAddress());

    return responseDTO;
}
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + id));

        existingCustomer.setCustomerName(requestDTO.getCustomerName());
        existingCustomer.setEmail(requestDTO.getEmail());
        existingCustomer.setPhone(requestDTO.getPhone());
        existingCustomer.setCompanyName(requestDTO.getCompanyName());
        existingCustomer.setAddress(requestDTO.getAddress());

        Customer updatedCustomer = customerRepository.save(existingCustomer);

        CustomerResponseDTO responseDTO = new CustomerResponseDTO();

        responseDTO.setId(updatedCustomer.getId());
        responseDTO.setCustomerName(updatedCustomer.getCustomerName());
        responseDTO.setEmail(updatedCustomer.getEmail());
        responseDTO.setPhone(updatedCustomer.getPhone());
        responseDTO.setCompanyName(updatedCustomer.getCompanyName());
        responseDTO.setAddress(updatedCustomer.getAddress());

        return responseDTO;
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

}