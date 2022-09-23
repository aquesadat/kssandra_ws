package com.kssandra.ksd_ws.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kssandra.ksd_persistence.domain.Prediction;

@Repository
public interface CryptoPredictionTestH2Repository extends JpaRepository<Prediction, Integer> {

}
