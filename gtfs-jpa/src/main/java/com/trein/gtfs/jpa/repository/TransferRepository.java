package com.trein.gtfs.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trein.gtfs.jpa.entity.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

}
