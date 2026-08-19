package com.fieldservicemanagement.fieldservicemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fieldservicemanagement.fieldservicemanagement.entity.Site;

public interface SiteRepository extends JpaRepository<Site, Long> {

}