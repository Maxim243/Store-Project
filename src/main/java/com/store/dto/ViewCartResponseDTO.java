package com.store.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ViewCartResponseDTO(List<ViewCartProductDTO> viewCartProductDTOList,
                                  Double cartSubtotalPrice) {

}


