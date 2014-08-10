package com.trein.gtfs.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trein.gtfs.jpa.entities.Stop;

public interface StopRepository extends JpaRepository<Stop, Long> {

    Stop findByStopId(String parentStation);

}