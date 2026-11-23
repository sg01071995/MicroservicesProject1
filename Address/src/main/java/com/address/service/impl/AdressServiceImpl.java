package com.address.service.impl;

import com.address.exception.ResourceNotFoundException;
import com.address.model.dto.AddressDto;
import com.address.model.dto.AddressRequest;
import com.address.model.dto.AddressRequestDto;
import com.address.model.entity.Address;
import com.address.repository.AddressRepository;
import com.address.service.AddressService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AdressServiceImpl implements AddressService {

    Logger log = LoggerFactory.getLogger(AdressServiceImpl.class);
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public AdressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<AddressDto> saveAddress(AddressRequest addressRequest) {
        //TODO: Implement the logic to save the address using addressRepository and return the saved AddressDto, Check if the empId exists in the employee service before saving the address. If not, throw an exception.

        List<Address> listToSave = saveOrUpdateAddress(addressRequest);
        // Save all addresses and return the saved AddressDto
        List<Address> savedAddresses = addressRepository.saveAll(listToSave);
        return savedAddresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public List<AddressDto> updateAddress(AddressRequest addressRequest) {

        List<Address> addressByEmpId = addressRepository.findByEmpId(addressRequest.getEmpId());
        if(addressByEmpId.isEmpty()){
            log.info("No address found for emp id {}", addressRequest.getEmpId());
            log.info("Creating new address for emp id {}", addressRequest.getEmpId());
        }
        List<Address> listToUpdate = saveOrUpdateAddress(addressRequest);
        List<Long> upcomingNonNullIDs = listToUpdate.stream().map(Address::getId).filter(Objects::nonNull).toList();
        List<Long> existingIDs = addressByEmpId.stream().map(Address::getId).toList();

        List<Long> idsToDelete = existingIDs.stream().filter(id -> !upcomingNonNullIDs.contains(id)).toList();
        if(idsToDelete.isEmpty()){
            log.info("No address to delete for emp id {}", addressRequest.getEmpId());
        } else {
            log.info("Deleting addresses with ids {} for emp id {}", idsToDelete, addressRequest.getEmpId());
            addressRepository.deleteAllById(idsToDelete);
        }
        List<Address> updatedAddresses = addressRepository.saveAll(listToUpdate);
        return updatedAddresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public AddressDto getSingleAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found for id: " + id));
        return modelMapper.map(address, AddressDto.class);
    }

    @Override
    public List<AddressDto> getAllAddress() {
        List<Address> addresses = addressRepository.findAll();
        if(addresses.isEmpty()){
            throw new ResourceNotFoundException("No address found");
        }
        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressDto.class))
                .toList();
    }

    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found for id: " + id));
        addressRepository.delete(address);
    }

    private List<Address> saveOrUpdateAddress(AddressRequest addressRequest) {
        List<Address> listToSave = new ArrayList<>();
        for (AddressRequestDto addressRequestDto : addressRequest.getAddressRequestDtoList()) {
            Address address = new Address();
            if (addressRequestDto.getId() != null) {
                address.setId(addressRequestDto.getId());
            }
//            address.setId(addressRequestDto.getId() != null ? addressRequestDto.getId() : null);
            address.setStreet(addressRequestDto.getStreet());
            address.setPinCode(addressRequestDto.getPinCode());
            address.setCity(addressRequestDto.getCity());
            address.setCountry(addressRequestDto.getCountry());
            address.setAddressType(addressRequestDto.getAddressType());
            address.setEmpId(addressRequest.getEmpId());
            listToSave.add(address);
        }
        return listToSave;
    }
}
