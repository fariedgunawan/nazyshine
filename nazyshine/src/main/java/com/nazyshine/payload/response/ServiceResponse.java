package com.nazyshine.payload.response;

import java.math.BigDecimal;
import com.nazyshine.model.Service;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;

    public ServiceResponse(Service service) {
        this.id = service.getId();
        this.name = service.getName();
        this.price = service.getPrice();
        this.description = service.getDescription();
    }
}