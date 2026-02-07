package com.cottondealer.app.service;

import com.cottondealer.app.model.GinSale;
import com.cottondealer.app.repository.GinSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GinService {
    @Autowired
    private GinSaleRepository ginSaleRepository;

    public java.util.List<GinSale> getAllSales() {
        return ginSaleRepository.findAll();
    }

    public java.util.List<GinSale> searchSales(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return ginSaleRepository.findByGinNameContainingIgnoreCase(keyword);
        }
        return ginSaleRepository.findAll();
    }

    public GinSale saveSale(GinSale sale) {
        if (sale.getTotalPrice() == null && sale.getQuantityKg() != null && sale.getSellPricePerKg() != null) {
            sale.setTotalPrice(sale.getQuantityKg() * sale.getSellPricePerKg());
        }
        return ginSaleRepository.save(sale);
    }
}
