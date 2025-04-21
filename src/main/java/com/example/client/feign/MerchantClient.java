package com.example.client.feign;

import com.example.client.dto.MerchantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchantClient", url = "${merchant.service.url}")
public interface MerchantClient {

    @GetMapping("/merchants/{id}")
    MerchantDTO findMerchantById(@PathVariable("id") String id);
}