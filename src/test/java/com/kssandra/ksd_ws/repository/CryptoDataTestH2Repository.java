package com.kssandra.ksd_ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kssandra.ksd_persistence.domain.CryptoData;

@Repository
public interface CryptoDataTestH2Repository extends JpaRepository<CryptoData, Integer> {

}
