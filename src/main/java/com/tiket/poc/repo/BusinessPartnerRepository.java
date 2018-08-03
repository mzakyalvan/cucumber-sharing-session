package com.tiket.poc.repo;

import com.tiket.poc.entity.BusinessPartner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BusinessPartnerRepository extends MongoRepository<BusinessPartner, UUID> {
}
