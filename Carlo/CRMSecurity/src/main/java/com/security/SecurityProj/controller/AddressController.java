package com.security.SecurityProj.controller;
import com.security.SecurityProj.dtos.AddressDTO;
import com.security.SecurityProj.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody AddressDTO addressDTO){
        service.addAddress(addressDTO);
    }

    @GetMapping("/address")
    public List<AddressDTO> getAddressesByUserId() {
       return service.findAll();
    }

}
