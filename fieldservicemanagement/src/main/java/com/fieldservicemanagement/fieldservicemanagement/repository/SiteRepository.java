package com.fieldservicemanagement.fieldservicemanagement.repository;

import com.fieldservicemanagement.fieldservicemanagement.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {

    List<Site> findByCustomerId(Long customerId);
}