package com.ariary.market;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FxRateRepository extends JpaRepository<FxRate, FxRate.Key> {

	List<FxRate> findByKeyDate(LocalDate date);

	@Modifying
	@Query("delete from FxRate f where f.key.date = :date")
	int deleteByDate(LocalDate date);

}
