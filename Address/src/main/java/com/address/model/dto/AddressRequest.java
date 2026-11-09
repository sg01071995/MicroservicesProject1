package com.address.model.dto;

import java.util.List;

public class AddressRequest {
    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public List<AddressRequestDto> getAddressRequestDtoList() {
        return addressRequestDtoList;
    }

    public void setAddressRequestDtoList(List<AddressRequestDto> addressRequestDtoList) {
        this.addressRequestDtoList = addressRequestDtoList;
    }

    private Long empId;
    private List<AddressRequestDto> addressRequestDtoList;
}
