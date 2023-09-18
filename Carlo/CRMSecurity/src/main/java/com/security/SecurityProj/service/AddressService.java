package com.security.SecurityProj.service;

import com.security.SecurityProj.entities.Address;
import com.security.SecurityProj.dtos.AddressDTO;
import com.security.SecurityProj.entities.User;
import com.security.SecurityProj.repositories.AddressRepository;
import com.security.SecurityProj.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service

public class AddressService {

    @Autowired
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public void addAddress(AddressDTO addressDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCivNumber(addressDTO.getCivNumber());

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NoSuchElementException("User not found")); // Provide a default if not present
        address.setUser(user);

        addressRepository.save(address);
    }


    public List<Address> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public List<AddressDTO> findAll() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressDTO> dtos = new ArrayList<>();
        for (Address address : addresses) {
            dtos.add(convertToDto(address));
        }
        return dtos;
    }


    private AddressDTO convertToDto(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setStreet(address.getStreet());
        dto.setCivNumber(address.getCivNumber());

        return dto;
    }


}
